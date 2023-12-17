package edu.ncsu.csc.iTrust2.unit;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;

/**
 * Tests the patient advocate model
 *
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
@ActiveProfiles ( { "test" } )
public class PatientAdvocateTesting {

    /**
     * Set up for personnel service
     */
    @Autowired
    private PersonnelService        service;

    /**
     * Set up for Patient service
     */
    @Autowired
    private PatientService<Patient> service1;
    /**
     * Demo patient
     */
    private static final String     USER_1 = "demoTestUser1";

    /**
     * Demo patient
     */
    private static final String     USER_2 = "demoTestUser2";

    /**
     * Demo Patient Advocate
     */
    private static final String     PA     = "PA1";

    /**
     * Demo password
     */
    private static final String     PW     = "123456";

    /**
     * Set up before each test case
     */
    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    /**
     * Testing if valid and invalid when create patient advocate
     */
    @Test
    @Transactional
    public void invalidPatientAdvocate () {
        // Check to make sure there isn't anyone in the record before adding
        // personnel
        Assertions.assertEquals( 0, service.count(), "There should be no PatientAdvocate records in the system" );

        // Create patient advocate with empty nick name and middle name
        final PatientAdvocate patientAd = new PatientAdvocate( new UserForm( PA, PW, Role.ROLE_PATIENTADVOCATE, 11 ) );

        // Save the patient advocate to the database
        service.save( patientAd );

        final List<Personnel> savedRecords = service.findAll();

        // Check if the patient advocate is in the database
        Assertions.assertEquals( 1, savedRecords.size(),
                "Creating a PatientAdvocate record should results in its creation in the DB" );

        // Check to make sure the HCP can't be create as Patient Advocate
        try {
            final PatientAdvocate patientAd1 = new PatientAdvocate( new UserForm( USER_1, PW, Role.ROLE_HCP, 2 ) );
            Assertions.fail( "Should not be able to create a PatientAdvocate with HCP role." );
        }
        catch ( final Exception e ) {
            // Do nothing
        }
    }

    /**
     * Testing middle name and nick name as null
     */
    @Test
    @Transactional
    public void middleNameAndNickNameWithNull () {
        // Check to make sure there isn't anyone in the record before adding
        // personnel
        Assertions.assertEquals( 0, service.count(), "There should be no Personnel records in the system" );

        // Create patient advocate with null nick name and middle name
        PatientAdvocate patientAd = new PatientAdvocate(
                new UserForm( PA, PW, "ROLE_PATIENTADVOCATE", "11", "Bob", "Ross", "123 Happy Accidents Street",
                        "Apt 456", "Los Angeles", "CA", "12345", "098-765-4321", "bobross@yahoo.com", "", "" ) );

        // Save the patient advocate to the database
        service.save( patientAd );

        // Check if the patient advocate is in the database
        Assertions.assertEquals( 1, service.count(),
                "Creating a PatientAdvocate record should result in its creation in the DB" );

        patientAd = (PatientAdvocate) service.findByName( PA );

        // Check if the nick name and middle name are empty string
        Assertions.assertEquals( "", patientAd.getMiddleName() );
        Assertions.assertEquals( "", patientAd.getNickname() );
    }

    /**
     * Testing add the patient to the patient advocate list
     */
    @Test
    @Transactional
    public void testAddPatients () {
        // Check to make sure there isn't anyone in the record before adding
        // personnel
        Assertions.assertEquals( 0, service.count(), "There should be no Personnel records in the system" );

        // Create patient advocate with null nick name and middle name
        final PatientAdvocate patientAd = new PatientAdvocate(
                new UserForm( PA, PW, "ROLE_PATIENTADVOCATE", "11", "Bob", "Ross", "123 Happy Accidents Street",
                        "Apt 456", "Los Angeles", "CA", "12345", "098-765-4321", "bobross@yahoo.com", "", "" ) );

        // Save the patient advocate to the system
        service.save( patientAd );

        // Create new patient
        final Patient patient = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );

        // Save the patient to the system
        service1.save( patient );

        final List<Personnel> savedRecords = service.findAll();

        Assertions.assertEquals( 1, savedRecords.size(),
                "Creating a Personnel record should results in its creation in the DB" );

        // Add the patient to the list
        patientAd.addPatients( patient );

        // Check to make sure the patient is added to the patient advocate list
        Assertions.assertEquals( patientAd.getPatients().size(), 1 );

        // Add duplicate patient to the list
        try {
            patientAd.addPatients( patient );
            Assertions.fail(
                    "Should not be able to add the same Patient twice to the list of Patients associated to a PatientAdvocate" );
        }
        catch ( final Exception e ) {
            // Leave empty
        }

        // Add non-existent patient to the list
        try {
            patientAd.addPatients( null );
            Assertions.fail(
                    "Should not be able to add a nonexistent Patient to the list of Patients associated to a PatientAdvocate" );
        }
        catch ( final Exception e ) {
            // Leave empty
        }
    }

    /**
     * Testing remove patient function
     */
    @Test
    @Transactional
    public void testRemovePatients () {
        // Check to make sure there isn't anyone in the record before adding
        // personnel
        Assertions.assertEquals( 0, service.count(), "There should be no Personnel records in the system" );

        // Create patient advocate
        final PatientAdvocate patientAd = new PatientAdvocate( new UserForm( PA, PW, Role.ROLE_PATIENTADVOCATE, 11 ) );

        // Save the patient advocate to the database
        service.save( patientAd );

        // Create patient
        final Patient patient = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );

        // Save patient to the database
        service1.save( patient );

        final List<Personnel> savedRecords = service.findAll();

        // Check if the patient advocate is in the system
        Assertions.assertEquals( 1, savedRecords.size(),
                "Creating a Personnel record should results in its creation in the DB" );

        // Add patient to the patient advocate list
        patientAd.addPatients( patient );

        // Check if the patient is in the list
        Assertions.assertEquals( patientAd.getPatients().size(), 1 );

        // Remove the patient from the list
        patientAd.removePatients( patient );

        // Check if the patient advocate list should be empty
        Assertions.assertTrue( patientAd.getPatients().isEmpty() );

        // Try to remove non-exist patient
        try {
            patientAd.removePatients( null );
            Assertions.assertTrue( patientAd.getPatients().isEmpty() );
            Assertions.fail( "Should not be able to remove null patient from the list" );
        }
        catch ( final Exception e ) {
            // Leave empty
        }

        // Try to remove from an empty list
        try {
            patientAd.removePatients( patient );
            Assertions.assertTrue( patientAd.getPatients().isEmpty() );
            Assertions.fail( "Should not be able to remove null patient from the list" );
        }
        catch ( final Exception e ) {
            // Leave empty
        }
    }

    /**
     * Testing set billing permission function
     */
    @Test
    @Transactional
    public void testSetBillingPermission () {
        // Check to make sure there isn't anyone in the record before adding
        // personnel
        Assertions.assertEquals( 0, service.count(), "There should be no Personnel records in the system" );

        // Create patient advocate
        final PatientAdvocate patientAd = new PatientAdvocate( new UserForm( PA, PW, Role.ROLE_PATIENTADVOCATE, 11 ) );

        // Save the patient advocate to the database
        service.save( patientAd );

        // Create patient
        final Patient patient = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );

        // Save patient to the database
        service1.save( patient );

        final List<Personnel> savedRecords = service.findAll();

        // Check if the patient advocate is in the system
        Assertions.assertEquals( 1, savedRecords.size(),
                "Creating a Personnel record should results in its creation in the DB" );

        // Add patient to the patient advocate list
        patientAd.addPatients( patient );

        // Check if the patient is in the list
        Assertions.assertEquals( patientAd.getPatients().size(), 1 );

        // Set the patient view bill to true
        patientAd.setViewBilling( patient, true );

        // Check to make sure the view billing permission is true
        // View prescriptions and office visit are false
        final boolean[] permission = patientAd.getPatients().get( patient.getUsername() );
        Assertions.assertTrue( permission[0] );
        Assertions.assertFalse( permission[1] );
        Assertions.assertFalse( permission[2] );

        // Set the patient view billing to false
        patientAd.setViewBilling( patient, false );

        // Check to make sure the view prescription is false
        // Billing and office visit are false
        final boolean[] change = patientAd.getPatients().get( patient.getUsername() );
        Assertions.assertFalse( change[0] );
        Assertions.assertFalse( change[1] );
        Assertions.assertFalse( change[2] );

    }

    /**
     * Testing set view precription permission function
     */
    @Test
    @Transactional
    public void testSetViewPrescriptions () {
        // Check to make sure there isn't anyone in the record before adding
        // personnel
        Assertions.assertEquals( 0, service.count(), "There should be no Personnel records in the system" );

        // Create patient advocate
        final PatientAdvocate patientAd = new PatientAdvocate( new UserForm( PA, PW, Role.ROLE_PATIENTADVOCATE, 11 ) );

        // Save the patient advocate to the database
        service.save( patientAd );

        // Create patient
        final Patient patient = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );

        // Save patient to the database
        service1.save( patient );

        final List<Personnel> savedRecords = service.findAll();

        // Check if the patient advocate is in the system
        Assertions.assertEquals( 1, savedRecords.size(),
                "Creating a Personnel record should results in its creation in the DB" );

        // Add patient to the patient advocate list
        patientAd.addPatients( patient );

        // Check if the patient is in the list
        Assertions.assertEquals( patientAd.getPatients().size(), 1 );

        // Set the patient view prescription to true
        patientAd.setViewPrescriptions( patient, true );

        // Check to make sure the view prescription permission is true
        // Billing and office visit are false
        final boolean[] permission = patientAd.getPatients().get( patient.getUsername() );
        Assertions.assertFalse( permission[0] );
        Assertions.assertTrue( permission[1] );
        Assertions.assertFalse( permission[2] );

        // Set the patient view prescription to false
        patientAd.setViewPrescriptions( patient, false );

        // Check to make sure the view prescription is false
        // Billing and office visit are false
        final boolean[] change = patientAd.getPatients().get( patient.getUsername() );
        Assertions.assertFalse( change[0] );
        Assertions.assertFalse( change[1] );
        Assertions.assertFalse( change[2] );
    }

    /**
     * Testing set office visit permission function
     */
    @Test
    @Transactional
    public void testSetOfficeVisit () {
        // Check to make sure there isn't anyone in the record before adding
        // personnel
        Assertions.assertEquals( 0, service.count(), "There should be no Personnel records in the system" );

        // Create patient advocate
        final PatientAdvocate patientAd = new PatientAdvocate( new UserForm( PA, PW, Role.ROLE_PATIENTADVOCATE, 11 ) );

        // Save the patient advocate to the database
        service.save( patientAd );

        // Create patient
        final Patient patient = new Patient( new UserForm( USER_1, PW, Role.ROLE_PATIENT, 1 ) );

        // Save patient to the database
        service1.save( patient );

        final List<Personnel> savedRecords = service.findAll();

        // Check if the patient advocate is in the system
        Assertions.assertEquals( 1, savedRecords.size(),
                "Creating a Personnel record should results in its creation in the DB" );

        // Add patient to the patient advocate list
        patientAd.addPatients( patient );

        // Check if the patient is in the list
        Assertions.assertEquals( patientAd.getPatients().size(), 1 );

        // Set the patient view office visit office to true
        patientAd.setViewOfficeVisits( patient, true );

        // Check to make sure the view office visit permission is true
        // Billing and prescription are false
        final boolean[] permission = patientAd.getPatients().get( patient.getUsername() );
        Assertions.assertFalse( permission[0] );
        Assertions.assertFalse( permission[1] );
        Assertions.assertTrue( permission[2] );

        // Set the patient view office visit to false
        patientAd.setViewOfficeVisits( patient, false );

        // Check to make sure the view office visit permission is false
        // Billing and prescription are false
        final boolean[] change = patientAd.getPatients().get( patient.getUsername() );
        Assertions.assertFalse( change[0] );
        Assertions.assertFalse( change[1] );
        Assertions.assertFalse( change[2] );
    }

}
