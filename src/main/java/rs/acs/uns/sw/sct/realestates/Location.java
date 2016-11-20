package rs.acs.uns.sw.sct.realestates;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A location
 */
@Entity
@Table(name = "locations")
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String country;

    @NotNull
    @Column(nullable = false)
    private String city;

    @Column()
    private String cityRegion;

    @NotNull
    @Column(nullable = false)
    private String street;

    @NotNull
    @Column(nullable = false)
    private String streetNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param id location identifier
     * @return Location (this)
     */
    public Location id(Long id) {
        this.id = id;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param country country name
     * @return Location (this)
     */
    public Location country(String country) {
        this.country = country;
        return this;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param city city name
     * @return Location (this)
     */
    public Location city(String city) {
        this.city = city;
        return this;
    }

    public String getCityRegion() {
        return cityRegion;
    }

    public void setCityRegion(String cityRegion) {
        this.cityRegion = cityRegion;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param cityRegion city region name
     * @return Location (this)
     */
    public Location cityRegion(String cityRegion) {
        this.cityRegion = cityRegion;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param street street name
     * @return Location (this)
     */
    public Location street(String street) {
        this.street = street;
        return this;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param streetNumber street number
     * @return Location (this)
     */
    public Location streetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        return this;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", cityRegion='" + cityRegion + '\'' +
                ", street='" + street + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                '}';
    }
}
