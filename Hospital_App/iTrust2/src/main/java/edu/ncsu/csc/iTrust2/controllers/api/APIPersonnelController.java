package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.PersonnelForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.forms.display.ProviderWithStatistics;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.PatientService;
import edu.ncsu.csc.iTrust2.services.PersonnelService;
import edu.ncsu.csc.iTrust2.services.SatisfactionSurveyStatisticsService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * Controller responsible for providing various REST API endpoints for the
 * Personnel model.
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIPersonnelController extends APIController {

    /** LoggerUtil */
    @Autowired
    private LoggerUtil                          loggerUtil;

    /** Personnel Service */
    @Autowired
    private PersonnelService                    service;

    /** Personnel Service */
    @Autowired
    private PatientService                      pService;

    /** SatisfactionSurveyStats service */
    @Autowired
    private SatisfactionSurveyStatisticsService satisfactionSurveyStatisticsService;

    /**
     * Retrieves and returns a list of all Personnel stored in the system
     *
     * @return list of personnel
     */
    @GetMapping ( BASE_PATH + "/personnel" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN', 'ROLE_PATIENTADVOCATE')" )
    public List<Personnel> getPersonnel () {
        return service.findAll();
    }

    /**
     * Retrieves and returns the Personnel with the username provided
     *
     * @param id
     *            The username of the Personnel to be retrieved, as stored in
     *            the Users table
     * @return response
     */
    @GetMapping ( BASE_PATH + "/personnel/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN', 'ROLE_PATIENTADVOCATE')" )
    public ResponseEntity getPersonnel ( @PathVariable ( "id" ) final String id ) {
        final Personnel personnel = (Personnel) service.findByName( id );
        if ( null == personnel ) {
            return new ResponseEntity( errorResponse( "No personnel found for id " + id ), HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( personnel, HttpStatus.OK );
        }
    }

    /**
     * If you are logged in as a personnel, then you can use this convenience
     * lookup to find your own information without remembering your id. This
     * allows you the shorthand of not having to look up the id in between.
     *
     * @return The personnel object for the currently authenticated user.
     */
    @GetMapping ( BASE_PATH + "/curPersonnel" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN', 'ROLE_PATIENTADVOCATE')" )
    public ResponseEntity getCurrentPersonnel () {
        final String username = LoggerUtil.currentUser();
        final Personnel personnel = (Personnel) service.findByName( username );
        if ( personnel == null ) {
            return new ResponseEntity( errorResponse( "Could not find a personnel entry for you, " + username ),
                    HttpStatus.NOT_FOUND );
        }
        else {
            loggerUtil.log( TransactionType.VIEW_DEMOGRAPHICS, username,
                    "Retrieved demographics for user " + username );
            return new ResponseEntity( personnel, HttpStatus.OK );
        }
    }

    /**
     * Updates the Personnel with the id provided by overwriting it with the new
     * Personnel record that is provided. If the ID provided does not match the
     * ID set in the Patient provided, the update will not take place
     *
     * @param id
     *            The username of the Personnel to be updated
     * @param personnelF
     *            The updated Personnel to save
     * @return response
     */
    @PutMapping ( BASE_PATH + "/personnel/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN')" )
    public ResponseEntity updatePersonnel ( @PathVariable final String id,
            @RequestBody final PersonnelForm personnelF ) {

        final Personnel fromDb = (Personnel) service.findByName( id );

        if ( null == fromDb ) {
            return new ResponseEntity( errorResponse( "Could not find a personnel entry for you, " + id ),
                    HttpStatus.NOT_FOUND );
        }

        fromDb.update( personnelF );
        if ( ( null != fromDb.getUsername() && !id.equals( fromDb.getUsername() ) ) ) {
            return new ResponseEntity(
                    errorResponse( "The ID provided does not match the ID of the Personnel provided" ),
                    HttpStatus.CONFLICT );
        }
        try {
            service.save( fromDb );
            loggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( fromDb, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not update " + id + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Updates the given patient advocate with the given patient advocate form
     *
     * @param idPA
     *            id of the patient advocate
     * @param patientAdvocateF
     *            patient advocate form to update to
     * @return response of the request
     */
    @PutMapping ( BASE_PATH + "/patientadvocate/{idPA}" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE', 'ROLE_ADMIN') " )
    public ResponseEntity updatePatientAdvocate ( @PathVariable final String idPA,
            @RequestBody final UserForm patientAdvocateF ) {

        final PatientAdvocate fromDb = (PatientAdvocate) service.findByName( idPA );

        if ( fromDb == null ) {
            return new ResponseEntity( errorResponse( "Could not find a patient advocate entry for you, " + idPA ),
                    HttpStatus.NOT_FOUND );
        }

        fromDb.update( patientAdvocateF );

        if ( ( fromDb.getUsername() != null && !idPA.equals( fromDb.getUsername() ) ) ) {
            return new ResponseEntity(
                    errorResponse( "The ID provided does not match the ID of the Patient Advocate provided" ),
                    HttpStatus.CONFLICT );
        }

        try {
            service.save( fromDb );
            loggerUtil.log( TransactionType.EDIT_DEMOGRAPHICS, LoggerUtil.currentUser() );
            return new ResponseEntity( fromDb, HttpStatus.OK );
        }
        catch ( final Exception e ) {
            return new ResponseEntity( errorResponse( "Could not update " + idPA + " because of " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Returns only personnel of a specific role, based on what the user wants.
     *
     * @param role
     *            the role to filter out personnel by
     * @return response and list of personnel matching query
     */
    @GetMapping ( BASE_PATH + "/personnel/getbyroles/{role}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_VACCINATOR', 'ROLE_BSM', 'ROLE_PATIENTADVOCATE')" )
    public ResponseEntity getPersonnelByRole ( @PathVariable ( "role" ) final String role ) {
        final List<Personnel> allPersonnel = service.findAll();

        try {
            final Role desired = Role.valueOf( role );

            allPersonnel.removeIf( e -> !e.getRoles().contains( desired ) );

            return new ResponseEntity( allPersonnel, HttpStatus.OK );
        }
        catch ( final IllegalArgumentException iae ) {
            return new ResponseEntity( errorResponse( "Invalid role" ), HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * Returns only personnel of a specific role, based on what the user wants.
     * Also returns statistics for the personnel.
     *
     * @param role
     *            the role to filter out personnel by
     * @return response and list of personnel with statistics matching query
     */
    @GetMapping ( BASE_PATH + "/personnel/getbyroles/{role}/statistics" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP', 'ROLE_ADMIN', 'ROLE_PATIENT', 'ROLE_VACCINATOR', 'ROLE_BSM', 'ROLE_PATIENTADVOCATE')" )
    public ResponseEntity getPersonnelByRoleWithStatistics ( @PathVariable ( "role" ) final String role ) {
        final List<Personnel> allPersonnel = service.findAll();

        try {
            final List<ProviderWithStatistics> enriched = allPersonnel.stream()
                    .filter( e -> e.getRoles().contains( Role.valueOf( role ) ) )
                    .map( personnel -> new ProviderWithStatistics( personnel,
                            satisfactionSurveyStatisticsService.findForHcp( personnel ) ) )
                    .collect( Collectors.toList() );

            return new ResponseEntity( enriched, HttpStatus.OK );
        }
        catch ( final IllegalArgumentException iae ) {
            return new ResponseEntity( errorResponse( "Invalid role" ), HttpStatus.BAD_REQUEST );
        }

    }

    /**
     * This method will unassigns patient to patient advocate
     *
     * @param idPA
     *            Patient Advocate ID
     * @param idP
     *            patient list to assigns
     * @return Success if the idPA is a patient advocate and it is on the
     *         personnel list. Otherwise, false
     */
    @PostMapping ( BASE_PATH + "/personnel/{idPA}" )
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN')" )
    public ResponseEntity assignPatientToPatientAdvocate ( @PathVariable ( "idPA" ) final String idPA,
            @RequestBody final Patient idP ) {

        System.out.println( "\nBeginning assignment operation...\n" );

        final PatientAdvocate patientAd = (PatientAdvocate) service.findByName( idPA );

        System.out.println( "\nFound patient advocate!\n" );

        if ( !patientAd.getRoles().contains( Role.ROLE_PATIENTADVOCATE ) ) {
            System.out.println( "\nListed PatientAdvocate is not actually a patient advocate\n" );
            return new ResponseEntity( errorResponse( "Invalid personnel " ), HttpStatus.BAD_REQUEST );
        }

        final Patient patient1 = (Patient) pService.findById( idP.getUsername() );

        patientAd.addPatients( patient1 );

        service.save( patientAd );
        loggerUtil.log( TransactionType.ASSIGN_PATIENT_ADVOCATE_TO_PATIENT, LoggerUtil.currentUser() );

        patient1.addPatientAdvocates( patientAd );

        pService.save( patient1 );

        System.out.println( "\nAssociation operation successful\n" );

        loggerUtil.log( TransactionType.ASSIGN_PATIENT_TO_PATIENT_ADVOCATE, LoggerUtil.currentUser() );

        return new ResponseEntity( patientAd, HttpStatus.OK );

    }

    /**
     * This method will unassigns patient to patient advocate
     *
     * @param idPA
     *            Patient Advocate ID
     * @param idP
     *            patient list to unassigns
     * @return Success if the idPA is a patient advocate and it is on the
     *         personnel list. Otherwise, false
     */
    @DeleteMapping ( BASE_PATH + "/personnel/{idPA}/{idP}" )
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN')" )
    public ResponseEntity unassignPatientToPatientAdvocate ( @PathVariable ( "idPA" ) final String idPA,
            @PathVariable final String idP ) {
        final Patient patientA = (Patient) pService.findByName( idP );

        final PatientAdvocate patientAd = (PatientAdvocate) service.findByName( idPA );

        if ( null == patientA ) {

            System.out.println( "\nPatient Advocate does not exist\n" );
            return new ResponseEntity( errorResponse( "No patient found for id " + idP ), HttpStatus.NOT_FOUND );

        }

        patientAd.removePatients( patientA );

        service.save( patientAd );
        loggerUtil.log( TransactionType.UNASSIGN_PATIENT_FROM_PATIENT_ADVOCATE, LoggerUtil.currentUser() );

        patientA.getPatientAdvocates().remove( patientAd );

        pService.save( patientA );
        loggerUtil.log( TransactionType.UNASSIGN_PATIENT_ADVOCATE_FROM_PATIENT, LoggerUtil.currentUser() );

        System.out.println( "\nDelete operation successful\n" );
        return new ResponseEntity( idP, HttpStatus.OK );

    }

    /**
     * This method will take in an array of permission and set it for the
     * patient advocate to view information on the patient.
     *
     * @param idPA
     *            patient advocate
     * @param idP
     *            patient
     * @param pMission
     *            an array of boolean permission
     * @return http status ok if the request is met. Otherwise, http status will
     *         return bad request
     */
    @PutMapping ( BASE_PATH + "/personnel/{idPA}/{idP}" )
    @PreAuthorize ( "hasAnyRole('ROLE_ADMIN', 'ROLE_PATIENT')" )
    public ResponseEntity permission ( @PathVariable ( "idPA" ) final String idPA, @PathVariable final String idP,
            @RequestBody final boolean[] pMission ) {

        final PatientAdvocate patientAd = (PatientAdvocate) service.findByName( idPA );

        final Patient patient = (Patient) pService.findByName( idP );

        if ( patientAd == null || patient == null ) {
            return new ResponseEntity( errorResponse( "Invalid Patient " ), HttpStatus.BAD_REQUEST );
        }

        if ( patientAd.getPatients().containsKey( patient.getUsername() ) ) {

            patientAd.setViewBilling( patient, pMission[0] );

            patientAd.setViewPrescriptions( patient, pMission[1] );

            patientAd.setViewOfficeVisits( patient, pMission[2] );

            service.save( patientAd );
            loggerUtil.log( TransactionType.UPDATE_PERMISSIONS, LoggerUtil.currentUser() );

            return new ResponseEntity( patientAd, HttpStatus.OK );
        }

        return new ResponseEntity( errorResponse( "Invalid Patient " ), HttpStatus.BAD_REQUEST );
    }

}
