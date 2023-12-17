package edu.ncsu.csc.iTrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Controller for the Patient landing screen and basic patient information
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class PatientController {

    /**
     * Retrieves the page for the Patient to request an Appointment
     *
     * @param model
     *            Data for the front end
     * @return The page the patient should see
     */
    @GetMapping ( "/patient/manageAppointmentRequest" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String requestAppointmentForm ( final Model model ) {
        return "/patient/manageAppointmentRequest";
    }

    /**
     * Returns the form page for a patient to view all OfficeVisits
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/patient/officeVisit/viewOfficeVisits" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewOfficeVisits ( final Model model ) {
        return "/patient/officeVisit/viewOfficeVisits";
    }

    /**
     * Returns the form page for a patient to view all prescriptions
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/patient/officeVisit/viewPrescriptions" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewPrescriptions ( final Model model ) {
        return "/patient/officeVisit/viewPrescriptions";
    }

    /**
     * Landing screen for a Patient when they log in
     *
     * @param model
     *            The data from the front end
     * @return The page to show to the user
     */
    @RequestMapping ( value = "patient/index" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String index ( final Model model ) {
        return Role.ROLE_PATIENT.getLanding();
    }

    /**
     * Provides the page for a User to view and edit their demographics
     *
     * @param model
     *            The data for the front end
     * @return The page to show the user so they can edit demographics
     */
    @GetMapping ( value = "patient/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewDemographics ( final Model model ) {
        return "/patient/editDemographics";
    }

    /**
     * Create a page for the patient to view all diagnoses
     *
     * @param model
     *            data for front end
     * @return The page for the patient to view their diagnoses
     */
    @GetMapping ( value = "patient/officeVisit/viewDiagnoses" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewDiagnoses ( final Model model ) {
        return "/patient/officeVisit/viewDiagnoses";
    }

    /**
     * Create a page for the patient to view vaccination history s
     *
     * @param model
     *            data for front end
     * @return The page for the patient to view vaccination history
     */
    @GetMapping ( value = "patient/vaccination-history" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewVaccinationHistory ( final Model model ) {
        return "/patient/vaccination-history";
    }

    /**
     * Create a page for the patient to view bills
     *
     * @param model
     *            data for front end
     * @return The page for the patient to view bills
     */
    @GetMapping ( value = "patient/viewBills" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String viewBills ( final Model model ) {
        return "/patient/viewBills";
    }

    /**
     * Create a page for the patient to manage patient advocate permissions
     *
     * @param model
     *            data for front end
     * @return The page for the patient to manage patient advocate permissions
     */
    @GetMapping ( value = "patient/managePermissions" )
    @PreAuthorize ( "hasRole('ROLE_PATIENT')" )
    public String managePermissions ( final Model model ) {
        return "/patient/managePermissions";
    }

}
