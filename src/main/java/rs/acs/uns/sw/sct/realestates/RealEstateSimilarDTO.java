package rs.acs.uns.sw.sct.realestates;

public class RealEstateSimilarDTO {

    private Location location;

    private Double area;

    public RealEstateSimilarDTO(Location location, Double area) {
        this.location = location;
        this.area = area;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }
}
