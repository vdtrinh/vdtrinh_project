package edu.ncsu.csc.iTrust2.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.models.enums.State;

/**
 * Patient Advocate Class
 *
 * @author Chau, Sarah
 *
 */
@Entity
public class PatientAdvocate extends Personnel {

    /**
     * The middle name of the Patient Advocate
     */
    @Length ( max = 20 )
    private String                 middleName;

    /**
     * The nickname of the Patient Advocate
     */
    @Length ( max = 30 )
    private String                 nickname;

    /**
     * All of the patients for the patient advocate. Map of patients to a
     * boolean array, which contains 3 permissions (true = has access) to
     * patient bills, prescriptions, and office visits, respectively.
     */
    @ElementCollection ( fetch = FetchType.EAGER )
    @CollectionTable ( name = "patient_advocate_mapping",
            joinColumns = { @JoinColumn ( name = "patient_username", referencedColumnName = "username" ) } )
    @MapKeyColumn ( name = "patient" )
    @Column ( name = "permissions" )
    private Map<String, boolean[]> patients;

    /**
     * Index of the patients' map value boolean array that represents the
     * prescription viewing permission for that patient.
     */
    private static final int       BILL_INDEX         = 0;

    /**
     * Index of the patients' map value boolean array that represents the bill
     * viewing permission for that patient.
     */
    private static final int       PRESCRIPTION_INDEX = 1;

    /**
     * Index of the patients' map value boolean array that represents the office
     * visit viewing permission for that patient.
     */
    private static final int       OFFICE_VISIT_INDEX = 2;

    /**
     * For Hibernate
     */
    public PatientAdvocate () {

    }

    /**
     * Creates a Patient Advocate with the given UserForm, middle name, and
     * nickname. Initializes the associated patients map.
     *
     * @throws IllegalArgumentException
     *             if the role is not a personnel
     * @param uf
     *            user form for the patient advocate
     */
    public PatientAdvocate ( final UserForm uf ) {

        super( uf );
        if ( getRoles().contains( Role.ROLE_PATIENT ) || getRoles().contains( Role.ROLE_HCP ) ) {
            throw new IllegalArgumentException( "Attempted to create a Personnel record for a non-Patient user!" );
        }

        setFirstName( uf.getFirstName() );
        setLastName( uf.getLastName() );
        setAddress1( uf.getAddress1() );
        setAddress2( uf.getAddress2() );
        setCity( uf.getCity() );
        if ( null != uf.getState() ) {
            setState( State.valueOf( uf.getState() ) );
        }
        setZip( uf.getZip() );
        setPhone( uf.getPhone() );
        setEmail( uf.getEmail() );
        setMiddleName( uf.getMiddleName() );
        setNickname( uf.getNickname() );

        this.patients = new HashMap<String, boolean[]>();

    }

    /**
     * Returns patient advocate's middle name.
     *
     * @return middle name
     */
    public String getMiddleName () {
        return middleName;
    }

    /**
     * Sets the patient advocate's middle name. If it is null, sets the middle
     * name to an empty string.
     *
     * @param middleName
     *            middle name to set to
     */
    public void setMiddleName ( final String middleName ) {

        if ( middleName == null ) {

            this.middleName = "";
            return;

        }

        this.middleName = middleName;
    }

    /**
     * Returns the patient advocate's nickname.
     *
     * @return nickname
     */
    public String getNickname () {
        return nickname;
    }

    /**
     * Sets the nickname for the patient advocate. If the nickname is null, sets
     * it to an empty string.
     *
     * @param nickname
     *            the nickname of the patient advocate
     */
    public void setNickname ( final String nickname ) {

        if ( nickname == null ) {

            this.nickname = "";
            return;

        }

        this.nickname = nickname;
    }

    /**
     * Returns the map of patients that the patient advocate is assigned to.
     *
     * @return map of patients
     */
    public Map<String, boolean[]> getPatients () {
        return patients;
    }

