package rs.acs.uns.sw.awt_test.announcements;

import rs.acs.uns.sw.awt_test.comments.Comment;
import rs.acs.uns.sw.awt_test.marks.Mark;
import rs.acs.uns.sw.awt_test.real_estates.RealEstate;
import rs.acs.uns.sw.awt_test.users.User;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "announcements")
@PrimaryKeyJoinColumn(name = "ann_id")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ann_id")
    private Long id;

    @Column(name = "ann_price")
    private Double price;

    @Column(name = "ann_date_announced")
    private Date dateAnnounced;

    @Column(name = "ann_date_modified")
    private Date dateModified;

    @Column(name = "ann_expiration_date")
    private Date expirationDate;

    @Column(name = "ann_telephone")
    private String telephoneNo;

    @Column(name = "ann_type")
    private String type;

    @Column(name = "ann_verified")
    private String verified;

    @ManyToOne
    @JoinColumn(name = "ann_real_estate_id")
    private RealEstate realEstate;

    @ManyToOne
    @JoinColumn(name = "re_author_id")
    private User author;

    @OneToMany(mappedBy = "announcement", fetch = FetchType.LAZY)
    private Set<Mark> marks = new HashSet<>(0);

    @OneToMany(mappedBy = "announcement", fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>(0);

    @Column(name = "ann_deleted")
    private Boolean deleted;

    public Announcement() {
    }

    public Announcement(Double price, Date dateAnnounced, Date dateModified, Date expirationDate, String telephoneNo, String type, String verified, RealEstate realEstate, User author, Set<Mark> marks, Set<Comment> comments, Boolean deleted) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getDateAnnounced() {
        return dateAnnounced;
    }

    public void setDateAnnounced(Date dateAnnounced) {
        this.dateAnnounced = dateAnnounced;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
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

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public RealEstate getRealEstate() {
        return realEstate;
    }

    public void setRealEstate(RealEstate realEstate) {
        this.realEstate = realEstate;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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
