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
        return new Mark()
                .value(DEFAULT_VALUE);
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

    @Before
    public void initTest() {
        mark = createEntity();
    }

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

    @Test
    @Transactional
    public void getAllMarksAsGuest() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get all the marks
        restMarkMockMvc.perform(get("/api/marks?sort=id,desc"))
                .andExpect(status().isUnauthorized());
    }

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

    @Test
    @Transactional
    public void getMarkAsGuest() throws Exception {
        // Initialize the database
        markRepository.saveAndFlush(mark);

        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/{id}", mark.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getNonExistingMark() throws Exception {
        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

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

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllMarksForAnnouncementAsAdvertiser() throws Exception {

        final Long announcementId = 1L;

        // Initialize the database
        mark.setAnnouncement(new Announcement().id(announcementId));
        markRepository.saveAndFlush(mark);

        final Long count = markRepository.findByAnnouncement_Id(announcementId, MarkConstants.PAGEABLE).getTotalElements();

        // Get the mark
        restMarkMockMvc.perform(get("/api/marks/announcement/{announcementId}",announcementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(mark.getId().intValue()))
                .andExpect(jsonPath("$.[*].value").value(DEFAULT_VALUE));
    }
}
