package edu.ncsu.csc.iTrust2.forms;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Form used for creating a new User. Will be parsed into an actual User object
 * to be saved.
 *
 * @author Kai Presler-Marshall
 *
 */
public class UserForm {

    /**
     * Username of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String      username;

    /**
     * Password of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String      password;

    /***
     * Confirmation password of the user
     */
    @NotEmpty
    @Length ( min = 6, max = 20 )
    private String      password2;

    /**
     * Role of the user
     */
    @NotEmpty
    private Set<String> roles;

    /**
     * Whether the User is enabled or not
     */
    private String      enabled;

    /**
     * First name of the User
     */
    @Length ( max = 20 )
    private String      firstName;

    /**
     * Last name of the User
     */
    @Length ( max = 30 )
    private String      lastName;

    /**
     * Address line 1 of the User
     */
    @Length ( max = 50 )
    private String      address1;

    /**
     * Address line 2 of the User
     */
    @Length ( max = 50 )
    private String      address2;

    /**
     * City of the User
     */
    @Length ( max = 15 )
    private String      city;

    /**
     * State of the User
     */
    @Length ( min = 2, max = 2 )
    private String      state;

    /**
     * Zip code of the User
     */
    @Length ( min = 5, max = 10 )
    private String      zip;

    /**
     * Phone number of the User
     */
    @Pattern ( regexp = "(^[0-9]{3}-[0-9]{3}-[0-9]{4}$)", message = "Phone number must be formatted as xxx-xxx-xxxx" )
    private String      phone;

    /**
     * Email of the User
     */
    @Length ( max = 30 )
    private String      email;

    /**
     * Middle name of the User
     */
    @Length ( max = 20 )
    private String      middleName;

    /**
     * Nickname of the User
     */
    @Length ( max = 20 )
    private String      nickname;

    /**
     * Empty constructor used to generate a blank form for the user to fill out.
     */
    public UserForm () {
    }

    /**
     * Create a UserForm from all of its fields. All inputs are strings.
     *
     * @param username
     *            Username of the new user.
     * @param password
     *            Password of the new user
     * @param role
     *            Role of the new User
     * @param enabled
     *            Whether the new User is enabled or not
     *
     */
    public UserForm ( final String username, final String password, final String role, final String enabled ) {
        setUsername( username );
        setPassword( password );
        setPassword2( password );
        addRole( role );
        setEnabled( enabled );
    }

    /**
     * Create a UserForm from all of its fields. All inputs are strings.
     *
     * @param username
     *            Username of the new user.
     * @param password
     *            Password of the new user
     * @param role
     *            Role of the new User
     * @param enabled
     *            Whether the new User is enabled or not
     * @param firstName
     *            first name of the user
     * @param lastName
     *            last name of the user
     * @param address1
     *            address line 1 of the user
     * @param address2
     *            address line 2 of the user
     * @param city
     *            city of address of the user
     * @param state
     *            state of address of the user
     * @param zip
     *            zip code of the user
     * @param phone
     *            phone number of the user
     * @param email
     *            email of the user
     * @param middleName
     *            middle name of the user
     * @param nickname
     *            nickname of the user
     *
     */
    public UserForm ( final String username, final String password, final String role, final String enabled,
            final String firstName, final String lastName, final String address1, final String address2,
            final String city, final String state, final String zip, final String phone, final String email,
            final String middleName, final String nickname ) {
        setUsername( username );
        setPassword( password );
        setPassword2( password );
        addRole( role );
        setEnabled( enabled );
        setFirstName( firstName );
        setLastName( lastName );
        setAddress1( address1 );
        setAddress2( address2 );
        setCity( city );
        setState( state );
        setZip( zip );
        setPhone( phone );
        setEmail( email );
        setMiddleName( middleName );
        setNickname( nickname );
    }

    /**
     * Create a new UserForm from all of its fields.
     *
     * @param username
     *            Username of the new user
     * @param password
     *            Password of the new user
     * @param role
     *            Role (Role Enum) of the new user
     * @param enabled
     *            Whether the user is enabled; 1 for enabled, 0 for disabled.
     */
    public UserForm ( final String username, final String password, final Role role, final Integer enabled ) {
        this( username, password, role.toString(), enabled != 0 ? "true" : null );
    }

    /**
     * Create a UserForm from the User object provided. This unfortunately
     * cannot fill out the password as the password cannot be un-hashed.
     *
     * @param u
     *            User object to convert to a UserForm.
     */
    public UserForm ( final User u ) {
        setUsername( u.getUsername() );
        setRoles( u.getRoles().stream().map( e -> e.toString() ).collect( Collectors.toSet() ) );
        setEnabled( u.getEnabled().toString() );
    }

    /**
     * Get the Username of the User for the form
     *
     * @return The Username of the User
     */
    public String getUsername () {
        return username;
    }

