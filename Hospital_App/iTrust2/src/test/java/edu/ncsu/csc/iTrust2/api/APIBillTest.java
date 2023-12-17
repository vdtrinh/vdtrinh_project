package edu.ncsu.csc.iTrust2.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZonedDateTime;
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

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.forms.VaccineVisitForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.VaccineAppointmentRequest;
import edu.ncsu.csc.iTrust2.models.VaccineType;
import edu.ncsu.csc.iTrust2.models.enums.AppointmentType;
import edu.ncsu.csc.iTrust2.models.enums.PaymentType;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.Status;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.PaymentService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.services.VaccineAppointmentRequestService;
import edu.ncsu.csc.iTrust2.services.VaccineTypeService;
import edu.ncsu.csc.iTrust2.services.VaccineVisitService;

/**
 * Test class for APIBillController API Endpoints
 *
 * @author mhyun, bswalia
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIBillTest {
    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    @Autowired
    private MockMvc                          mvc;
    /** VaccineVisitService service */
    @Autowired
    private VaccineVisitService              vaccineService;

    /** VaccineAppointmentRequestService service */
    @Autowired
    private VaccineAppointmentRequestService reqService;

    /** VaccineTypeService service */
    @Autowired
    private VaccineTypeService               vaccineTypeService;

    /** BillService service */
    @Autowired
    private BillService                      billService;
    /** CPTCodeService service */
    @Autowired
    private CPTCodeService                   cptService;
    /** UserService service */
    @Autowired
    UserService                              userService;
    /** PersonnelService service */
    @Autowired
    PersonnelService                         personnelService;

    /** PaymentService service */
    @Autowired
    private PaymentService                   paymentService;

    /**
     * Setup before for each test
     */
    @BeforeEach
    public void setup () {
        userService.deleteAll();
        billService.deleteAll();

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );
        final User patient2 = new Patient( new UserForm( "patient2", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_PATIENTADVOCATE, 1 ) );
        hcp.getRoles().add( Role.ROLE_HCP );

        userService.save( hcp );

        userService.save( patient );
        userService.save( patient2 );

        final VaccineType vaccine = new VaccineType();
        vaccine.setName( "Moderna" );
        vaccine.setNumDoses( 1 );
        vaccine.setIsAvailable( true );

        userService.saveAll( List.of( patient, hcp ) );
        vaccineTypeService.save( vaccine );

        final VaccineAppointmentRequest app = new VaccineAppointmentRequest();

        app.setDate( ZonedDateTime.parse( "2030-11-19T04:50:00.000-05:00" ) );
        app.setType( AppointmentType.GENERAL_CHECKUP );
        app.setStatus( Status.APPROVED );
        app.setHcp( userService.findByName( "hcp" ) );
        app.setPatient( userService.findByName( "patient" ) );
        app.setComments( "Test appointment please ignore" );
        app.setVaccine( vaccineTypeService.findByName( "Moderna" ) );

        reqService.save( app );
    }

    /**
     * Testing creating a bill from office visit
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    @Transactional
    public void testCreateBillOfficeVisit () throws Exception {
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
        ov2.setPatient( "patient2" );
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

        // testing getting a list of bills of a patient

        final Bill b = billService.findAll().get( 0 );
        Assertions.assertEquals( 5, b.getRemainingCost() );
        Assertions.assertEquals( c, b.getCptCodes().get( 0 ) );

    }

    /**
     * Testing creating a bill from vaccine visit
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "hcp", roles = { "HCP" } )
    @Transactional
    public void testCreateBillVaccineVisit () throws Exception {
        // create office visit form
        final VaccineVisitForm vv = new VaccineVisitForm();
        final Long reqid = reqService.findAll().get( 0 ).getId();
        vv.setRequestId( reqid + "" );
        vv.setDateTime( "2030-11-19T04:50:00.000-05:00" );
        vv.setVaccinator( "hcp" );
        vv.setPatient( "patient" );
        vv.setVaccine( "Moderna" );
        vv.setDose( "1" );

        // create CPTCode for vv
        final CPTCode c = new CPTCode();
        c.setCode( 55555 );
        c.setCost( 5 );
        c.setDescription( "asd" );
        c.setisActive( true );
        c.setVersion( 1 );
        cptService.save( c );
        final List<Long> li = new ArrayList<Long>();
        li.add( cptService.findAll().get( 0 ).getId() );
        vv.setCptCodes( li );
        // call endpoint
        mvc.perform( post( "/api/v1/bills/vv" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( vv ) ) ).andExpect( status().isOk() );
        final Bill b = billService.findAll().get( 0 );
        Assertions.assertEquals( 5, b.getRemainingCost() );
        Assertions.assertEquals( c, b.getCptCodes().get( 0 ) );

    }

    /**
     * Testing editing a bill from office visit
     *
     * @throws Exception
     *             exception
     */
    @Test
    @WithMockUser ( username = "billingstaffmember", roles = { "BSM" } )
    @Transactional
    public void testEditBill () throws Exception {
        // create office visit form
        final OfficeVisitForm ov = new OfficeVisitForm();
        ov.setDate( "2030-11-19T04:50:00.000-05:00" );
        ov.setHcp( "hcp" );
        ov.setPatient( "patient" );
        ov.setNotes( "Test office visit" );
        ov.setType( AppointmentType.GENERAL_CHECKUP.toString() );
        ov.setHospital( "iTrust Test Hospital 2" );
        // create CPTCode for ov
        final CPTCode c = new CPTCode();
        c.setCode( 55555 );
        c.setCost( 50 );
        c.setDescription( "asd" );
        c.setisActive( true );
        c.setVersion( 1 );
        cptService.save( c );
        final List<Long> li = new ArrayList<Long>();
        li.add( cptService.findAll().get( 0 ).getId() );
        ov.setCptCodes( li );
        // call endpoint
        final Bill b2 = billService.build( ov );
        billService.save( b2 );

        // testing getting a list of bills of a patient

        final Bill b = billService.findAll().get( 0 );
        Assertions.assertEquals( 50, b.getRemainingCost() );
        Assertions.assertEquals( c, b.getCptCodes().get( 0 ) );

        final Payment pay = new Payment();
        pay.setId( pay.getId() );
        pay.setAmount( 20 );
        pay.setPaymentType( PaymentType.CASH );
        paymentService.save( pay );
        final PaymentForm pf = new PaymentForm( pay );

        // calling put endpoint to update payment
        mvc.perform( put( "/api/v1/bills/" + b.getId() ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( pf ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );
        Assertions.assertEquals( 30, b.getRemainingCost(), 0.1 );
        Assertions.assertTrue( b.getCptCodes().get( 0 ).equals( c ) );
    }

}
