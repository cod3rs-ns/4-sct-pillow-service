package rs.acs.uns.sw.sct.realestates;

import com.google.common.base.Objects;

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

    @NotNull
    @Column(nullable = false)
    private Double latitude;

    @NotNull
    @Column(nullable = false)
    private Double longitude;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        if (country != null ? !country.toLowerCase().contains(location.country.trim().toLowerCase()) : location.country != null) return false;
        if (city != null ? !city.toLowerCase().contains(location.city.trim().toLowerCase()) : location.city != null) return false;
        if (cityRegion != null ? !cityRegion.toLowerCase().contains(location.cityRegion.trim().toLowerCase()) : location.cityRegion != null) return false;
        if (street != null ? !street.toLowerCase().contains(location.street.trim().toLowerCase()) : location.street != null) return false;
        return streetNumber != null ? streetNumber.equals(location.streetNumber.toLowerCase()) : location.streetNumber == null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId(), this.getLongitude(), this.getLatitude());
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param latitude Location latitude
     * @return Location (this)
     */
    public Location latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param longitude Location latitude
     * @return Location (this)
     */
    public Location longitude(Double longitude) {
        this.longitude = longitude;
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

    /**
     * Method which returns if location is in provided area
     *
     * @param x1    Top right corner longitude
     * @param y1    Top right corner latitude
     * @param x2    Bottom left corner longitude
     * @param y2    Bottom left corner latitude
     * @return Boolean (true - if is in Area, false otherwise)
     */
    public Boolean isInArea(Double x1, Double y1, Double x2, Double y2) {

        Boolean isX = (x2 <= this.longitude) && (this.longitude <= x1);
        Boolean isY = (y2 <= this.latitude) && (this.latitude <= y1);

        return isX && isY;
    }
}
