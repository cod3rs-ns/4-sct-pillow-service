package rs.acs.uns.sw.awt_test.companies;

import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
@PrimaryKeyJoinColumn(name = "co_id")
public class Company implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "co_id")
    private Long id;

    @NotNull
    @Column(name = "co_name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "co_address", nullable = false)
    private String address;

    @NotNull
    @Column(name = "co_telephone", nullable = false)
    private String telephoneNo;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>(0);

    public Company() {
    }

    public Company(Long id, String name, String address, String telephoneNo, Set<User> users) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephoneNo = telephoneNo;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Company name(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Company address(String address) {
        this.address = address;
        return this;
    }

    public String getTelephoneNo() {
        return telephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }

    public Company telephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
        return this;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
