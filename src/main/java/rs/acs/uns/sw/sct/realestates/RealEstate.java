package rs.acs.uns.sw.sct.realestates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import rs.acs.uns.sw.sct.announcements.Announcement;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A real estate.
 */
@Entity
@Table(name = "real_estates")
public class RealEstate implements Serializable {

    private static final long serialVersionUID = 1L;

    // ±5% of area constants
    private static final double LOWER_LIMIT = 0.95;
    private static final double UPPER_LIMIT = 1.05;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String type;

    @NotNull
    @Column(nullable = false)
    private Double area;

    @NotNull
    @Column(nullable = false)
    private String heatingType;

    @Column
    private String equipment;

    @NotNull
    @Column(nullable = false)
    private Boolean deleted;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Location location;

    @JsonIgnore
    @OneToMany(mappedBy = "realEstate", fetch = FetchType.LAZY)
    private Set<Announcement> announcements = new HashSet<>(0);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealEstate)) return false;

        RealEstate that = (RealEstate) o;

        if (area != null ? !area.equals(that.area) : that.area != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;

        return true;

    }

    /**
     * Method that checks if two Real Estates are similar
     *
     * @param that Real Estate that needs to be checked for
     * @return boolean - true if real estates are similar, false otherwise
     */
    public boolean similar(RealEstateSimilarDTO that) {

        if (equals(that)) {
            return true;
        }

        return isLocationSimilar(that) && isAreaSimilar(that);
    }

    /**
     * Method that checks if two Real Estates' locations are similar
     *
     * @param that Real Estate that location needs to be checked for
     * @return boolean - true if real estates are on similar location, false otherwise
     */
    public boolean isLocationSimilar(RealEstateSimilarDTO that) {
        return this.getLocation().equals(that.getLocation());
    }

    /**
     * Method that checks if two Real Estates' area are similar
     *
     * @param that Real Estate that area needs to be checked for
     * @return boolean - true if real estates have similar area, false otherwise
     */
    public boolean isAreaSimilar(RealEstateSimilarDTO that) {
        return (LOWER_LIMIT * this.getArea() <= that.getArea() && that.getArea() <= UPPER_LIMIT * this.getArea())
                || (LOWER_LIMIT * that.getArea() <= this.getArea() && this.getArea() <= UPPER_LIMIT * that.getArea());
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
     * @param id real estate identifier
     * @return RealEstate (this)
     */
    public RealEstate id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param name real estate name
     * @return RealEstate (this)
     */
    public RealEstate name(String name) {
        this.name = name;
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
     * @param type real estate type
     * @return RealEstate (this)
     */
    public RealEstate type(String type) {
        this.type = type;
        return this;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param area real estate area
     * @return RealEstate (this)
     */
    public RealEstate area(Double area) {
        this.area = area;
        return this;
    }

    public String getHeatingType() {
        return heatingType;
    }

    public void setHeatingType(String heatingType) {
        this.heatingType = heatingType;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param heatingType real estate heating type
     * @return RealEstate (this)
     */
    public RealEstate heatingType(String heatingType) {
        this.heatingType = heatingType;
        return this;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param equipment real estate technical equipment
     * @return RealEstate (this)
     */
    public RealEstate equipment(String equipment) {
        this.equipment = equipment;
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
     * @param deleted real estate logical deletion status
     * @return RealEstate (this)
     */
    public RealEstate deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param location real estate location
     * @return RealEstate (this)
     */
    public RealEstate location(Location location) {
        this.location = location;
        return this;
    }

    public Set<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(Set<Announcement> announcements) {
        this.announcements = announcements;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param announcements all announcements of real estate
     * @return RealEstate (this)
     */
    public RealEstate announcements(Set<Announcement> announcements) {
        this.announcements = announcements;
        return this;
    }
}