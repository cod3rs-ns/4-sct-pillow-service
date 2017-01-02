package rs.acs.uns.sw.sct.announcements;

import rs.acs.uns.sw.sct.realestates.RealEstate;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserDTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Announcement Data Transfer Object.
 *
 * @see Announcement
 */
public class AnnouncementDTO implements Serializable {

    private Long id;

    @NotNull
    private Double price;

    @NotNull
    private Date dateAnnounced;

    @NotNull
    private Date expirationDate;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String type;

    @NotNull
    private String verified;

    private UserDTO author;

    private RealEstate realEstate;

    private Set<Image> images = new HashSet<>(0);

    /**
     * Converts DTO to Announcement entity
     *
     * @param user author of the announcement
     * @return announcement for further use
     */
    public Announcement convertToAnnouncement(User user) {
        return new Announcement()
                .id(id)
                .author(user)
                .images(images)
                .price(price)
                .dateAnnounced(new Date())
                .expirationDate(expirationDate)
                .phoneNumber(phoneNumber)
                .type(type)
                .deleted(false)
                .verified("not-verified")
                .realEstate(realEstate);

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
     * @param id announcement identifier
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO id(Long id) {
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
     * @param price announcement identifier
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO price(Double price) {
        this.price = price;
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
     * @param expirationDate announcement identifier
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO expirationDate(Date expirationDate) {
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
     * @param phoneNumber announcement identifier
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO phoneNumber(String phoneNumber) {
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
     * @param type announcement identifier
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO type(String type) {
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
     * @param verified announcement verification status
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO verified(String verified) {
        this.verified = verified;
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
     * @param realEstate announcement identifier
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO realEstate(RealEstate realEstate) {
        this.realEstate = realEstate;
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
     * @param images announcement identifier
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO images(Set<Image> images) {
        this.images = images;
        return this;
    }

    public UserDTO getAuthor() {
        return author;
    }

    public void setAuthor(UserDTO author) {
        this.author = author;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param author Announcement's author
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO author(UserDTO author) {
        this.author = author;
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
     * @param dateAnnounced Announcement's announced date
     * @return AnnouncementDTO (this)
     */
    public AnnouncementDTO dateAnnounced(Date dateAnnounced) {
        this.dateAnnounced = dateAnnounced;
        return this;
    }
}
