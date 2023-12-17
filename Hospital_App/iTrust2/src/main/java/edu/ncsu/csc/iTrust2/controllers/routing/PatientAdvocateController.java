package edu.ncsu.csc.iTrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Controller to manage basic abilities for Patient Advocate roles
 *
 * @author Grant Arne
 *
 */

@Controller
public class PatientAdvocateController {

    /**
     * Returns the patient advocate for the given model
     *
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "patientadvocate/index" )
    @PreAuthorize ( "hasRole('ROLE_PATIENTADVOCATE')" )
    public String index ( final Model model ) {
        return Role.ROLE_PATIENTADVOCATE.getLanding();
    }

    /**
     * Returns the page to view patient advocate's patients for the given model
     *
     * @param model
     *            model to check
     * @return role
     */
    @RequestMapping ( value = "patientadvocate/viewPatients" )
    @PreAuthorize ( "hasRole('ROLE_PATIENTADVOCATE')" )
    public String viewPatients ( final Model model ) {
        return "/patientadvocate/viewPatients";
    }

    /**
     * Provides the page for a User to view and edit their demographics
     *
     * @param model
     *            The data for the front end
     * @return The page to show the user so they can edit demographics
     */
    @GetMapping ( value = "patientadvocate/editDemographics" )
    @PreAuthorize ( "hasRole('ROLE_PATIENTADVOCATE')" )
    public String viewDemographics ( final Model model ) {
        return "/patientadvocate/editDemographics";
    }
}
