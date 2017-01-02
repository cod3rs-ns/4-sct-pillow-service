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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getCompanyVerified() {
        return companyVerified;
    }

    public void setCompanyVerified(String companyVerified) {
        this.companyVerified = companyVerified;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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
