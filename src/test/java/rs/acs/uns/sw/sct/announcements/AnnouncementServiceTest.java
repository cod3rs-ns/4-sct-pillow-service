package rs.acs.uns.sw.sct.announcements;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.constants.AnnouncementConstants;
import rs.acs.uns.sw.sct.realestates.Location;
import rs.acs.uns.sw.sct.realestates.RealEstate;
import rs.acs.uns.sw.sct.search.AnnouncementSearchWrapper;

import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.AnnouncementConstants.*;
import static rs.acs.uns.sw.sct.constants.CompanyConstants.PAGEABLE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
@ActiveProfiles("test")
public class AnnouncementServiceTest {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    private Announcement newAnnouncement;
    private Announcement updatedAnnouncement;
    private Announcement existingAnnouncement;

    /**
     * Asserts equality of two Announcements.
     *
     * @param ann1 One of the Announcements to be compared
     * @param ann2 The other Announcement to be compared
     */
    private void compareAnnouncements(Announcement ann1, Announcement ann2) {
        if (ann1.getId() != null && ann2.getId() != null)
            assertThat(ann1.getId()).isEqualTo(ann2.getId());
        assertThat(ann1.getPrice()).isEqualTo(ann2.getPrice());
        if (ann1.getDateAnnounced() != null && ann2.getDateAnnounced() != null)
            assertThat(ann1.getDateAnnounced()).isEqualTo(ann2.getDateAnnounced());
        if (ann1.getDateModified() != null && ann2.getDateModified() != null)
            assertThat(ann1.getDateModified()).isEqualTo(ann2.getDateModified());
        if (ann1.getExpirationDate() != null && ann2.getExpirationDate() != null)
            assertThat(ann1.getExpirationDate()).isEqualTo(ann2.getExpirationDate());
        assertThat(ann1.getPhoneNumber()).isEqualTo(ann2.getPhoneNumber());
        assertThat(ann1.getVerified()).isEqualTo(ann2.getVerified());
        if (ann1.getRealEstate() != null && ann2.getRealEstate() != null)
            assertThat(ann1.getRealEstate().getId()).isEqualTo(ann2.getRealEstate().getId());
        assertThat(ann1.getAuthor().getId()).isEqualTo(ann2.getAuthor().getId());
    }

    /**
     * Initializes all objects required for testing
     */
    @Before
    public void initTest() {
        newAnnouncement = createNewEntity();
        existingAnnouncement = new Announcement()
                .id(ID)
                .price(PRICE)
                .dateAnnounced(DATE_ANNOUNCED)
                .dateModified(DATE_MODIFIED)
                .expirationDate(EXPIRATION_DATE)
                .phoneNumber(TELEPHONE_NO)
                .type(TYPE)
                .verified(DEFAULT_VERIFIED)
                .realEstate(REAL_ESTATE)
                .author(AUTHOR)
                .deleted(DEFAULT_DELETED);
        updatedAnnouncement = new Announcement()
                .id(null)
                .price(UPDATED_PRICE)
                .dateAnnounced(UPDATED_DATE_ANNOUNCED)
                .dateModified(UPDATED_DATE_MODIFIED)
                .expirationDate(UPDATED_EXPIRATION_DATE)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .type(UPDATED_TYPE)
                .verified(DEFAULT_VERIFIED)
                .realEstate(UPDATED_REAL_ESTATE)
                .author(UPDATED_AUTHOR)
                .deleted(DEFAULT_DELETED);
    }

    /**
     * Creates an Announcement object
     *
     * @return a new Announcement object
     */
    private Announcement createNewEntity() {
        Location LOCATION = new Location().id(null)
                .city(CITY)
                .cityRegion(CITY_REGION)
                .country(COUNTRY)
                .street(STREET)
                .streetNumber(STREET_NUMBER)
                .latitude(90.)
                .longitude(90.);

        RealEstate NEW_REAL_ESTATE = new RealEstate().id(null)
                .type(RE_TYPE)
                .area(RE_AREA)
                .heatingType(RE_HEATING_TYPE)
                .deleted(RE_DELETED)
                .location(LOCATION);
        return new Announcement()
                .id(null)
                .price(NEW_PRICE)
                .name(NEW_NAME)
                .description(NEW_DESCRIPTION)
                .dateAnnounced(NEW_DATE_ANNOUNCED)
                .dateModified(NEW_DATE_MODIFIED)
                .expirationDate(NEW_EXPIRATION_DATE)
                .phoneNumber(NEW_PHONE_NUMBER)
                .type(NEW_TYPE)
                .verified(DEFAULT_VERIFIED)
                .realEstate(NEW_REAL_ESTATE)
                .author(NEW_AUTHOR)
                .deleted(DEFAULT_DELETED);
    }

