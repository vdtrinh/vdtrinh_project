package edu.ncsu.csc.iTrust2.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for API functionality for interacting with Users.
 *
 * @author Kai Presler-Marshall
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles ( { "test" } )
public class APIUserTest {

    private static final String USER_1 = "API_USER_1";

    private static final String USER_2 = "API_USER_2";

    private static final String USER_3 = "API_USER_3";

    private static final String PW     = "123456";

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    @Autowired
    private MockMvc             mvc;

    @Autowired
    private UserService<User>   service;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * Tests creating users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testCreateUsers () throws Exception {

        Assertions.assertEquals( 0, service.count(), "There should be no Users in the system" );

        final UserForm u = new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 1, service.count(), "There should be one user in the system after creating a User" );

        final UserForm u2 = new UserForm( USER_2, PW, Role.ROLE_HCP, 1 );

        u2.addRole( Role.ROLE_VIROLOGIST.toString() );
        u2.addRole( Role.ROLE_OPH.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 2, service.count(), "It should be possible to create a user with multiple roles" );

        final User retrieved = service.findByName( USER_2 );

        Assertions.assertNotNull( retrieved, "The created user should be retrievable from the database" );

        Assertions.assertEquals( Personnel.class, retrieved.getClass(), "The retrieved user should be a Personnel" );

        Assertions.assertEquals( 3, retrieved.getRoles().size(), "The retrieved user should have 3 roles" );

        final UserForm u3 = new UserForm( USER_3, PW, Role.ROLE_VACCINATOR, 1 );
        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u3 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final User vaccinator = service.findByName( USER_3 );
        Assertions.assertNotNull( vaccinator, "The created user should be retrievable from the database" );

        Assertions.assertEquals( Personnel.class, vaccinator.getClass(), "The retrieved user should be a Personnel" );

