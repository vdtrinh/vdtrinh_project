/**
 *
 */
package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.DrugForm;
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.PrescriptionForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Hospital;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.Prescription;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.HospitalService;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;
import edu.ncsu.csc.iTrust2.services.PrescriptionService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * @author sarah
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPatientAdvocateTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    @Autowired
    private MockMvc             mvc;
    /** BillService service */
    @Autowired
    private BillService         billService;
    /** CPTCodeService service */
    @Autowired
    private CPTCodeService      cptService;
    /** UserService service */
    @Autowired
    UserService                 userService;
    /** PersonnelService service */
    @Autowired
    PersonnelService            personnelService;
    /** Drug form for a prescription */
    private DrugForm            drugForm;

    /** OfficeVisitService service */
    @Autowired
    private OfficeVisitService  officeVisitService;
    /** Prescription service */
    @Autowired
    private PrescriptionService prescriptionService;
    /** Hospital service */
    @Autowired
    private HospitalService     hospitalService;

    /**
     * Setup before for each test
     *
     * @throws Exception
     *             if exception
     */
    @BeforeEach
    @WithMockUser ( username = "hcp", roles = { "USER", "HCP", "ADMIN" } )
    public void setup () throws Exception {
        userService.deleteAll();
        billService.deleteAll();
        officeVisitService.deleteAll();
        personnelService.deleteAll();
        billService.deleteAll();
        cptService.deleteAll();
        prescriptionService.deleteAll();

        Assertions.assertEquals( 0, officeVisitService.count() );

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new PatientAdvocate( new UserForm( "hcp", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );
        hcp.addRole( Role.ROLE_HCP );

        userService.save( hcp );

        userService.save( patient );

        final Hospital hosp = new Hospital();
        hosp.setAddress( "123 Raleigh Road" );
        hosp.setState( State.NC );
        hosp.setZip( "27514" );
        hosp.setName( "iTrust Test Hospital 2" );

        hospitalService.save( hosp );

    }

    /**
     * Tests office visit APIs for patient advocates to view patient office
     * visits.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "PATIENTADVOCATE", "ADMIN", "HCP" } )
    @Transactional
    public void testPAGetsOfficeVisits () throws Exception {

        final Patient p = (Patient) userService.findByName( "patient" );

        // create office visit form
        final OfficeVisitForm ov = new OfficeVisitForm();
        ov.setDate( "2030-11-19T04:50:00.000-05:00" );
        ov.setHcp( "hcp" );
        ov.setPatient( "patient" );
        ov.setNotes( "Test office visit" );
        ov.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        ov.setHospital( "iTrust Test Hospital 2" );
        final OfficeVisitForm ov2 = new OfficeVisitForm();
        ov2.setDate( "2030-11-19T04:50:00.000-05:00" );
        ov2.setHcp( "hcp" );
        ov2.setPatient( "patient" );
        ov2.setNotes( "Test office visit" );
        ov2.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        ov2.setHospital( "iTrust Test Hospital 2" );
        // create CPTCode for ov
        final CPTCode c = new CPTCode();
        c.setCode( 55555 );
        c.setCost( 5 );
        c.setDescription( "asd" );
        c.setisActive( true );
        c.setVersion( 1 );

        final CPTCode c2 = new CPTCode();
        c2.setCode( 55556 );
        c2.setCost( 5 );
        c2.setDescription( "asd" );
        c2.setisActive( true );
        c2.setVersion( 1 );
        cptService.save( c );
        cptService.save( c2 );
        final List<Long> li = new ArrayList<Long>();
        final List<Long> li2 = new ArrayList<Long>();
        li.add( cptService.findAll().get( 0 ).getId() );
        li2.add( cptService.findAll().get( 1 ).getId() );
        ov.setCptCodes( li );
        ov2.setCptCodes( li2 );

        mvc.perform( post( "/api/v1/officevisits" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ov ) ) ).andExpect( status().isOk() );

        assertEquals( 1, officeVisitService.count() );

        mvc.perform( post( "/api/v1/officevisits" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ov2 ) ) ).andExpect( status().isOk() );

        assertEquals( 2, officeVisitService.count() );
        assertEquals( 2, officeVisitService.findByPatient( p ).size() );

        // testing getting a list of office visits of a patient

        mvc.perform( post( "/api/v1/personnel/hcp" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final PatientAdvocate pa = (PatientAdvocate) personnelService.findByName( "hcp" );

        assertEquals( 1, pa.getPatients().size() );

        String result = mvc.perform( get( "/api/v1/officevisits/hcp/patient" ) )
                .andExpect( MockMvcResultMatchers.status().isBadRequest() ).andDo( print() ).andReturn().getResponse()
                .getContentAsString();

        final boolean[] permission = { false, false, true };

        mvc.perform( put( "/api/v1/personnel/hcp/patient" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( permission ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        assertFalse( pa.getPatients().get( p.getUsername() )[0] );
        assertFalse( pa.getPatients().get( p.getUsername() )[1] );
        assertTrue( pa.getPatients().get( p.getUsername() )[2] );

        result = mvc.perform( get( "/api/v1/officevisits/hcp/patient" ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andDo( print() ).andReturn().getResponse()
                .getContentAsString();

        final List<OfficeVisit> officeVisits = TestUtils.gson().fromJson( result, new TypeToken<List<OfficeVisit>>() {
        }.getType() );

        assertTrue( officeVisits.size() == 2 );

    }

    /**
     * Tests prescription APIs for patient advocates to view patient bills.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "ADMIN", "PATIENTADVOCATE", "HCP" } )
    @Transactional
    public void testPAGetsBills () throws Exception {

        assertEquals( 0, billService.count() );

        // create office visit form
        final OfficeVisitForm ov = new OfficeVisitForm();
        ov.setDate( "2030-11-19T04:50:00.000-05:00" );
        ov.setHcp( "hcp" );
        ov.setPatient( "patient" );
        ov.setNotes( "Test office visit" );
        ov.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        ov.setHospital( "iTrust Test Hospital 2" );
        final OfficeVisitForm ov2 = new OfficeVisitForm();
        ov2.setDate( "2030-11-19T04:50:00.000-05:00" );
        ov2.setHcp( "hcp" );
        ov2.setPatient( "patient" );
        ov2.setNotes( "Test office visit" );
        ov2.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        ov2.setHospital( "iTrust Test Hospital 2" );
        // create CPTCode for ov
        final CPTCode c = new CPTCode();
        c.setCode( 55555 );
        c.setCost( 5 );
        c.setDescription( "asd" );
        c.setisActive( true );
        c.setVersion( 1 );

        final CPTCode c2 = new CPTCode();
        c2.setCode( 55556 );
        c2.setCost( 5 );
        c2.setDescription( "asd" );
        c2.setisActive( true );
        c2.setVersion( 1 );
        cptService.save( c );
        cptService.save( c2 );
        final List<Long> li = new ArrayList<Long>();
        final List<Long> li2 = new ArrayList<Long>();
        li.add( cptService.findAll().get( 0 ).getId() );
        li2.add( cptService.findAll().get( 1 ).getId() );
        ov.setCptCodes( li );
        ov2.setCptCodes( li2 );
        // call endpoint
        mvc.perform( post( "/api/v1/bills/ov" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ov ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/bills/ov" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( ov2 ) ) ).andExpect( status().isOk() );

        final Patient p = (Patient) userService.findByName( "patient" );
        userService.save( p );

        mvc.perform( post( "/api/v1/personnel/hcp" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final PatientAdvocate pa = (PatientAdvocate) personnelService.findByName( "hcp" );

        assertEquals( 1, pa.getPatients().size() );

        String result = mvc.perform( get( "/api/v1/bills/patient" ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() ).andReturn().getResponse().getContentAsString();

        assertEquals( 0, result.length() );

        final boolean[] permission = { true, false, false };

        mvc.perform( put( "/api/v1/personnel/hcp/patient" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( permission ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        assertTrue( pa.getPatients().get( p.getUsername() )[0] );
        assertFalse( pa.getPatients().get( p.getUsername() )[1] );
        assertFalse( pa.getPatients().get( p.getUsername() )[2] );

        result = mvc.perform( get( "/api/v1/bills/patient" ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() ).andReturn().getResponse().getContentAsString();

        final List<Bill> bills = TestUtils.gson().fromJson( result, new TypeToken<List<Bill>>() {
        }.getType() );

        assertTrue( bills.size() == 2 );

    }

    /**
     * Tests prescription APIs for patient advocates to view patient
     * prescriptions.
     *
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "PATIENTADVOCATE", "ADMIN", "HCP" } )
    @Transactional
    public void testPAGetsPrescription () throws Exception {
        assertEquals( 0, prescriptionService.count() );

        // Create drug for testing
        drugForm = new DrugForm();
        drugForm.setCode( "0000-0000-20" );
        drugForm.setName( "TEST" );
        drugForm.setDescription( "DESC" );
        mvc.perform( post( "/api/v1/drugs" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( drugForm ) ) );

        // Create two prescription forms for testing
        final PrescriptionForm form1 = new PrescriptionForm();
        form1.setDrug( drugForm.getCode() );
        form1.setDosage( 100 );
        form1.setRenewals( 12 );
        form1.setPatient( "patient" );
        form1.setStartDate( "2009-10-10" ); // 10/10/2009
        form1.setEndDate( "2010-10-10" ); // 10/10/2010

        final PrescriptionForm form2 = new PrescriptionForm();
        form2.setDrug( drugForm.getCode() );
        form2.setDosage( 200 );
        form2.setRenewals( 3 );
        form2.setPatient( "patient" );
        form2.setStartDate( "2020-10-10" ); // 10/10/2020
        form2.setEndDate( "2020-11-10" ); // 11/10/2020

        mvc.perform( post( "/api/v1/prescriptions" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/prescriptions" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form2 ) ) ).andExpect( status().isOk() );

        final Patient p = (Patient) userService.findByName( "patient" );
        userService.save( p );

        mvc.perform( post( "/api/v1/personnel/hcp" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( p ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        final PatientAdvocate pa = (PatientAdvocate) personnelService.findByName( "hcp" );

        assertEquals( 1, pa.getPatients().size() );

        String result = mvc.perform( get( "/api/v1/prescriptions/hcp/patient" ) )
                .andExpect( MockMvcResultMatchers.status().isBadRequest() ).andDo( print() ).andReturn().getResponse()
                .getContentAsString();

        final boolean[] permission = { false, true, false };

        mvc.perform( put( "/api/v1/personnel/hcp/patient" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( permission ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        assertFalse( pa.getPatients().get( p.getUsername() )[0] );
        assertTrue( pa.getPatients().get( p.getUsername() )[1] );
        assertFalse( pa.getPatients().get( p.getUsername() )[2] );

        result = mvc.perform( get( "/api/v1/prescriptions/hcp/patient" ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andDo( print() ).andReturn().getResponse()
                .getContentAsString();

        final List<Prescription> prescriptions = TestUtils.gson().fromJson( result,
                new TypeToken<List<Prescription>>() {
                }.getType() );

        assertTrue( prescriptions.size() == 2 );

    }

}
