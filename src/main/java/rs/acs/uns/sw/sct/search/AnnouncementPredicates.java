package rs.acs.uns.sw.sct.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import rs.acs.uns.sw.sct.announcements.QAnnouncement;

/**
 * AnnouncementPredicates used for resolving search criteria from
 *
 * @see AnnouncementSearchWrapper
 * to allow search for announcements functionality.
 */
public final class AnnouncementPredicates {

    private AnnouncementPredicates() {
    }

    /**
     * Creates predicate on based on wrapper.
     *
     * @param searchWrapper announcement wrapper which contains announcement fields used as search criteria
     * @return search predicate
     */
    public static Predicate search(AnnouncementSearchWrapper searchWrapper) {
        BooleanBuilder where = new BooleanBuilder();

        QAnnouncement announcement = QAnnouncement.announcement;
        if (searchWrapper.getStartPrice() != null || searchWrapper.getEndPrice() != null)
            where.and(announcement.price.between(searchWrapper.getStartPrice(), searchWrapper.getEndPrice()));
        if (searchWrapper.getPhoneNumber() != null)
            where.and(announcement.phoneNumber.containsIgnoreCase(searchWrapper.getPhoneNumber()));
        if (searchWrapper.getType() != null)
            where.and(announcement.type.containsIgnoreCase(searchWrapper.getType()));
        if (searchWrapper.getAuthorName() != null)
            where.and(announcement.author.firstName.containsIgnoreCase(searchWrapper.getAuthorName()));
        if (searchWrapper.getAuthorSurname() != null)
            where.and(announcement.author.lastName.containsIgnoreCase(searchWrapper.getAuthorSurname()));
        if (searchWrapper.getStartArea() != null || searchWrapper.getEndArea() != null)
            where.and(announcement.realEstate.area.between(searchWrapper.getStartArea(), searchWrapper.getEndArea()));
        if (searchWrapper.getHeatingType() != null)
            where.and(announcement.realEstate.heatingType.containsIgnoreCase(searchWrapper.getHeatingType()));
        if (searchWrapper.getName() != null)
            where.and(announcement.name.containsIgnoreCase(searchWrapper.getName()));
        if (searchWrapper.getCity() != null)
            where.and(announcement.realEstate.location.city.containsIgnoreCase(searchWrapper.getCity()));
        if (searchWrapper.getCityRegion() != null)
            where.and(announcement.realEstate.location.cityRegion.containsIgnoreCase(searchWrapper.getCityRegion()));
        if (searchWrapper.getCountry() != null)
            where.and(announcement.realEstate.location.country.containsIgnoreCase(searchWrapper.getCountry()));
        if (searchWrapper.getStreet() != null)
            where.and(announcement.realEstate.location.street.containsIgnoreCase(searchWrapper.getStreet()));
        if (searchWrapper.getStreetNumber() != null)
            where.and(announcement.realEstate.location.streetNumber.equalsIgnoreCase(searchWrapper.getStreetNumber()));
        if (searchWrapper.getIntercom() != null)
            where.and(announcement.realEstate.intercom.eq(searchWrapper.getIntercom()));
        if (searchWrapper.getInternet() != null)
            where.and(announcement.realEstate.internet.eq(searchWrapper.getInternet()));
        if (searchWrapper.getPhone() != null)
            where.and(announcement.realEstate.phone.eq(searchWrapper.getPhone()));
        if (searchWrapper.getAirConditioner() != null)
            where.and(announcement.realEstate.airConditioner.eq(searchWrapper.getAirConditioner()));
        if (searchWrapper.getVideoSurveillance() != null)
            where.and(announcement.realEstate.videoSurveillance.eq(searchWrapper.getVideoSurveillance()));
        if (searchWrapper.getCableTV() != null)
            where.and(announcement.realEstate.cableTV.eq(searchWrapper.getCableTV()));

        where.and(announcement.deleted.eq(false));

        return where;
    }
}
