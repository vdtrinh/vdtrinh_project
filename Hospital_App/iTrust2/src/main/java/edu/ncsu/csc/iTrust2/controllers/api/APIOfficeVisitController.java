package edu.ncsu.csc.iTrust2.controllers.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.iTrust2.forms.OfficeVisitForm;
import edu.ncsu.csc.iTrust2.models.OfficeVisit;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.PatientAdvocate;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.TransactionType;
import edu.ncsu.csc.iTrust2.services.OfficeVisitService;
import edu.ncsu.csc.iTrust2.services.UserService;
import edu.ncsu.csc.iTrust2.utils.LoggerUtil;

/**
 * API controller for interacting with the OfficeVisit model. Provides standard
 * CRUD routes as appropriate for different user types
 *
 * @author Kai Presler-Marshall
 *
 */
@RestController
@SuppressWarnings ( { "unchecked", "rawtypes" } )
public class APIOfficeVisitController extends APIController {

    /** OfficeVisit service */
    @Autowired
    private OfficeVisitService officeVisitService;

    /** User service */
    @Autowired
    private UserService<User>  userService;

    /** LoggerUtil */
    @Autowired
    private LoggerUtil         loggerUtil;

    /**
     * Retrieves a list of all OfficeVisits in the database
     *
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/officevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public List<OfficeVisit> getOfficeVisits () {
        loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, LoggerUtil.currentUser() );
        return officeVisitService.findAll();
    }

    /**
     * Retrieves all of the office visits for the current HCP.
     *
     * @return all of the office visits for the current HCP.
     */
    @GetMapping ( BASE_PATH + "/officevisits/HCP" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public List<OfficeVisit> getOfficeVisitsForHCP () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, self );
        final List<OfficeVisit> visits = officeVisitService.findByHcp( self );
        return visits;
    }

    /**
     * Retrieves a list of all OfficeVisits in the database for the current
     * patient
     *
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/officevisits/myofficevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENT')" )
    public List<OfficeVisit> getMyOfficeVisits () {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, self );
        return officeVisitService.findByPatient( self );
    }

    /**
     * Retrieves a specific OfficeVisit in the database, with the given ID
     *
     * @param id
     *            ID of the office visit to retrieve
     * @return list of office visits
     */
    @GetMapping ( BASE_PATH + "/officevisits/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity getOfficeVisit ( @PathVariable final Long id ) {
        final User self = userService.findByName( LoggerUtil.currentUser() );
        loggerUtil.log( TransactionType.GENERAL_CHECKUP_HCP_VIEW, self );
        if ( !officeVisitService.existsById( id ) ) {
            return new ResponseEntity( HttpStatus.NOT_FOUND );
        }

        return new ResponseEntity( officeVisitService.findById( id ), HttpStatus.OK );
    }

    /**
     * Returns a list of office visits using the given username, or null if the
     * patient advocate cannot access.
     *
     * @param pUsername
     *            the username of the desired patient to receive office visits
     * @param paUsername
     *            the username of the desired patient advocate to receive office
     *            visits
     * @return the requested office visit list
     */
    @PreAuthorize ( "hasAnyRole('ROLE_PATIENTADVOCATE')" )
    @GetMapping ( BASE_PATH + "/officevisits/{paUsername}/{pUsername}" )
    public ResponseEntity getOfficeVisitsOfPatient ( @PathVariable final String paUsername,
            @PathVariable final String pUsername ) {
        final PatientAdvocate pa = (PatientAdvocate) userService.findByName( paUsername );
        final Patient p = (Patient) userService.findByName( pUsername );
        if ( pa == null || p == null ) {
            loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, LoggerUtil.currentUser(),
                    "Failed to find patient office visits with username " + pUsername );
            return new ResponseEntity( errorResponse( "No patient office visits found for " + pUsername ),
                    HttpStatus.NOT_FOUND );
        }

        final boolean permissions[] = pa.getPatients().get( p.getUsername() );
        if ( permissions == null || !permissions[2] ) {
            loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, LoggerUtil.currentUser(),
                    "Permission denied for " + paUsername + " to access prescription information for " + pUsername );
            return new ResponseEntity(
                    errorResponse( "Permission denied to access prescription information for " + pUsername ),
                    HttpStatus.BAD_REQUEST );
        }

        final List<OfficeVisit> visits = officeVisitService.findByPatient( p );

        loggerUtil.log( TransactionType.VIEW_ALL_OFFICE_VISITS, LoggerUtil.currentUser(),
                "Viewed prescriptions for " + pUsername );
        return new ResponseEntity( visits, HttpStatus.OK );
    }

    /**
     * Creates and saves a new OfficeVisit from the RequestBody provided.
     *
     * @param visitForm
     *            The office visit to be validated and saved
     * @return response
     */
    @PostMapping ( BASE_PATH + "/officevisits" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity createOfficeVisit ( @RequestBody final OfficeVisitForm visitForm ) {
        try {
            visitForm.setHcp( LoggerUtil.currentUser() );
            final OfficeVisit visit = officeVisitService.build( visitForm );

            if ( null != visit.getId() && officeVisitService.existsById( visit.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Office visit with the id " + visit.getId() + " already exists" ),
                        HttpStatus.CONFLICT );
            }
            officeVisitService.save( visit );
            loggerUtil.log( TransactionType.GENERAL_CHECKUP_CREATE, LoggerUtil.currentUser(),
                    visit.getPatient().getUsername() );
            return new ResponseEntity( visit, HttpStatus.OK );

        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not validate or save the OfficeVisit provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

    /**
     * Creates and saves a new Office Visit from the RequestBody provided.
     *
     * @param id
     *            ID of the office visit to update
     * @param visitForm
     *            The office visit to be validated and saved
     * @return response
     */
    @PutMapping ( BASE_PATH + "/officevisits/{id}" )
    @PreAuthorize ( "hasAnyRole('ROLE_HCP')" )
    public ResponseEntity updateOfficeVisit ( @PathVariable final Long id,
            @RequestBody final OfficeVisitForm visitForm ) {
        try {
            final OfficeVisit visit = officeVisitService.build( visitForm );

            if ( null == visit.getId() || !officeVisitService.existsById( visit.getId() ) ) {
                return new ResponseEntity(
                        errorResponse( "Office visit with the id " + visit.getId() + " doesn't exist" ),
                        HttpStatus.NOT_FOUND );
            }
            officeVisitService.save( visit );
            loggerUtil.log( TransactionType.GENERAL_CHECKUP_EDIT, LoggerUtil.currentUser(),
                    visit.getPatient().getUsername() );
            return new ResponseEntity( visit, HttpStatus.OK );

        }
        catch ( final Exception e ) {
            e.printStackTrace();
            return new ResponseEntity(
                    errorResponse( "Could not validate or save the OfficeVisit provided due to " + e.getMessage() ),
                    HttpStatus.BAD_REQUEST );
        }
    }

}
