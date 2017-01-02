package rs.acs.uns.sw.sct.verification;

import rs.acs.uns.sw.sct.users.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Token used for user verification.
 */
@Entity
@Table(name = "verification_tokens")
public class VerificationToken implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "vt_id")
    private Long id;

    @Column(name = "vt_token", columnDefinition = "char(40)")
    private String token;

    @Column(name = "vt_expiry_date")
    private Date expiryDate;

    @JoinColumn(name = "vt_user_id")
    @OneToOne(cascade = CascadeType.REFRESH)
    private User user;

    public VerificationToken(){

    }

    /**
     * Constructor
     *
     * @param token token value
     * @param date  date of expiration
     * @param user  user for whom token is created
     */
    public VerificationToken(String token, Date date, User user) {
        super();
        this.token = token;
        this.expiryDate = date;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
