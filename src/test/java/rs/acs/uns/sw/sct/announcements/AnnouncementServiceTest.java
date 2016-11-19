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
import rs.acs.uns.sw.sct.AwtTestSiitProject2016Application;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.AnnouncementConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AwtTestSiitProject2016Application.class)
public class AnnouncementServiceTest {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementRepository announcementRepository;

    private Announcement newAnnouncement;
    private Announcement updatedAnnouncement;
    private Announcement existingAnnouncement;

    private void compareAnnoncements(Announcement ann1, Announcement ann2){
        if (ann1.getId() != null && ann2.getId() != null)
            assertThat(ann1.getId()).isEqualTo(ann2.getId());
        assertThat(ann1.getPrice()).isEqualTo(ann2.getPrice());
        if (ann1.getDateAnnounced() != null && ann2.getDateAnnounced() != null)
            assertThat(ann1.getDateAnnounced()).isEqualTo(ann2.getDateAnnounced());
        if (ann1.getDateModified() != null && ann2.getDateModified() != null)
            assertThat(ann1.getDateModified()).isEqualTo(ann2.getDateModified());
        if (ann1.getExpirationDate() != null && ann2.getExpirationDate() != null)
            assertThat(ann1.getExpirationDate()).isEqualTo(ann2.getExpirationDate());
        assertThat(ann1.getTelephoneNo()).isEqualTo(ann2.getTelephoneNo());
        assertThat(ann1.getVerified()).isEqualTo(ann2.getVerified());
        if (ann1.getRealEstate() != null && ann2.getRealEstate() != null)
            assertThat(ann1.getRealEstate().getId()).isEqualTo(ann2.getRealEstate().getId());
        assertThat(ann1.getAuthor().getId()).isEqualTo(ann2.getAuthor().getId());
    }

    @Before
    public void initTest() {
        existingAnnouncement = new Announcement(ID, PRICE, DATE_ANNOUNCED, DATE_MODIFIED, EXPIRATION_DATE, TELEPHONE_NO, TYPE, DEFAULT_VERIFIED, REAL_ESTATE, AUTHOR, DEFAULT_MARKS, DEFAULT_COMMENTS, DEFAULT_DELETED);
        newAnnouncement = new Announcement(null, NEW_PRICE, NEW_DATE_ANNOUNCED, NEW_DATE_MODIFIED, NEW_EXPIRATION_DATE, NEW_TELEPHONE_NO, NEW_TYPE, DEFAULT_VERIFIED, NEW_REAL_ESTATE, NEW_AUTHOR, DEFAULT_MARKS, DEFAULT_COMMENTS, DEFAULT_DELETED);
        updatedAnnouncement = new Announcement(null, UPDATED_PRICE, UPDATED_DATE_ANNOUNCED, UPDATED_DATE_MODIFIED, UPDATED_EXPIRATION_DATE, UPDATED_TELEPHONE_NO, UPDATED_TYPE, DEFAULT_VERIFIED, UPDATED_REAL_ESTATE, UPDATED_AUTHOR, DEFAULT_MARKS, DEFAULT_COMMENTS, DEFAULT_DELETED);
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
    public void testFindOne(){
        Announcement ann = announcementService.findOne(ID);
        assertThat(ann).isNotNull();

        compareAnnoncements(ann, existingAnnouncement);
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

        compareAnnoncements(dbAnnouncement, newAnnouncement);
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
        dbAnnouncement.setTelephoneNo(UPDATED_TELEPHONE_NO);
        dbAnnouncement.setRealEstate(UPDATED_REAL_ESTATE);

        Announcement updatedDbAnnouncement = announcementService.save(dbAnnouncement);
        assertThat(updatedDbAnnouncement).isNotNull();

        compareAnnoncements(updatedDbAnnouncement, updatedAnnouncement);
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


    /*
	 * Negative tests
	 */

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullPrice() {
        newAnnouncement.setPrice(null);
        announcementService.save(newAnnouncement);
        // rollback previous price
        newAnnouncement.setPrice(NEW_PRICE);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullDateAnnounced() {
        newAnnouncement.setDateAnnounced(null);
        announcementService.save(newAnnouncement);
        // rollback previous date announced
        newAnnouncement.setDateAnnounced(NEW_DATE_ANNOUNCED);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullExpirationDate() {
        newAnnouncement.setExpirationDate(null);
        announcementService.save(newAnnouncement);
        // rollback previous expiration date
        newAnnouncement.setExpirationDate(NEW_EXPIRATION_DATE);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullDateTelephoneNo() {
        newAnnouncement.setTelephoneNo(null);
        announcementService.save(newAnnouncement);
        // rollback previous telephone no
        newAnnouncement.setTelephoneNo(NEW_TELEPHONE_NO);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullType() {
        newAnnouncement.setType(null);
        announcementService.save(newAnnouncement);
        // rollback previous type
        newAnnouncement.setType(NEW_TYPE);
    }
}
