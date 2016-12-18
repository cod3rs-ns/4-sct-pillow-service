package rs.acs.uns.sw.sct.announcements;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.constants.AnnouncementConstants;
import rs.acs.uns.sw.sct.realestates.Location;
import rs.acs.uns.sw.sct.realestates.RealEstate;
import rs.acs.uns.sw.sct.search.AnnouncementSearchWrapper;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.AnnouncementConstants.*;
import static rs.acs.uns.sw.sct.constants.CompanyConstants.PAGEABLE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class AnnouncementServiceTest {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    private Announcement newAnnouncement;
    private Announcement updatedAnnouncement;
    private Announcement existingAnnouncement;

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

    private Announcement createNewEntity() {
        Location LOCATION = new Location().id(null)
                .city(CITY)
                .cityRegion(CITY_REGION)
                .country(COUNTRY)
                .street(STREET)
                .streetNumber(STREET_NUMBER);
        RealEstate NEW_REAL_ESTATE = new RealEstate().id(null)
                .equipment(RE_EQUIPMENT)
                .name(RE_NAME)
                .type(RE_TYPE)
                .area(RE_AREA)
                .heatingType(RE_HEATING_TYPE)
                .deleted(RE_DELETED)
                .location(LOCATION);
        return new Announcement()
                .id(null)
                .price(NEW_PRICE)
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

    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Announcement> announcements = announcementService.findAll(pageRequest);
        assertThat(announcements).hasSize(PAGE_SIZE);
    }

    @Test
    public void testFindAll() {
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(DB_COUNT_ANNOUNCEMENT);
    }

    @Test
    public void testFindOne() {
        Announcement ann = announcementService.findOne(ID);
        assertThat(ann).isNotNull();

        compareAnnouncements(ann, existingAnnouncement);
    }

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


    @Test
    @Transactional
    public void testUpdate() {
        Announcement dbAnnouncement = announcementService.findOne(ID);

        dbAnnouncement.setPrice(UPDATED_PRICE);
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

    @Test
    public void testAnnouncementsByAuthorId() {
        Page<Announcement> dbAnnouncements = announcementService.findAllByCompany(COMPANY_ID, PAGEABLE);
        List<Announcement> content = dbAnnouncements.getContent();

        for (Announcement ann : content) {
            assertThat(ann.getAuthor().getCompany().getId()).isEqualTo(COMPANY_ID);
        }

        assertThat(content.size()).isEqualTo(COUNT_OF_COMPANY_ANN);
    }

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
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullPrice() {
        newAnnouncement.price(null);
        announcementService.save(newAnnouncement);
    }

    @Test()
    @Transactional
    public void testAddNullDateAnnounced() {
        announcementService.save(newAnnouncement);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullExpirationDate() {
        newAnnouncement.setExpirationDate(null);
        announcementService.save(newAnnouncement);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullTelephoneNo() {
        newAnnouncement.setPhoneNumber(null);
        announcementService.save(newAnnouncement);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullType() {
        newAnnouncement.setType(null);
        announcementService.save(newAnnouncement);
    }

    @Test
    public void testFindAllByStatusDeletedTrue() {
        final Boolean status = true;

        final Page<Announcement> announcements = announcementRepository.findAllByDeleted(status, PAGEABLE);

        assertThat(announcements.getTotalElements()).isEqualTo(DB_COUNT_ANNOUNCEMENT_DELETED_TRUE);

        for (final Announcement announcement : announcements) {
            assertThat(announcement.isDeleted()).isEqualTo(status);
        }
    }

    @Test
    public void testFindAllByStatusDeletedFalse() {
        final Boolean status = false;

        final Page<Announcement> announcements = announcementRepository.findAllByDeleted(status, PAGEABLE);

        assertThat(announcements.getTotalElements()).isEqualTo(DB_COUNT_ANNOUNCEMENT_DELETED_FALSE);

        for (final Announcement announcement : announcements) {
            assertThat(announcement.isDeleted()).isEqualTo(status);
        }
    }


    @Test
    @Transactional
    public void searchAnnouncementsWithoutAnyAttribute() throws Exception {
        final int dbSize = announcementService.findAllByStatus(false, null).getContent().size();
        final int requiredSize = dbSize < PAGEABLE.getPageSize() ? dbSize : PAGEABLE.getPageSize();

        List<Announcement> result = announcementService.findBySearchTerm(new AnnouncementSearchWrapper(), PAGEABLE);
        assertThat(result).hasSize(requiredSize);
    }

    @Test
    @Transactional
    public void searchAnnouncementsByAreaLimitInclude() throws Exception {
        announcementRepository.saveAndFlush(newAnnouncement);

        final double area = newAnnouncement.getRealEstate().getArea();

        AnnouncementSearchWrapper wrapper = new AnnouncementSearchWrapper()
                .startArea(area)
                .endArea(area);

        List<Announcement> result = announcementService.findBySearchTerm(wrapper, PAGEABLE);

        for (Announcement ann : result) {
            assertThat(ann.getRealEstate().getArea()).isBetween(area, area);
        }
    }

    @Test
    @Transactional
    public void searchDeletedAnnouncements() throws Exception {
        Announcement persisted = announcementRepository.saveAndFlush(
                newAnnouncement.deleted(true));

        List<Announcement> result = announcementService.findBySearchTerm(new AnnouncementSearchWrapper(), AnnouncementConstants.PAGEABLE);

        for (Announcement ann : result) {
            assertThat(ann.getId()).isNotEqualTo(persisted.getId());
        }
    }
}