    /**
     * Checks that a patient is valid: not null and assigned to this patient
     * advocate. Does not return and instead will throw an exception if the
     * patient is invalid.
     *
     * @throws IllegalArgumentException
     *             if p is null or not assigned to the patient advocate
     * @param p
     *            patient to check
     */
    private void checkPatient ( final Patient p ) {
        if ( p == null ) {
            throw new IllegalArgumentException( "This Patient does not exist!" );
        }
        if ( !patients.containsKey( p.getUsername() ) ) {
            throw new IllegalArgumentException( "This Patient is not assigned to this Patient Advocate!" );
        }
    }

    /**
     * Assigns the given patient to this patient advocate and adds the patient
     * to their patients map.
     *
     * @throws IllegalArgumentException
     *             if the patient is null or already in the map
     * @param addPatient
     *            patient to add to this patient advocate
     */
    public void addPatients ( final Patient addPatient ) {

        if ( addPatient == null ) {

            throw new IllegalArgumentException( "This Patient does not exist!" );

        }

        if ( patients.containsKey( addPatient.getUsername() ) ) {

            throw new IllegalArgumentException( "This Patient is already assigned to this Patient Advocate!" );

        }

        patients.put( addPatient.getUsername(), new boolean[3] );

    }

    /**
     * Unassigns the given patient from this patient advocate. Removes the
     * patient from the patients map.
     *
     * @throws IllegalArgumentException
     *             if patient is null or not in the map
     * @param removePatient
     *            patient to remove from this patient advocate
     */
    public void removePatients ( final Patient removePatient ) {

        checkPatient( removePatient );

        patients.remove( removePatient.getUsername() );

    }

    /**
     * Sets the permission of the patient advocate's bill access of the given
     * patient.
     *
     * @throws IllegalArgumentException
     *             if patient is null or not in map
     * @param patient
     *            patient to change access of
     * @param permission
     *            new permission of the patient advocate for billing information
     */
    public void setViewBilling ( final Patient patient, final boolean permission ) {
        checkPatient( patient );
        final boolean permissions[] = patients.get( patient.getUsername() );
        permissions[BILL_INDEX] = permission;
        patients.replace( patient.getUsername(), permissions );
    }

    /**
     * Sets the permission of the patient advocate's prescription access of the
     * given patient.
     *
     * @throws IllegalArgumentException
     *             if patient is null or not in map
     * @param patient
     *            patient to change access of
     * @param permission
     *            new permission of the patient advocate for prescription
     *            information
     */
    public void setViewPrescriptions ( final Patient patient, final boolean permission ) {
        checkPatient( patient );
        final boolean permissions[] = patients.get( patient.getUsername() );
        permissions[PRESCRIPTION_INDEX] = permission;
        patients.replace( patient.getUsername(), permissions );
    }

    /**
     * Sets the permission of the patient advocate's office visit access of the
     * given patient.
     *
     * @throws IllegalArgumentException
     *             if patient is null or not in map
     * @param patient
     *            patient to change access of
     * @param permission
     *            new permission of the patient advocate for office visit
     *            information
     */
    public void setViewOfficeVisits ( final Patient patient, final boolean permission ) {
        checkPatient( patient );
        final boolean permissions[] = patients.get( patient.getUsername() );
        permissions[OFFICE_VISIT_INDEX] = permission;
        patients.replace( patient.getUsername(), permissions );
    }

    /**
     * Create a new personnel based off of the PersonnelForm
     *
     * @param form
     *            the filled-in personnel form with personnel information
     * @return `this` Personnel, after updating from form
     */
    public PatientAdvocate update ( final UserForm form ) {

        setFirstName( form.getFirstName() );
        setLastName( form.getLastName() );
        setAddress1( form.getAddress1() );
        setAddress2( form.getAddress2() );
        setCity( form.getCity() );
        setState( State.valueOf( form.getState() ) );
        setZip( form.getZip() );
        setPhone( form.getPhone() );
        setEmail( form.getEmail() );
        setNickname( form.getNickname() );
        setMiddleName( form.getMiddleName() );
        super.setPassword( form.getPassword() );

        return this;
    }

}
