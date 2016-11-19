package rs.acs.uns.sw.awt_test.real_estates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import rs.acs.uns.sw.awt_test.announcements.Announcement;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "real_estates")
public class RealEstate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "re_id")
    private Long id;

    @NotNull
    @Column(name = "re_name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "re_type", nullable = false)
    private String type;

    @NotNull
    @Column(name = "re_area", nullable = false)
    private Double area;

    @NotNull
    @Column(name = "re_heating_type", nullable = false)
    private String heatingType;

    @NotNull
    @Column(name = "re_deleted", nullable = false)
    private Boolean deleted;

    @JsonIgnore
    @OneToMany(mappedBy = "realEstate", fetch = FetchType.LAZY)
    private Set<Announcement> announcements = new HashSet<>(0);

    public RealEstate() {
    }

    public RealEstate(Long id, String name, String type, Double area, String heatingType, Set<Announcement> announcements, Boolean deleted) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.area = area;
        this.heatingType = heatingType;
        this.announcements = announcements;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public RealEstate heatingType(String heatingType) {
        this.heatingType = heatingType;
        return this;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public RealEstate deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Set<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(Set<Announcement> announcements) {
        this.announcements = announcements;
    }
}