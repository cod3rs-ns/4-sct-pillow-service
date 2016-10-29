package rs.acs.uns.sw.awt_test.real_estates;

import rs.acs.uns.sw.awt_test.announcements.Announcement;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "real_estates")
@PrimaryKeyJoinColumn(name="re_id")
public class RealEstate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "re_id")
    private Integer id;

    @Column(name = "re_name")
    private String name;

    @Column(name = "re_type")
    private String type;

    @Column(name = "re_area")
    private Double area;

    @Column(name = "re_heating_type")
    private String heatingType;

    @OneToMany(mappedBy = "realEstate", fetch = FetchType.LAZY)
    private Set<Announcement> announcements = new HashSet<>(0);

    @Column(name = "re_deleted")
    private Boolean deleted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getHeatingType() {
        return heatingType;
    }

    public void setHeatingType(String heatingType) {
        this.heatingType = heatingType;
    }

    public Set<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(Set<Announcement> announcements) {
        this.announcements = announcements;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}