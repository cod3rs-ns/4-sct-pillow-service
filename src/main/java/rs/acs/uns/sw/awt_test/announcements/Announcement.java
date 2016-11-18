package rs.acs.uns.sw.awt_test.announcements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import rs.acs.uns.sw.awt_test.comments.Comment;
import rs.acs.uns.sw.awt_test.marks.Mark;
import rs.acs.uns.sw.awt_test.real_estates.RealEstate;
import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "announcements")
@PrimaryKeyJoinColumn(name = "ann_id")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ann_id")
    private Long id;

    @NotNull
    @Column(name = "ann_price", nullable = false)
    private Double price;

    @NotNull
    @Column(name = "ann_date_announced", nullable = false)
    private Date dateAnnounced;

    @Column(name = "ann_date_modified")
    private Date dateModified;

    @NotNull
    @Column(name = "ann_expiration_date", nullable = false)
    private Date expirationDate;

    @NotNull
    @Column(name = "ann_telephone", nullable = false)
    private String telephoneNo;

    @NotNull
    @Column(name = "ann_type", nullable = false)
    private String type;

    @Column(name = "ann_verified")
    private String verified;

    @ManyToOne
    @JoinColumn(name = "ann_real_estate_id")
    private RealEstate realEstate;

    @ManyToOne
    @JoinColumn(name = "re_author_id")
    private User author;

    @JsonIgnore
    @OneToMany(mappedBy = "announcement", fetch = FetchType.LAZY)
    private Set<Mark> marks = new HashSet<>(0);

    @OneToMany(mappedBy = "announcement", fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>(0);

    @Column(name = "ann_deleted")
    private Boolean deleted;

    public Announcement() {
    }

    public Announcement(Long id, Double price, Date dateAnnounced, Date dateModified, Date expirationDate, String telephoneNo, String type, String verified, RealEstate realEstate, User author, Set<Mark> marks, Set<Comment> comments, Boolean deleted) {
        this.id = id;
        this.price = price;
        this.dateAnnounced = dateAnnounced;
        this.dateModified = dateModified;
        this.expirationDate = expirationDate;
        this.telephoneNo = telephoneNo;
        this.type = type;
        this.verified = verified;
        this.realEstate = realEstate;
        this.author = author;
        this.marks = marks;
        this.comments = comments;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Announcement id(Long id) {
        this.id = id;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Announcement price(Double price) {
        this.price = price;
        return this;
    }

    public Date getDateAnnounced() {
        return dateAnnounced;
    }

    public void setDateAnnounced(Date dateAnnounced) {
        this.dateAnnounced = dateAnnounced;
    }

    public Announcement dateAnnounced(Date dateAnnounced) {
        this.dateAnnounced = dateAnnounced;
        return this;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Announcement dateModified(Date dateModified) {
        this.dateModified = dateModified;
        return this;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Announcement expirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public String getTelephoneNo() {
        return telephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }

    public Announcement telephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Announcement type(String type) {
        this.type = type;
        return this;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public Announcement verified(String verified) {
        this.verified = verified;
        return this;
    }

    public RealEstate getRealEstate() {
        return realEstate;
    }

    public void setRealEstate(RealEstate realEstate) {
        this.realEstate = realEstate;
    }

    public Announcement realEstate(RealEstate realEstate) {
        this.realEstate = realEstate;
        return this;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public Announcement author(User user) {
        this.author = user;
        return this;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Set<Mark> getMarks() {
        return marks;
    }

    public void setMarks(Set<Mark> marks) {
        this.marks = marks;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

}