    /**
     * Tests pageable retrieval of Announcements
     * <p>
     * This test uses a PageRequest object to specify the number
     * of results it wants to receive when it requests Announcements,
     * then asserts that the number of returned results matches
     * the page size in our request.
     */
    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Announcement> announcements = announcementService.findAll(pageRequest);
        assertThat(announcements).hasSize(PAGE_SIZE);
    }

    /**
     * Tests retrieval of all Announcements
     * <p>
     * This test finds all Announcements on the repository and asserts
     * that the number of returned results is equal to the number of
     * Announcements on the database
     */
    @Test
    public void testFindAll() {
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(DB_COUNT_ANNOUNCEMENT);
    }

    /**
     * Tests retrieval of a single Announcement.
     * <p>
     * This test uses the id of an Announcement that is in the repository
     * to search for it, then asserts that the returned value is not null
     * and compares the returned Announcement to an existing Announcement.
     */
    @Test
    public void testFindOne() {
        Announcement ann = announcementService.findOne(ID);
        assertThat(ann).isNotNull();

        compareAnnouncements(ann, existingAnnouncement);
    }

    /**
     * Tests addition of Announcements
     * <p>
     * This test saves a new Announcement using the AnnouncementService,
     * then it finds all Announcements and asserts that the size of the results
     * has increased by one. It also asserts that the new Announcement that is on
     * the database equals the Announcement we added.
     */
    @Test
    @Transactional
    public void testAdd() {
        int dbSizeBeforeAdd = announcementRepository.findAll().size();

        Announcement dbAnnouncement = announcementService.save(newAnnouncement);
        assertThat(dbAnnouncement).isNotNull();

        // Validate that new announcement is in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(dbSizeBeforeAdd + 1);

        compareAnnouncements(dbAnnouncement, newAnnouncement);
    }

    /**
     * Tests updating of Announcements.
     * <p>
     * This test retrieves an Announcement using the service, then changes
     * its attributes and saves it to the database. Then it asserts that
     * the object on the database is not null and equals our updated Announcement.
     */
    @Test
    @Transactional
    public void testUpdate() {
        Announcement dbAnnouncement = announcementService.findOne(ID);

        dbAnnouncement.setPrice(UPDATED_PRICE);
        dbAnnouncement.setName(UPDATED_NAME);
        dbAnnouncement.setDescription(UPDATED_DESCRIPTION);
        dbAnnouncement.setDateAnnounced(UPDATED_DATE_ANNOUNCED);
        dbAnnouncement.setDateModified(UPDATED_DATE_MODIFIED);
        dbAnnouncement.setExpirationDate(UPDATED_EXPIRATION_DATE);
        dbAnnouncement.setAuthor(UPDATED_AUTHOR);
        dbAnnouncement.setType(UPDATED_TYPE);
        dbAnnouncement.setPhoneNumber(UPDATED_PHONE_NUMBER);
        dbAnnouncement.setRealEstate(UPDATED_REAL_ESTATE);

        Announcement updatedDbAnnouncement = announcementService.save(dbAnnouncement);
        assertThat(updatedDbAnnouncement).isNotNull();

        compareAnnouncements(updatedDbAnnouncement, updatedAnnouncement);
    }

    /**
     * Tests removal of Announcements
     * <p>
     * This test deletes an Announcement using the service, then
     * asserts that the number of Announcements on the database
     * has been reduced by one. It also asserts that an object
     * with the deleted Announcement's id does not exists on the
     * database.
     */
    @Test
    @Transactional
    public void testRemove() {
        int dbSizeBeforeRemove = announcementRepository.findAll().size();
        announcementService.delete(ID);

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(dbSizeBeforeRemove - 1);

        Announcement dbAnnouncement = announcementService.findOne(ID);
        assertThat(dbAnnouncement).isNull();
    }

    /**
     * Tests searching Announcements by their company's id
     * <p>
     * This test searches for all Announcements by a Company.
     * It then asserts that the company of every Announcement
     * in the results matches the one we searched by
     */
    @Test
    public void testAnnouncementsByAuthorId() {
        Date today = new Date();
        Page<Announcement> dbAnnouncements = announcementService.findAllByCompany(COMPANY_ID, PAGEABLE);
        List<Announcement> content = dbAnnouncements.getContent();

        for (Announcement ann : content) {
            assertThat(ann.getAuthor().getCompany().getId()).isEqualTo(COMPANY_ID);
            assertThat(ann.getDateAnnounced().after(today));
        }
    }

    /**
     * Tests searching for the top Announcements by a company
     * <p>
     * This test searches for the top Announcements by a company.
     * It asserts that the number of results matches the expected
     * number of top Announcements and that the results are sorted
     * by descending price.
     */
    @Test
    public void testTopThreeAnnouncements() {
        List<Announcement> dbAnnouncements = announcementService.findTopByCompany(COMPANY_ID);

        assertThat(dbAnnouncements.size()).isLessThanOrEqualTo(TOP);

        // test sorting ascending by price
        for (int i = 0; i < dbAnnouncements.size() - 1; i++) {
            assertThat(dbAnnouncements.get(i).getPrice()).isLessThanOrEqualTo(dbAnnouncements.get(i + 1).getPrice());
        }
    }

    /*
     * Negative tests
     */

    /**
     * Tests adding an Announcement with a null price value
     * <p>
     * This test sets an Announcement's price to null, then
     * attempts to add it to the database. As price is a
     * non-nullable field, the test receives a
     * Constraint Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullPrice() {
        newAnnouncement.price(null);
        announcementService.save(newAnnouncement);
    }

    /**
     * Tests adding an Announcement with a null date announced value
     * <p>
     * This test sets an Announcement's date announced to null, then
     * attempts to add it to the database. As date announced is a
     * non-nullable field, the test receives Constraint Violation
     * exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullDateAnnounced() {
        newAnnouncement.dateAnnounced(null);
        announcementService.save(newAnnouncement);
    }

    /**
     * Tests adding an Announcement with a null expiration date value
     * <p>
     * This test sets an Announcement's expiration date to null, then
     * attempts to add it to the database. As expiration date is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullExpirationDate() {
        newAnnouncement.setExpirationDate(null);
        announcementService.save(newAnnouncement);
    }

    /**
     * Tests adding an Announcement with a null phone number value
     * <p>
     * This test sets an Announcement's phone number to null, then
     * attempts to add it to the database. As phone number is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullTelephoneNo() {
        newAnnouncement.setPhoneNumber(null);
        announcementService.save(newAnnouncement);
    }

    /**
     * Tests adding an Announcement with a null type value
     * <p>
     * This test sets an Announcement's type to null, then
     * attempts to add it to the database. As type is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullType() {
        newAnnouncement.setType(null);
        announcementService.save(newAnnouncement);
    }


    /**
     * Tests adding an Announcement with a null name value
     * <p>
     * This test sets an Announcement's name to null, then
     * attempts to add it to the database. As name is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullName() {
        newAnnouncement.setName(null);
        announcementService.save(newAnnouncement);
    }


    /**
     * Tests adding an Announcement with a null description value
     * <p>
     * This test sets an Announcement's description to null, then
     * attempts to add it to the database. As description is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullDescription() {
        newAnnouncement.setDescription(null);
        announcementService.save(newAnnouncement);
    }

    /**
     * Tests finding deleted Announcements
     * <p>
     * This test finds all Announcements that have been deleted, then
     * asserts that the number of returned Announcements equals
     * the expected number of deleted Announcements and that
     * every one of the results has been deleted.
     */
    @Test
    public void testFindAllByStatusDeletedTrue() {
        final Boolean status = true;

        final Page<Announcement> announcements = announcementRepository.findAllByDeleted(status, PAGEABLE);

        assertThat(announcements.getTotalElements()).isEqualTo(DB_COUNT_ANNOUNCEMENT_DELETED_TRUE);

        for (final Announcement announcement : announcements) {
            assertThat(announcement.isDeleted()).isEqualTo(status);
        }
    }

    /**
     * Tests finding undeleted Announcements
     * <p>
     * This test finds all Announcements that have not been deleted,
     * then asserts that the number of returned Announcements equals
     * the expected number of undeleted Announcements and that
     * every one of the results has not been deleted.
     */
    @Test
    public void testFindAllByStatusDeletedFalse() {
        final Boolean status = false;

        final Page<Announcement> announcements = announcementRepository.findAllByDeleted(status, PAGEABLE);

        assertThat(announcements.getTotalElements()).isEqualTo(DB_COUNT_ANNOUNCEMENT_DELETED_FALSE);

        for (final Announcement announcement : announcements) {
            assertThat(announcement.isDeleted()).isEqualTo(status);
        }
    }


    /**
     * Tests search using no arguments
     * <p>
     * This test creates an empty Announcement Search Wrapper and uses
     * it to search the database for Announcements. It then asserts that
     * the number of returned results matches the number of undeleted
     * Announcements on the database or the number of results on a page,
     * whichever is smaller.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchAnnouncementsWithoutAnyAttribute() throws Exception {
        final int dbSize = announcementService.findAllByStatus(false, null).getContent().size();
        final int requiredSize = dbSize < PAGEABLE.getPageSize() ? dbSize : PAGEABLE.getPageSize();

        Page<Announcement> result = announcementService.findBySearchTerm(new AnnouncementSearchWrapper(), PAGEABLE);
        assertThat(result).hasSize(requiredSize);
    }

    /**
     * Tests search using an Area argument
     * <p>
     * This test saves an Announcement to the database,
     * then uses its Area value to search the database.
     * Then it asserts that the Area of all results is equal
     * to the area specified.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchAnnouncementsByAreaLimitInclude() throws Exception {
        announcementRepository.saveAndFlush(newAnnouncement);

        final double area = newAnnouncement.getRealEstate().getArea();

        AnnouncementSearchWrapper wrapper = new AnnouncementSearchWrapper()
                .startArea(area)
                .endArea(area);

        Page<Announcement> result = announcementService.findBySearchTerm(wrapper, PAGEABLE);

        for (Announcement ann : result) {
            assertThat(ann.getRealEstate().getArea()).isBetween(area, area);
        }
    }

    /**
     * Tests searching for a deleted Announcement
     * <p>
     * This tests sets an Announcement's deleted value to true,
     * then saves it to the database. Then it searches the database
     * with no arguments and asserts that none of the returned results
     * match the deleted Announcement's id.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchDeletedAnnouncements() throws Exception {
        Announcement persisted = announcementRepository.saveAndFlush(
                newAnnouncement.deleted(true));

        Page<Announcement> result = announcementService.findBySearchTerm(new AnnouncementSearchWrapper(), AnnouncementConstants.PAGEABLE);

        for (Announcement ann : result) {
            assertThat(ann.getId()).isNotEqualTo(persisted.getId());
        }
    }

    /**
     * Tests searching for all announcements in provided area
     * <p>
     * This test search for all announcements which longitude and latitude is
     * in provided square and then asserts if size of resulting list is same as
     * number of announcements in database which satisfies this condition.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    public void searchAnnouncementsInArea() throws Exception {

        final Double topRightLong = 20.0;
        final Double topRightLat = 46.0;
        final Double bottomLeftLong = 18.0;
        final Double bottomLeftLat = 45.0;

        final List<Announcement> announcements = announcementService.findAllInArea(topRightLong, topRightLat, bottomLeftLong, bottomLeftLat, PAGEABLE).getContent();

        assertThat(announcements.size()).isEqualTo(ANNOUNCEMENTS_IN_AREA);

        for (final Announcement announcement : announcements) {
            assertThat(announcement.getRealEstate().getLocation().isInArea(topRightLong, topRightLat, bottomLeftLong, bottomLeftLat));
        }
    }

}
