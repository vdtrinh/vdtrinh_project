package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.PatientForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.enums.BloodType;
import edu.ncsu.csc.iTrust2.models.enums.Ethnicity;
import edu.ncsu.csc.iTrust2.models.enums.Gender;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.models.enums.VaccinationStatus;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

/**
 * Test for API functionality for interacting with Patients
 *
 * @author Kai Presler-Marshall
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIPatientTest {

    @Autowired
    private MockMvc          mvc;

    @Autowired
    private PatientService   service;

    @Autowired
    private PersonnelService personnelService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {

        service.deleteAll();

        personnelService.deleteAll();
    }

    /**
     * Tests that getting a patient that doesn't exist returns the proper
     * status.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetNonExistentPatient () throws Exception {
        mvc.perform( get( "/api/v1/patients/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests PatientAPI
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    @Transactional
    public void testPatientAPI () throws Exception {

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setUsername( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        patient.setVaccinationStatus( VaccinationStatus.FIRST_DOSE.toString() );

        // Editing the patient before they exist should fail
        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isNotFound() );

        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( antti );

        // Creating a User should create the Patient record automatically
        mvc.perform( get( "/api/v1/patients/antti" ) ).andExpect( status().isOk() );

        // Should also now be able to edit existing record with new information
        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() );

        Patient anttiRetrieved = (Patient) service.findByName( "antti" );

        // Test a few fields to be reasonably confident things are working
        Assertions.assertEquals( "Walhelm", anttiRetrieved.getLastName() );
        Assertions.assertEquals( Gender.Male, anttiRetrieved.getGender() );
        Assertions.assertEquals( "Viipuri", anttiRetrieved.getCity() );

        // Also test a field we haven't set yet
        Assertions.assertNull( anttiRetrieved.getPreferredName() );

        patient.setPreferredName( "Antti Walhelm" );

        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() );

        anttiRetrieved = (Patient) service.findByName( "antti" );

        Assertions.assertNotNull( anttiRetrieved.getPreferredName() );

        // Editing with the wrong username should fail.
        mvc.perform( put( "/api/v1/patients/badusername" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().is4xxClientError() );

    }

    /**
     * Test accessing the patient PUT request unauthenticated
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testPatientUnauthenticated () throws Exception {
        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setUsername( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        patient.setVaccinationStatus( VaccinationStatus.FIRST_DOSE.toString() );

        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( antti );

        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isUnauthorized() );
    }

    /**
     * Test accessing the patient PUT request as a patient
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "antti", roles = { "PATIENT" } )
    public void testPatientAsPatient () throws Exception {
        final Patient antti = new Patient( new UserForm( "antti", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( antti );

        final PatientForm patient = new PatientForm();
        patient.setAddress1( "1 Test Street" );
        patient.setAddress2( "Some Location" );
        patient.setBloodType( BloodType.APos.toString() );
        patient.setCity( "Viipuri" );
        patient.setDateOfBirth( "1977-06-15" );
        patient.setEmail( "antti@itrust.fi" );
        patient.setEthnicity( Ethnicity.Caucasian.toString() );
        patient.setFirstName( "Antti" );
        patient.setGender( Gender.Male.toString() );
        patient.setLastName( "Walhelm" );
        patient.setPhone( "123-456-7890" );
        patient.setUsername( "antti" );
        patient.setState( State.NC.toString() );
        patient.setZip( "27514" );
        patient.setVaccinationStatus( VaccinationStatus.FULLY_VACCINATED.toString() );

        // a patient can edit their own info
        mvc.perform( put( "/api/v1/patients/antti" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isOk() );

        final Patient anttiRetrieved = (Patient) service.findByName( "antti" );

        // Test a few fields to be reasonably confident things are working
        Assertions.assertEquals( "Walhelm", anttiRetrieved.getLastName() );
        Assertions.assertEquals( Gender.Male, anttiRetrieved.getGender() );
        Assertions.assertEquals( "Viipuri", anttiRetrieved.getCity() );

        // Also test a field we haven't set yet
        Assertions.assertNull( anttiRetrieved.getPreferredName() );

        // but they can't edit someone else's
        patient.setUsername( "patient" );
        mvc.perform( put( "/api/v1/patients/patient" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( patient ) ) ).andExpect( status().isUnauthorized() );

        // we should be able to update our record too
        mvc.perform( get( "/api/v1/patient/" ) ).andExpect( status().isOk() );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testAssociatePatientsToPatientAdvocate () throws Exception {
        final PatientAdvocate pa1 = new PatientAdvocate(
                new UserForm( "pa_test1", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        final PatientAdvocate pa2 = new PatientAdvocate(
                new UserForm( "pa_test2", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        final Patient p = new Patient( new UserForm( "p_test1", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( p );
        Assertions.assertEquals( service.count(), 1 );

        personnelService.save( pa1 );
        Assertions.assertEquals( personnelService.count(), 1 );

        mvc.perform( post( "/api/v1/patient/p_test1/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pa1 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final Patient p1 = (Patient) service.findByName( p.getUsername() );

        Assertions.assertEquals( p1.getPatientAdvocates().size(), 1 );

        personnelService.save( pa2 );
        Assertions.assertEquals( personnelService.count(), 2 );

        mvc.perform( post( "/api/v1/patient/p_test1/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pa2 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final Patient p2 = (Patient) service.findByName( p.getUsername() );

        Assertions.assertEquals( p2.getPatientAdvocates().size(), 2 );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUnassociatePatientsToPatientAdvocate () throws Exception {
        final PatientAdvocate pa1 = new PatientAdvocate(
                new UserForm( "pa_test1", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        final PatientAdvocate pa2 = new PatientAdvocate(
                new UserForm( "pa_test2", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        final Patient p = new Patient( new UserForm( "p_test1", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( p );
        Assertions.assertEquals( service.count(), 1 );

        personnelService.save( pa1 );
        Assertions.assertEquals( personnelService.count(), 1 );

        mvc.perform( post( "/api/v1/patient/p_test1/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pa1 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final Patient p1 = (Patient) service.findByName( p.getUsername() );

        Assertions.assertEquals( p1.getPatientAdvocates().size(), 1 );

        personnelService.save( pa2 );
        Assertions.assertEquals( personnelService.count(), 2 );

        mvc.perform( post( "/api/v1/patient/p_test1/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pa2 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        Assertions.assertEquals( p1.getPatientAdvocates().size(), 2 );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/patient/p_test1/" + pa1.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        final Patient p2 = (Patient) service.findByName( p.getUsername() );

        Assertions.assertEquals( p2.getPatientAdvocates().size(), 1 );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/patient/p_test1/" + pa2.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( p2.getPatientAdvocates().size(), 0 );

    }

}
