package edu.ncsu.csc.iTrust2.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.PersonnelForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

/**
 * Test for API functionality for interacting with Personnel
 *
 * @author Kai Presler-Marshall
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIPersonnelTest {

    @Autowired
    private MockMvc                 mvc;

    @Autowired
    private PersonnelService        service;

    @Autowired
    private PatientService<Patient> pService;

    /**
     * Sets up test
     */
    @BeforeEach
    public void setup () {

        service.deleteAll();

        pService.deleteAll();
    }

    /**
     * Tests getting a non existent personnel and ensures that the correct
     * status is returned.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testGetNonExistentPersonnel () throws Exception {
        mvc.perform( get( "/api/v1/personnel/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests PersonnelAPI
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testPersonnelAPI () throws Exception {

        final Personnel hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        service.save( hcp );

        final PersonnelForm personnel = new PersonnelForm();

        personnel.setAddress1( "1 Test Street" );
        personnel.setAddress2( "Address Part 2" );
        personnel.setCity( "Prague" );
        personnel.setEmail( "hcp@itrust.cz" );
        personnel.setFirstName( "Test" );
        personnel.setLastName( "HCP" );
        personnel.setPhone( "123-456-7890" );
        personnel.setUsername( "hcp" );
        personnel.setState( "NC" );
        personnel.setZip( "27514" );

        // Should be able to update with the new values
        mvc.perform( put( "/api/v1/personnel/hcp" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) ).andExpect( status().isOk() );

        final Personnel retrieved = (Personnel) service.findByName( "hcp" );

        Assertions.assertEquals( "Prague", retrieved.getCity() );
        Assertions.assertEquals( State.NC, retrieved.getState() );

        mvc.perform( get( "/api/v1/personnel" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        mvc.perform( get( "/api/v1/personnel/hcp" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        // Edit with wrong url ID should fail
        mvc.perform( put( "/api/v1/personnel/badhcp" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) ).andExpect( status().isNotFound() );

        // Edit with matching, but nonexistent ID should fail.
        personnel.setUsername( "badhcp" );
        mvc.perform( put( "/api/v1/personnel/badhcp" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( personnel ) ) ).andExpect( status().is4xxClientError() );

    }

    /**
     * Tests getting personnel by their roles.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "ADMIN" } )
    public void testGetByRole () throws Exception {
        // Valid get requests
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_LABTECH" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_ER" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_HCP" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_OD" ) ).andExpect( status().isOk() );
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_OPH" ) ).andExpect( status().isOk() );

        // Invalid get request
        mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_SCHMOO" ) ).andExpect( status().is4xxClientError() );
    }

    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    public void testRoleFiltering () throws Exception {

        final Personnel hcp = new Personnel( new UserForm( "hcp_test1", "123456", Role.ROLE_HCP, 1 ) );

        final Personnel hcp2 = new Personnel( new UserForm( "hcp_test2", "123456", Role.ROLE_HCP, 1 ) );
        hcp2.addRole( Role.ROLE_ER );

        final Personnel admin = new Personnel( new UserForm( "admin_test", "123456", Role.ROLE_ADMIN, 1 ) );

        service.saveAll( List.of( hcp, hcp2, admin ) );

        final MvcResult result = mvc.perform( get( "/api/v1/personnel/getbyroles/ROLE_HCP" ) )
                .andExpect( status().isOk() ).andReturn();

        final String content = result.getResponse().getContentAsString();

        Assertions.assertTrue( content.contains( "hcp_test1" ) );
        Assertions.assertTrue( content.contains( "hcp_test2" ) );
        Assertions.assertFalse( content.contains( "admin_test" ) );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testAssociatePatientsToPatientAdvocate () throws Exception {
        final PatientAdvocate pa = new PatientAdvocate(
                new UserForm( "pa_test1", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        final PatientAdvocate pa2 = new PatientAdvocate(
                new UserForm( "pa_test2", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        final Patient p1 = new Patient( new UserForm( "p_test1", "123456", Role.ROLE_PATIENT, 1 ) );
        final Patient p2 = new Patient( new UserForm( "p_test2", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( pa );
        Assertions.assertEquals( service.count(), 1 );

        service.save( pa2 );
        Assertions.assertEquals( service.count(), 2 );

        pService.save( p1 );
        assertEquals( pService.count(), 1 );

        mvc.perform( post( "/api/v1/personnel/pa_test1/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p1 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        mvc.perform( post( "/api/v1/personnel/pa_test2/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p1 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final PatientAdvocate pa1 = (PatientAdvocate) service.findByName( pa.getUsername() );

        final PatientAdvocate pa2Db = (PatientAdvocate) service.findByName( pa2.getUsername() );

        Assertions.assertEquals( pa1.getPatients().size(), 1 );

        Assertions.assertEquals( pa2Db.getPatients().size(), 1 );

        pService.save( p2 );
        assertEquals( pService.count(), 2 );

        mvc.perform( post( "/api/v1/personnel/pa_test1/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p2 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        Assertions.assertEquals( pa1.getPatients().size(), 2 );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUnassignPatientsToPatientAdvocate () throws Exception {
        final PatientAdvocate pa = new PatientAdvocate(
                new UserForm( "pa_test1", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        final Patient p1 = new Patient( new UserForm( "p_test1", "123456", Role.ROLE_PATIENT, 1 ) );
        final Patient p2 = new Patient( new UserForm( "p_test2", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( pa );
        Assertions.assertEquals( service.count(), 1 );

        pService.save( p1 );
        assertEquals( pService.count(), 1 );

        mvc.perform( post( "/api/v1/personnel/pa_test1/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p1 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final PatientAdvocate pa1 = (PatientAdvocate) service.findByName( pa.getUsername() );

        Assertions.assertEquals( pa1.getPatients().size(), 1 );

        pService.save( p2 );
        assertEquals( pService.count(), 2 );

        mvc.perform( post( "/api/v1/personnel/pa_test1/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p2 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        Assertions.assertEquals( pa1.getPatients().size(), 2 );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/personnel/pa_test1/" + p1.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        final PatientAdvocate pa3 = (PatientAdvocate) service.findByName( pa.getUsername() );

        Assertions.assertEquals( pa3.getPatients().size(), 1 );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/personnel/pa_test1/" + p2.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( pa3.getPatients().size(), 0 );

    }

    /**
     * This will test the permission API to set the view permission
     *
     * @throws Exception
     *             when there is an exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testPermission () throws Exception {
        final PatientAdvocate pa = new PatientAdvocate(
                new UserForm( "pa_test1", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        final Patient p1 = new Patient( new UserForm( "p_test1", "123456", Role.ROLE_PATIENT, 1 ) );

        service.save( pa );
        Assertions.assertEquals( service.count(), 1 );

        pService.save( p1 );
        assertEquals( pService.count(), 1 );

        mvc.perform( post( "/api/v1/personnel/pa_test1" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p1 ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final PatientAdvocate pa1 = (PatientAdvocate) service.findByName( pa.getUsername() );

        Assertions.assertEquals( pa1.getPatients().size(), 1 );

        final boolean[] permission = { true, false, true };

        mvc.perform( put( "/api/v1/personnel/pa_test1/p_test1" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( permission ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andDo( print() );

        Assertions.assertTrue( pa1.getPatients().get( p1.getUsername() )[0] );
        Assertions.assertFalse( pa1.getPatients().get( p1.getUsername() )[1] );
        Assertions.assertTrue( pa1.getPatients().get( p1.getUsername() )[2] );

        mvc.perform( put( "/api/v1/personnel/pa_test1/p_test2" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( permission ) ) )
                .andExpect( MockMvcResultMatchers.status().isBadRequest() ).andDo( print() );

    }

    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUpdateAndViewPatientAdvocateObject () throws Exception {
        final PatientAdvocate pa = new PatientAdvocate( new UserForm( "pa", "123456", Role.ROLE_PATIENTADVOCATE, 11 ) );

        service.save( pa );

        Assertions.assertEquals( 1, service.count() );

        final UserForm paDemo = new UserForm();

        paDemo.setAddress1( "1 Test Street" );
        paDemo.setAddress2( "Address Part 2" );
        paDemo.setCity( "Prague" );
        paDemo.setEmail( "pa@itrust.cz" );
        paDemo.setFirstName( "Test" );
        paDemo.setLastName( "PA" );
        paDemo.setPhone( "123-456-7890" );
        paDemo.setUsername( "pa" );
        paDemo.setState( "NC" );
        paDemo.setZip( "27514" );
        paDemo.setNickname( "Tomato" );
        paDemo.setPassword( "123456" );

        mvc.perform( put( "/api/v1/patientadvocate/pa" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( paDemo ) ) ).andExpect( status().isOk() );

        final PatientAdvocate retrieved = (PatientAdvocate) service.findByName( "pa" );

        Assertions.assertEquals( "Tomato", retrieved.getNickname() );
        Assertions.assertEquals( "Prague", retrieved.getCity() );
        Assertions.assertEquals( "123456", retrieved.getPassword() );

        mvc.perform( get( "/api/v1/personnel/pa" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );
    }

}
