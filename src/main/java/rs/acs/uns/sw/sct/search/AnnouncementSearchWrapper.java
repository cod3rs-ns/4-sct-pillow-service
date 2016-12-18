package rs.acs.uns.sw.sct.search;

/**
 * Object that wraps all search criteria attributes.
 */
public class AnnouncementSearchWrapper {
    private Double startPrice;
    private Double endPrice;
    private String phoneNumber;
    private String type;
    private String authorName;
    private String authorSurname;
    private Double startArea;
    private Double endArea;
    private String heatingType;
    private String name;
    private String country;
    private String cityRegion;
    private String city;
    private String street;
    private String streetNumber;

    /**
     * Setter used for 'method chaining'.
     *
     * @param startPrice grater than this price
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper startPrice(Double startPrice) {
        this.startPrice = startPrice;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param endPrice less than this price
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper endPrice(Double endPrice) {
        this.endPrice = endPrice;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param phoneNumber contains phone number
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param type contains type
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param authorName contains author name
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper authorName(String authorName) {
        this.authorName = authorName;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param authorSurname contains author surname
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper authorSurname(String authorSurname) {
        this.authorSurname = authorSurname;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param startArea grater than start area
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper startArea(Double startArea) {
        this.startArea = startArea;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param endArea less than start area
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper endArea(Double endArea) {
        this.endArea = endArea;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param heatingType contains heating type
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper heatingType(String heatingType) {
        this.heatingType = heatingType;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param name contains name
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param country contains country
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper country(String country) {
        this.country = country;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param city contains city
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper city(String city) {
        this.city = city;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param cityRegion contains city region
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper cityRegion(String cityRegion) {
        this.cityRegion = cityRegion;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param street contains city street
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper street(String street) {
        this.street = street;
        return this;
    }

    /**
     * Setter used for 'method chaining'.
     *
     * @param streetNumber contains street number
     * @return AnnouncementSearchWrapper (this)
     */
    public AnnouncementSearchWrapper streetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        return this;
    }

    public Double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(Double startPrice) {
        this.startPrice = startPrice;
    }

    public Double getEndPrice() {
        return endPrice;
    }

    public void setEndPrice(Double endPrice) {
        this.endPrice = endPrice;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorSurname() {
        return authorSurname;
    }

    public void setAuthorSurname(String authorSurname) {
        this.authorSurname = authorSurname;
    }

    public Double getStartArea() {
        return startArea;
    }

    public void setStartArea(Double startArea) {
        this.startArea = startArea;
    }

    public Double getEndArea() {
        return endArea;
    }

    public void setEndArea(Double endArea) {
        this.endArea = endArea;
    }

    public String getHeatingType() {
        return heatingType;
    }

    public void setHeatingType(String heatingType) {
        this.heatingType = heatingType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCityRegion() {
        return cityRegion;
    }

    public void setCityRegion(String cityRegion) {
        this.cityRegion = cityRegion;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }
}
