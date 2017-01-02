package rs.acs.uns.sw.sct.marks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.announcements.AnnouncementService;
import rs.acs.uns.sw.sct.constants.MarkConstants;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.util.AuthorityRoles;
import rs.acs.uns.sw.sct.util.DBUserMocker;
import rs.acs.uns.sw.sct.util.TestUtil;

import javax.annotation.PostConstruct;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MarkResource REST controller.
 *
 * @see MarkController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class MarkControllerTest {

    private static final Integer DEFAULT_VALUE = 1;
    private static final Integer UPDATED_VALUE = 2;
    private static final Long DEFAULT_USER = 1L;
    private static final Long GRADED_USER = 2L;
    private static final Long DEFAULT_ANNOUNCEMENT = 1L;

    @Autowired
    private MarkRepository markRepository;

    @Autowired
    private MarkService markService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AnnouncementService announcementService;

    private MockMvc restMarkMockMvc;

    private Mark mark;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mark createEntity() {
        Announcement announcement = new Announcement()
                .id(DEFAULT_ANNOUNCEMENT)
                .author(new User().id(DEFAULT_USER));
        announcement.getImages();

        return new Mark()
                .value(DEFAULT_VALUE)
                .announcement(announcement)
                .grader(new User().id(DEFAULT_USER))
                .gradedAnnouncer(new User().id(GRADED_USER));
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        MarkController markCtrl = new MarkController();
        ReflectionTestUtils.setField(markCtrl, "markService", markService);
        this.restMarkMockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    /**
     * Initializes all objects needed for further testing.
     * <p>
     * This method is called before testing starts.
     */
    @Before
    public void initTest() {
        mark = createEntity();
    }

    /**
     * Tests Mark creation as an Advertiser.
     * <p>
     * This test uses a mock Advertiser user to create a new Mark object
     * on the database. It then asserts that the number of objects on the
     * database has increased by one and that the last Mark added matches
     * the mark we created.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "test_advertiser_company_member")
    public void createMarkAsAdvertiser() throws Exception {
        int databaseSizeBeforeCreate = markRepository.findAll().size();

        // Create the Mark
        mark.setAnnouncement(announcementService.findOne(1L));
        restMarkMockMvc.perform(post("/api/marks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(mark)))
                .andExpect(status().isCreated());

        // Validate the Mark in the database
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeCreate + 1);
        Mark testMark = marks.get(marks.size() - 1);
        assertThat(testMark.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    /**
     * Tests Mark creation as a Guest.
     * <p>
     * This test uses no authorization to create a new Mark object
     * on the database, which is forbidden. It then asserts that the number
     * of objects on the database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void createMarkAsGuest() throws Exception {
        final int databaseSizeBeforeCreate = markRepository.findAll().size();

        // Create the Mark

        restMarkMockMvc.perform(post("/api/marks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(mark)))
                .andExpect(status().isUnauthorized());

        // Validate the Mark in the database
        final List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeCreate);
    }

    /**
     * Tests whether the "Value" field is nullable
     * <p>
     * This test attempts to add a Mark object with a null "Value" value to the database,
     * this is forbidden as the "Value" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in the database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = markRepository.findAll().size();
        // set the field null
        mark.setValue(null);

        // Create the Mark, which fails.

        restMarkMockMvc.perform(post("/api/marks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(mark)))
                .andExpect(status().isBadRequest());

        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests getting all Marks as an Admin
     * <p>
     * This test uses a mocked Admin user to request all Marks
     * from the database. It then asserts that the received results
     * match what was expected.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getAllMarksAsAdmin() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get all the marks
        restMarkMockMvc.perform(get("/api/marks?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(mark.getId().intValue())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    /**
     * Tests getting all Marks as an Admin
     * <p>
     * This test uses a mocked Admin user to request all Marks
     * from the database, which fails and returns a Forbidden status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllMarksAsAdvertiser() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get all the marks
        restMarkMockMvc.perform(get("/api/marks?sort=id,desc"))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests getting all Marks as a Guest
     * <p>
     * This test uses a mocked Guest user to request all Marks
     * from the database, which fails and returns an Unauthorized status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllMarksAsGuest() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get all the marks
        restMarkMockMvc.perform(get("/api/marks?sort=id,desc"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests getting a single Mark by id as an Advertiser
     * <p>
     * This test uses a mocked Advertiser user to retrieve a
     * Mark object from the database using its ID.
     * It then checks whether the object's attributes have valid values.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getMarkAsAdvertiser() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/{id}", mark.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(mark.getId().intValue()))
                .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    /**
     * Tests getting a single Mark by id as a Guest
     * <p>
     * This test uses a mocked Guest user to attempt to retrieve an
     * Mark object from the database using its ID.
     * This fails and returns an Unauthorized status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getMarkAsGuest() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/{id}", mark.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests invalid retrieval attempts
     * <p>
     * This tests attempts to retrieve a Mark object which is not in the database
     * by searching for a non-existent id. This returns an "Is not found" status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getNonExistingMark() throws Exception {
        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests Mark updating.
     * <p>
     * This test saves a Mark object to the database,
     * then updates the values of its attributes and uses PUT to save the object.
     * Then it uses a mocked Advertiser user to
     * compare the original number of objects in the database to the new one
     * and the updated values of our modified Mark with the ones found in
     * the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = DBUserMocker.ADVERTISER_USERNAME)
    public void updateMark() throws Exception {
        mark.grader(DBUserMocker.ADVERTISER);
        // Initialize the database
        markService.save(mark);

        int databaseSizeBeforeUpdate = markRepository.findAll().size();

        // Update the mark
        Mark updatedMark = markRepository.findOne(mark.getId());
        updatedMark
                .value(UPDATED_VALUE);

        restMarkMockMvc.perform(put("/api/marks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedMark)))
                .andExpect(status().isOk());

        // Validate the Mark in the database
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeUpdate);
        Mark testMark = marks.get(marks.size() - 1);
        assertThat(testMark.getValue()).isEqualTo(UPDATED_VALUE);
    }

    /**
     * Tests Mark deletion as the Mark's author
     * <p>
     * This tests sets a Mark's author to a mocked Verifier
     * user that it then uses to delete it
     * on the database. It then asserts
     * that the number of objects on the database
     * after this action has been reduced by one.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER, username = DBUserMocker.VERIFIER_USERNAME)
    public void deleteMarkAsVerifier() throws Exception {
        mark.grader(DBUserMocker.VERIFIER);
        // Initialize the database
        markService.save(mark);

        int databaseSizeBeforeDelete = markRepository.findAll().size();

        // Get the mark
        restMarkMockMvc.perform(delete("/api/marks/{id}", mark.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeDelete - 1);
    }

    /**
     * Tests Mark deletion as a Guest
     * <p>
     * This tests sets a Mark's author to a mocked Guest
     * user and then attempts to delete it on the database
     * with no authorization, which is not allowed.
     * It then asserts that the number of
     * Marks on the database has not
     * changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteMarkAsGuest() throws Exception {
        // Initialize the database
        markService.save(mark);

        int databaseSizeBeforeDelete = markRepository.findAll().size();

        // Delete the mark
        restMarkMockMvc.perform(delete("/api/marks/{id}", mark.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());

        List<Mark> marks = markRepository.findAll();
        assertThat(marks).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests getting all Marks for an Announcement
     * <p>
     * This test sets an Announcement as a Mark's announcement,
     * then saves it to the database. It then then finds all Marks
     * tied to that Announcement on that database and asserts that
     * the number of Marks matches the expected number.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllMarksForAnnouncementAsAdvertiser() throws Exception {

        final Long announcementId = 1L;
        final Long userId = 1L;

        // Initialize the database
        mark.setAnnouncement(new Announcement().id(announcementId).author(new User().id(userId)));
        markRepository.saveAndFlush(mark);

        final Long count = markRepository.findByAnnouncement_Id(announcementId, MarkConstants.PAGEABLE).getTotalElements();

        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/announcement/{announcementId}",announcementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(mark.getId().intValue())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
