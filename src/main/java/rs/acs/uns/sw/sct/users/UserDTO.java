package rs.acs.uns.sw.sct.users;

import rs.acs.uns.sw.sct.companies.Company;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * User Data Transfer Object.
 *
 * @see User
 */
public class UserDTO implements Serializable {

    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String type;

    private Company company;

    private String companyVerified;

    private String imagePath;

    /**
     * Converts DTO to User entity
     *
     * @return User for further use
     */
    public User convertToUser() {
        return new User()
                .id(id)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .type(type)
                .company(company)
                .companyVerified(companyVerified)
                .imagePath(imagePath)
                .verified(false)
                .deleted(false);

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param id    User id
     * @return UserDTO (this)
     */
    public UserDTO id(Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param username    User username
     * @return UserDTO (this)
     */
    public UserDTO username(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param email    User email
     * @return UserDTO (this)
     */
    public UserDTO email(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param firstName    User first name
     * @return UserDTO (this)
     */
    public UserDTO firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param lastName    User last name
     * @return UserDTO (this)
     */
    public UserDTO lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param phoneNumber    User's phone number
     * @return UserDTO (this)
     */
    public UserDTO phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param type    User's type - admin, advertiser or verifier
     * @return UserDTO (this)
     */
    public UserDTO type(String type) {
        this.type = type;
        return this;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param company    User's company
     * @return UserDTO (this)
     */
    public UserDTO company(Company company) {
        this.company = company;
        return this;
    }

    public String getCompanyVerified() {
        return companyVerified;
    }

    public void setCompanyVerified(String companyVerified) {
        this.companyVerified = companyVerified;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param companyVerified    Is user verified in company
     * @return UserDTO (this)
     */
    public UserDTO companyVerified(String companyVerified) {
        this.companyVerified = companyVerified;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param imagePath    User's profile image path
     * @return UserDTO (this)
     */
    public UserDTO imagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                ", company=" + company +
                ", companyVerified='" + companyVerified + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
