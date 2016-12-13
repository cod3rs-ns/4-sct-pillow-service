package rs.acs.uns.sw.sct.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import rs.acs.uns.sw.sct.announcements.QAnnouncement;

public final class AnnouncementPredicates {

    private AnnouncementPredicates() {
    }

    public static Predicate search(AnnouncementSearchWrapper searchWrapper) {
        BooleanBuilder where = new BooleanBuilder();

        QAnnouncement announcement = QAnnouncement.announcement;
        if (searchWrapper.getStartPrice() != null || searchWrapper.getEndPrice() != null)
            where.and(announcement.price.between(searchWrapper.getStartPrice(), searchWrapper.getEndPrice()));
        if (searchWrapper.getPhoneNumber() != null)
            where.and(announcement.phoneNumber.toLowerCase().contains(searchWrapper.getPhoneNumber().toLowerCase()));
        if (searchWrapper.getType() != null)
            where.and(announcement.type.toLowerCase().contains(searchWrapper.getType().toLowerCase()));
        if (searchWrapper.getAuthorName() != null)
            where.and(announcement.author.firstName.toLowerCase().contains(searchWrapper.getAuthorName().toLowerCase()));
        if (searchWrapper.getAuthorSurname() != null)
            where.and(announcement.author.lastName.toLowerCase().contains(searchWrapper.getAuthorSurname().toLowerCase()));
        if (searchWrapper.getStartArea() != null || searchWrapper.getEndArea() != null)
            where.and(announcement.realEstate.area.between(searchWrapper.getStartArea(), searchWrapper.getEndArea()));
        if (searchWrapper.getHeatingType() != null)
            where.and(announcement.realEstate.heatingType.toLowerCase().contains(searchWrapper.getHeatingType().toLowerCase()));
        if (searchWrapper.getName() != null)
            where.and(announcement.realEstate.name.toLowerCase().contains(searchWrapper.getName().toLowerCase()));
        if (searchWrapper.getCity() != null)
            where.and(announcement.realEstate.location.city.toLowerCase().contains(searchWrapper.getCity().toLowerCase()));
        if (searchWrapper.getCityRegion() != null)
            where.and(announcement.realEstate.location.cityRegion.toLowerCase().contains(searchWrapper.getCityRegion().toLowerCase()));
        if (searchWrapper.getCountry() != null)
            where.and(announcement.realEstate.location.country.toLowerCase().contains(searchWrapper.getCountry().toLowerCase()));
        if (searchWrapper.getStreet() != null)
            where.and(announcement.realEstate.location.street.toLowerCase().contains(searchWrapper.getStreet().toLowerCase()));

        return where;
    }
}
