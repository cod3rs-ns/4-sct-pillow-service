package rs.acs.uns.sw.sct.announcements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import rs.acs.uns.sw.sct.comments.Comment;
import rs.acs.uns.sw.sct.marks.Mark;
import rs.acs.uns.sw.sct.realestates.RealEstate;
import rs.acs.uns.sw.sct.users.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * An announcement. Entity which is center of the whole application.
 */
@Entity
@Table(name = "announcements")
public class Announcement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Double price;

    @NotNull
    @Column(nullable = false)
    private Date dateAnnounced;

    @Column
    private Date dateModified;

    @NotNull
    @Column(nullable = false)
    private Date expirationDate;

    @NotNull
    @Column(nullable = false)
    private String phoneNumber;

    @NotNull
    @Column(nullable = false)
    private String type;

    @NotNull
    @Column(nullable = false)
    private String verified;

    @NotNull
    @Column(nullable = false)
    private Boolean deleted;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn()
    private RealEstate realEstate;

    @ManyToOne
    @JoinColumn()
    private User author;

    @JsonIgnore
    @OneToMany(mappedBy = "announcement", fetch = FetchType.LAZY)
    private Set<Mark> marks = new HashSet<>(0);

    @JsonIgnore
    @OneToMany(mappedBy = "announcement", fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>(0);

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Set<Image> images = new HashSet<>(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param id announcement identifier
     * @return Announcement (this)
     */
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

    /**
     * Setter used for 'method chaining'.
     *
     * @param price announcement rice
     * @return Announcement (this)
     */
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

    /**
     * Setter used for 'method chaining'.
     *
     * @param dateAnnounced date on which announcement is created.
     * @return Announcement (this)
     */
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

    /**
     * Setter used for 'method chaining'.
     *
     * @param dateModified date on which announcement is last time modified.
     * @return Announcement (this)
     */
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


    /**
     * Setter used for 'method chaining'.
     *
     * @param expirationDate date on which announcement will expire.
     * @return Announcement (this)
     */
    public Announcement expirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
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
     * @param phoneNumber phone number of author.
     * @return Announcement (this)
     */
    public Announcement phoneNumber(String phoneNumber) {
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
     * @param type type of announcement.
     * @return Announcement (this)
     */
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

    /**
     * Setter used for 'method chaining'.
     *
     * @param verified verified status.
     * @return Announcement (this)
     */
    public Announcement verified(String verified) {
        this.verified = verified;
        return this;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param deleted representing announcement logical deletion state.
     * @return Announcement (this)
     */
    public Announcement deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public RealEstate getRealEstate() {
        return realEstate;
    }

    public void setRealEstate(RealEstate realEstate) {
        this.realEstate = realEstate;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param realEstate Real Estate object.
     * @return Announcement (this)
     */
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

    /**
     * Setter used for 'method chaining'.
     *
     * @param user author of announcement.
     * @return Announcement (this)
     */
    public Announcement author(User user) {
        this.author = user;
        return this;
    }

    public Set<Mark> getMarks() {
        return marks;
    }

    public void setMarks(Set<Mark> marks) {
        this.marks = marks;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param marks marks of announcement
     * @return Announcement (this)
     */
    public Announcement marks(Set<Mark> marks) {
        this.marks = marks;
        return this;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param comments comments of announcement
     * @return Announcement (this)
     */
    public Announcement comments(Set<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param images images of announcement
     * @return Announcement (this)
     */
    public Announcement images(Set<Image> images) {
        this.images = images;
        return this;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "id=" + id +
                ", price=" + price +
                ", dateAnnounced=" + dateAnnounced +
                ", dateModified=" + dateModified +
                ", expirationDate=" + expirationDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                ", verified='" + verified + '\'' +
                ", deleted=" + deleted +
                ", realEstate=" + realEstate +
                "}";
    }

    /**
     * Converting announcement to DTO object
     *
     * @return AnnouncementDTO (dto)
     */
    public AnnouncementDTO convertToDTO() {
        AnnouncementDTO dto = new AnnouncementDTO();

        dto.setType(this.type);
        dto.setId(id);
        dto.setExpirationDate(expirationDate);
        dto.setImages(images);
        dto.setPhoneNumber(phoneNumber);
        dto.setPrice(price);
        dto.setRealEstate(realEstate);
        dto.setAuthor(author.convertToDTO());
        dto.setVerified(verified);
        dto.dateAnnounced(dateAnnounced);

        return dto;
    }
}