    /**
     * Sets a new Username for the User on the form
     *
     * @param username
     *            New Username to set
     */
    public void setUsername ( final String username ) {
        this.username = username;
    }

    /**
     * Gets the Password provided in the form
     *
     * @return Password provided
     */
    public String getPassword () {
        return password;
    }

    /**
     * Sets the Password for the User on the form.
     *
     * @param password
     *            Password of the user
     */
    public void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * Gets the Password confirmation provided in the form
     *
     * @return Password confirmation provided
     */
    public String getPassword2 () {
        return password2;
    }

    /**
     * Sets the Password confirmation in the Form
     *
     * @param password2
     *            The password confirmation
     */
    public void setPassword2 ( final String password2 ) {
        this.password2 = password2;
    }

    /**
     * Role of the new User
     *
     * @return The User's role
     */
    public Set<String> getRoles () {
        return roles;
    }

    /**
     * Sets the Role of the new User
     *
     * @param roles
     *            Roles of the user
     */
    public void setRoles ( final Set<String> roles ) {
        this.roles = roles;
    }

    /**
     * Adds the provided Role to this User
     *
     * @param role
     *            The Role to add
     */
    public void addRole ( final String role ) {
        if ( null == this.roles ) {
            this.roles = new HashSet<String>();
        }
        this.roles.add( role );
    }

    /**
     * Gets whether the new User created is to be enabled or not
     *
     * @return Whether the User is enabled
     */
    public String getEnabled () {
        return enabled;
    }

    /**
     * Retrieves whether the user is enabled or not
     *
     * @param enabled
     *            New Enabled setting
     */
    public void setEnabled ( final String enabled ) {
        this.enabled = enabled;
    }

    /**
     * Returns first name of the user
     *
     * @return First name
     */
    public String getFirstName () {
        return firstName;
    }

    /**
     * Sets the first name of the User
     *
     * @param firstName
     *            First name
     */
    public void setFirstName ( final String firstName ) {
        this.firstName = firstName;
    }

    /**
     * Returns last name of the user
     *
     * @return Last name
     */
    public String getLastName () {
        return lastName;
    }

    /**
     * Sets the last name of the user
     *
     * @param lastName
     *            Last name
     */
    public void setLastName ( final String lastName ) {
        this.lastName = lastName;
    }

    /**
     * Returns address line 1 of the user
     *
     * @return Address line 1
     */
    public String getAddress1 () {
        return address1;
    }

    /**
     * Sets address line 1 of the user
     *
     * @param address1
     *            Address line 1
     */
    public void setAddress1 ( final String address1 ) {
        this.address1 = address1;
    }

    /**
     * Returns address line 2 of the user
     *
     * @return Address line 2
     */
    public String getAddress2 () {
        return address2;
    }

    /**
     * Sets address line 2 of the user
     *
     * @param address2
     *            Address line 2
     */
    public void setAddress2 ( final String address2 ) {
        this.address2 = address2;
    }

    /**
     * Returns city of the user
     *
     * @return City
     */
    public String getCity () {
        return city;
    }

    /**
     * Sets city of the user
     *
     * @param city
     *            City
     */
    public void setCity ( final String city ) {
        this.city = city;
    }

    /**
     * Returns state of the user
     *
     * @return State
     */
    public String getState () {
        return state;
    }

    /**
     * Sets state of the user
     *
     * @param state
     *            State
     */
    public void setState ( final String state ) {
        this.state = state;
    }

    /**
     * Returns zip code of the user
     *
     * @return Zip code
     */
    public String getZip () {
        return zip;
    }

    /**
     * Sets zip code of the user
     *
     * @param zip
     *            Zip code
     */
    public void setZip ( final String zip ) {
        this.zip = zip;
    }

    /**
     * Returns phone number of the user
     *
     * @return Phone number
     */
    public String getPhone () {
        return phone;
    }

    /**
     * Sets phone number of the user
     *
     * @param phone
     *            Phone number
     */
    public void setPhone ( final String phone ) {
        this.phone = phone;
    }

    /**
     * Returns email of the user
     *
     * @return Email
     */
    public String getEmail () {
        return email;
    }

    /**
     * Sets email of the user
     *
     * @param email
     *            Email
     */
    public void setEmail ( final String email ) {
        this.email = email;
    }

    /**
     * Returns middle name of the user
     *
     * @return Middle name
     */
    public String getMiddleName () {
        return middleName;
    }

    /**
     * Sets middle name of the user
     *
     * @param middleName
     *            Middle name
     */
    public void setMiddleName ( final String middleName ) {
        this.middleName = middleName;
    }

    /**
     * Returns nickname of the user
     *
     * @return Nickname
     */
    public String getNickname () {
        return nickname;
    }

    /**
     * Sets nickname of the user
     *
     * @param nickname
     *            Nickname
     */
    public void setNickname ( final String nickname ) {
        this.nickname = nickname;
    }

}
