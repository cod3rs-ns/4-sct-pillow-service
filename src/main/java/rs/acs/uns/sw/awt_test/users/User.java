package rs.acs.uns.sw.awt_test.users;


import com.fasterxml.jackson.annotation.JsonIgnore;
import rs.acs.uns.sw.awt_test.companies.Company;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
@PrimaryKeyJoinColumn(name="u_id")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "u_id")
    private Integer id;

    @Column(name = "u_email")
    private String email;

    @JsonIgnore
    @Column(name = "u_password")
    private String password;

    @Column(name = "u_fname")
    private String firstName;

    @Column(name = "u_lname")
    private String lastName;

    @Column(name = "u_telephone")
    private String telephoneNo;

    @Column(name = "u_type")
    private String type;

    @ManyToOne
    @JoinColumn(name = "u_company")
    private Company company;

    @Column(name = "u_company_verified")
    private String companyVerified;

    @Column(name = "u_deleted")
    private Boolean deleted;

    public User() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getTelephoneNo() {
        return telephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompanyVerified() {
        return companyVerified;
    }

    public void setCompanyVerified(String companyVerified) {
        this.companyVerified = companyVerified;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