        Assertions.assertEquals( 1, vaccinator.getRoles().size(), "The retrieved user should have 1 role" );

    }

    /**
     * Attempts to create a PatientAdvocate, verifies all the fields are saved
     * persistently, then attempts to create an HCP user, verifying that these
     * details are saved as well.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testCreatePatientAdvocate () throws Exception {

        assertEquals( 0, service.count() );

        final UserForm userF = new UserForm( "zucchini", "password", "ROLE_PATIENTADVOCATE", "true", "Vu", "Trinh",
                "123 Main Street", "Apt 4", "Raleigh", "NC", "12345", "123-456-7890", "vdtrinh@ncsu.edu", "Dinh",
                "Zucchini" );

        mvc.perform( post( "/api/v1/users/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( userF ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        assertEquals( 1, service.count() );

        final PatientAdvocate user = (PatientAdvocate) service.findByName( "zucchini" );

        assertTrue( user.getUsername().equals( "zucchini" ) );
        assertTrue( !user.getPassword().equals( null ) );
        assertTrue( user.getRoles().contains( Role.ROLE_PATIENTADVOCATE ) );
        assertEquals( 1, user.getEnabled() );
        assertTrue( user.getFirstName().equals( "Vu" ) );
        assertTrue( user.getLastName().equals( "Trinh" ) );
        assertTrue( user.getAddress1().equals( "123 Main Street" ) );
        assertTrue( user.getAddress2().equals( "Apt 4" ) );
        assertTrue( user.getCity().equals( "Raleigh" ) );
        assertEquals( State.NC, user.getState() );
        assertTrue( user.getZip().equals( "12345" ) );
        assertTrue( user.getPhone().equals( "123-456-7890" ) );
        assertTrue( user.getEmail().equals( "vdtrinh@ncsu.edu" ) );
        assertTrue( user.getMiddleName().equals( "Dinh" ) );
        assertTrue( user.getNickname().equals( "Zucchini" ) );

        // add other types of users

        final UserForm hcpF = new UserForm( "myHCP", "hcppassword", "ROLE_HCP", "true" );

        mvc.perform( post( "/api/v1/users/" ).with( csrf() ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( hcpF ) ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        assertEquals( 2, service.count() );

        final Personnel hcp = (Personnel) service.findByName( "myHCP" );

        assertTrue( hcp.getUsername().equals( "myHCP" ) );
        assertTrue( !hcp.getPassword().equals( null ) );
        assertTrue( hcp.getRoles().contains( Role.ROLE_HCP ) );
        assertEquals( 1, hcp.getEnabled() );
        assertNull( hcp.getFirstName() );
        assertNull( hcp.getLastName() );
        assertNull( hcp.getAddress1() );
        assertNull( hcp.getAddress2() );
        assertNull( hcp.getCity() );
        assertNull( hcp.getState() );
        assertNull( hcp.getZip() );
        assertNull( hcp.getPhone() );
        assertNull( hcp.getEmail() );

    }

    /**
     * Tests creating invalid users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testCreateInvalidUsers () throws Exception {

        final UserForm u1 = new UserForm( USER_1, PW, Role.ROLE_ADMIN, 1 );

        u1.addRole( Role.ROLE_ER.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u1 ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        Assertions.assertEquals( 0, service.count(),
                "Trying to create an invalid user should not create any User record" );

        final UserForm u2 = new UserForm( USER_2, PW, Role.ROLE_PATIENT, 1 );

        u2.addRole( Role.ROLE_HCP.toString() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( u2 ) ) )
                .andExpect( MockMvcResultMatchers.status().is4xxClientError() );

        Assertions.assertEquals( 0, service.count(),
                "Trying to create an invalid user should not create any User record" );

    }

    /**
     * Tests updating users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testUpdateUsers () throws Exception {

        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_HCP, 1 );

        final User u1 = new Personnel( uf );

        service.save( u1 );

        Assertions.assertEquals( u1.getUsername(), service.findByName( USER_1 ).getUsername() );

        uf.addRole( Role.ROLE_ER.toString() );
        uf.addRole( Role.ROLE_VACCINATOR.toString() );

        mvc.perform( MockMvcRequestBuilders.put( "/api/v1/users/" + uf.getUsername() ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( uf ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        final User retrieved = service.findByName( USER_1 );

        Assertions.assertEquals( 3, retrieved.getRoles().size(), "Updating a user should give them additional Roles" );

    }

    // @Test
    // @Transactional
    // @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    // public void testcreateNewPatientAdvocates () throws Exception {
    // final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_PATIENTADVOCATE,
    // 11 );
    //
    // final User u1 = new Personnel( uf );
    //
    // service.save( u1 );
    //
    // Assertions.assertEquals( u1.getUsername(), service.findByName( USER_1
    // ).getUsername() );
    //
    // mvc.perform( MockMvcRequestBuilders.put( "/api/v1/users/" +
    // uf.getUsername() ).with( csrf() )
    // .contentType( MediaType.APPLICATION_JSON ).content(
    // TestUtils.asJsonString( uf ) ) )
    // .andExpect( MockMvcResultMatchers.status().isOk() );
    //
    // final User retrieved = service.findByName( USER_1 );
    //
    // Assertions.assertEquals( 1, retrieved.getRoles().size() );
    //
    // }

    /**
     * Gets deleted users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testRetrieveDeleteUsers () throws Exception {
        final UserForm uf = new UserForm( USER_1, PW, Role.ROLE_HCP, 1 );

        final User u1 = new Personnel( uf );

        service.save( u1 );

        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/users/Lodewijk" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isNotFound() );

        mvc.perform( MockMvcRequestBuilders.get( "/api/v1/users/" + USER_1 ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/Lodewijk" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isNotFound() );

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/" + USER_1 ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() );

        Assertions.assertEquals( 0, service.count(), "Deleting a user should really delete them" );

    }

    /**
     * Tests the roles
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testRole () throws Exception {

        final MockHttpServletResponse response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andReturn().getResponse();

        final String responseStr = response.getContentAsString();

        Assertions.assertTrue( responseStr.contains( "ROLE_ADMIN" ) );

    }

    /**
     * Tests unauthroized users
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "user", roles = { "USER" } )
    public void testRoleUnauthorised () throws Exception {

        final MockHttpServletResponse response = mvc
                .perform( MockMvcRequestBuilders.get( "/api/v1/role" ).contentType( MediaType.APPLICATION_JSON ) )
                .andExpect( MockMvcResultMatchers.status().isUnauthorized() ).andReturn().getResponse();

        final String responseStr = response.getContentAsString();

        Assertions.assertTrue( responseStr.contains( "UNAUTHORIZED" ) );

    }

    /**
     * This test evaluates the ability to delete PatientAdvocates and Patients
     * which have associations at the time of deletion.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "admin", roles = { "ADMIN" } )
    public void testDeleteAssociatedUsers () throws Exception {

        Assertions.assertEquals( 0, service.count(), "There should be no Users in the system" );

        // Create one Patient Advocate myAdvocate

        final UserForm myAdvocateForm = new UserForm( "myAdvocate", "ADVOCATEPASSWORD", "ROLE_PATIENTADVOCATE", "true",
                "John", "Doe", "2345 Advocate Street", "", "Advocateville", "NC", "12465", "123-520-2394",
                "johndoe@advocates.org", "Advocate", "Addie" );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( myAdvocateForm ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andDo( print() );

        // Create two Patients: patient1 and patient2

        final UserForm patient1Form = new UserForm( "patient1", "PATIENTPASSWORD", "ROLE_PATIENT", "true" );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patient1Form ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andDo( print() );

        final UserForm patient2Form = new UserForm( "patient2", "PATIENTPASSWORD", "ROLE_PATIENT", "true" );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/users" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patient2Form ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andDo( print() );

        // Check that all three users exist in service

        Assertions.assertEquals( 3, service.count(), "There should be no Users in the system" );

        PatientAdvocate myAdvocate = (PatientAdvocate) service.findByName( "myAdvocate" );
        final Patient patient1 = (Patient) service.findByName( "patient1" );
        Patient patient2 = (Patient) service.findByName( "patient2" );

        // Associate myAdvocate & patient1, patient2 via API calls to /personnel
        // endpoint

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/personnel/myAdvocate" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patient1 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andDo( print() );

        mvc.perform( MockMvcRequestBuilders.post( "/api/v1/personnel/myAdvocate" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( patient2 ) ) )
                .andExpect( MockMvcResultMatchers.status().isOk() ).andDo( print() );

        // Check that patient1 and patient 2 are associated to myAdvocate

        assertEquals( 2, myAdvocate.getPatients().size() );
        assertTrue( myAdvocate.getPatients().containsKey( "patient1" ) );
        assertTrue( myAdvocate.getPatients().containsKey( "patient2" ) );

        // Check that myAdvocate is associated to patient1 and patient2

        assertEquals( 1, patient1.getPatientAdvocates().size() );
        assertTrue( patient1.getPatientAdvocates().contains( myAdvocate ) );

        assertEquals( 1, patient2.getPatientAdvocates().size() );
        assertTrue( patient2.getPatientAdvocates().contains( myAdvocate ) );

        // Attempt to delete patient1, print, expect success

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/patient1" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        // Check that myAdvocate is no longer associated to patient1 but is
        // still associated to patient2

        myAdvocate = (PatientAdvocate) service.findByName( "myAdvocate" );

        assertEquals( 1, myAdvocate.getPatients().size() );
        assertFalse( myAdvocate.getPatients().containsKey( "patient1" ) );
        assertTrue( myAdvocate.getPatients().containsKey( "patient2" ) );

        // Check that myAdvocate is still associated to patient2

        patient2 = (Patient) service.findByName( "patient2" );
        assertTrue( patient2.getPatientAdvocates().contains( myAdvocate ) );

        // Attempt to delete myAdvocate, print, expect success

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/myAdvocate" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        // Check that myAdvocate is no longer associated to patient2

        patient2 = (Patient) service.findByName( "patient2" );
        assertFalse( patient2.getPatientAdvocates().contains( myAdvocate ) );
        assertEquals( 0, patient2.getPatientAdvocates().size() );

        // Attempt to delete patient2, print, expect success

        mvc.perform( MockMvcRequestBuilders.delete( "/api/v1/users/patient2" ).with( csrf() )
                .contentType( MediaType.APPLICATION_JSON ) ).andExpect( MockMvcResultMatchers.status().isOk() )
                .andDo( print() );

        Assertions.assertEquals( 0, service.count(), "There should be no Users in the system" );

    }

}
