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

    @NotNull
    @Column(nullable = false)
    private Boolean deleted;

    @JsonIgnore
    @OneToMany(mappedBy = "realEstate", fetch = FetchType.LAZY)
    private Set<Announcement> announcements = new HashSet<>(0);

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
    
    public Set<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(Set<Announcement> announcements) {
        this.announcements = announcements;
    }
}