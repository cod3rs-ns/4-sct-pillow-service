package rs.acs.uns.sw.sct.announcements;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.constants.RealEstateConstants;
import rs.acs.uns.sw.sct.constants.UserConstants;
import rs.acs.uns.sw.sct.realestates.Location;
import rs.acs.uns.sw.sct.realestates.RealEstate;
import rs.acs.uns.sw.sct.realestates.RealEstateService;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserControllerTest;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.*;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static rs.acs.uns.sw.sct.constants.AnnouncementConstants.FILE_TO_BE_UPLOAD;
import static rs.acs.uns.sw.sct.constants.AnnouncementConstants.NEW_BASE_DIR;
import static rs.acs.uns.sw.sct.util.ContainsIgnoreCase.containsIgnoringCase;
import static rs.acs.uns.sw.sct.util.TestUtil.getRandomCaseInsensitiveSubstring;

/**
 * Test class for the AnnouncementResource REST controller.
 *
 * @see AnnouncementController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class AnnouncementControllerTest {

    private static final Double DEFAULT_PRICE = 150D;
    private static final Double UPDATED_PRICE = 1D;

    private static final Date DEFAULT_DATE_ANNOUNCED = DateUtil.asDate(LocalDate.ofEpochDay(0L));
    private static final Date UPDATED_DATE_ANNOUNCED = DateUtil.asDate(LocalDate.ofEpochDay(1L));

    private static final Date DEFAULT_DATE_MODIFIED = DateUtil.asDate(LocalDate.ofEpochDay(0L));
    private static final Date UPDATED_DATE_MODIFIED = DateUtil.asDate(LocalDate.ofEpochDay(1L));

    private static final Date DEFAULT_EXPIRATION_DATE = DateUtil.asDate(LocalDate.ofEpochDay(0L));
    private static final Date UPDATED_EXPIRATION_DATE = DateUtil.asDate(LocalDate.ofEpochDay(1L));

    private static final String DEFAULT_PHONE_NUMBER = "0600000000";
    private static final String UPDATED_PHONE_NUMBER = "0611111111";

    private static final String DEFAULT_TYPE = "TYPE_AAA";
    private static final String UPDATED_TYPE = "TYPE_BBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    private static final String DEFAULT_VERIFIED = "not-verified";
    private static final String UPDATED_VERIFIED = "verified";

    private static final String EXPIRATION_DATE_JSON_AFTER = "{\"expirationDate\": \"4/12/2017\"}";
    private static final String EXPIRATION_DATE_JSON_BEFORE = "{\"expirationDate\": \"4/12/2015\"}";
    private static final String EXPIRATION_DATE_JSON_INVALID_NAME = "{\"expiration\": \"4/12/2017\"}";
    private static final String EXPIRATION_DATE_JSON_INVALID_FORMAT = "{\"expirationDate\": \"4-12-2017\"}";
    private static final String EXTENDED_DATE_AS_STRING = "2017-12-4";

    private static final int PAGE_SIZE = 5;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    @Autowired
    private RealEstateService realEstateService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private EntityManager em;

    @Autowired
    private ConfigurableEnvironment env;

    private MockMvc restAnnouncementMockMvc;

    private Announcement announcement;

    private AnnouncementDTO announcementDTO;

    private MockMultipartFile fileToBeUpload;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Announcement createEntity() {
        return new Announcement()
                .price(DEFAULT_PRICE)
                .dateAnnounced(DEFAULT_DATE_ANNOUNCED)
                .dateModified(DEFAULT_DATE_MODIFIED)
                .expirationDate(DEFAULT_EXPIRATION_DATE)
                .phoneNumber(DEFAULT_PHONE_NUMBER)
                .type(DEFAULT_TYPE)
                .verified(DEFAULT_VERIFIED)
                .deleted(DEFAULT_DELETED);
    }


    /**
     * Create an announcement DTO for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AnnouncementDTO createDTO() {
        return createEntity().convertToDTO();
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AnnouncementController announcementCtrl = new AnnouncementController();
        ReflectionTestUtils.setField(announcementCtrl, "announcementService", announcementService);
        ReflectionTestUtils.setField(announcementCtrl, "userService", userService);

        ReflectionTestUtils.setField(AnnouncementController.class, "uploadPath", NEW_BASE_DIR);

        this.restAnnouncementMockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    public MockMultipartFile createFile() {
        File f = new File(FILE_TO_BE_UPLOAD);
        MockMultipartFile file = null;

        try {
            FileInputStream fi = new FileInputStream(f);
            file = new MockMultipartFile("file", f.getName(), "multipart/form-data", fi);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }


    @Before
    public void initTest() {
        announcement = createEntity();
        announcementDTO = createDTO();
        fileToBeUpload = createFile();
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = UserConstants.USER_USERNAME)
    public void createAnnouncement() throws Exception {
        int databaseSizeBeforeCreate = announcementRepository.findAll().size();

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcementDTO)))
                .andExpect(status().isCreated());

        // Validate the Announcement in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeCreate + 1);
        Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(DEFAULT_EXPIRATION_DATE);
        assertThat(testAnnouncement.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testAnnouncement.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testAnnouncement.getVerified()).isEqualTo(DEFAULT_VERIFIED);
        assertThat(testAnnouncement.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void createAnnouncementWithVerifierAuthority() throws Exception {
        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcementDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = announcementRepository.findAll().size();
        // set the field null
        announcement.setPrice(null);

        // Create the Announcement, which fails.

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcement)))
                .andExpect(status().isBadRequest());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkExpirationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = announcementRepository.findAll().size();
        // set the field null
        announcement.setExpirationDate(null);

        // Create the Announcement, which fails.

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcement.convertToDTO())))
                .andExpect(status().isBadRequest());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTelephoneNoIsRequired() throws Exception {
        int databaseSizeBeforeTest = announcementRepository.findAll().size();
        // set the field null
        announcement.setPhoneNumber(null);

        // Create the Announcement, which fails.

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcement)))
                .andExpect(status().isBadRequest());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = announcementRepository.findAll().size();
        // set the field null
        announcement.setType(null);

        // Create the Announcement, which fails.

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcement)))
                .andExpect(status().isBadRequest());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getAllAnnouncementsAsAdmin() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcements
        restAnnouncementMockMvc.perform(get("/api/announcements?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].dateAnnounced").value(hasItem((int) DEFAULT_DATE_ANNOUNCED.getTime())))
                .andExpect(jsonPath("$.[*].dateModified").value(hasItem((int) DEFAULT_DATE_MODIFIED.getTime())))
                .andExpect(jsonPath("$.[*].expirationDate").value(hasItem((int) DEFAULT_EXPIRATION_DATE.getTime())))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllAnnouncementsAsAdvertiser() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcements
        restAnnouncementMockMvc.perform(get("/api/announcements?sort=id,desc"))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void getAnnouncement() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get the announcement
        restAnnouncementMockMvc.perform(get("/api/announcements/{id}", announcement.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(announcement.getId().intValue()))
                .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
                .andExpect(jsonPath("$.dateAnnounced").value(String.valueOf(DEFAULT_DATE_ANNOUNCED.getTime())))
                .andExpect(jsonPath("$.dateModified").value(String.valueOf(DEFAULT_DATE_MODIFIED.getTime())))
                .andExpect(jsonPath("$.expirationDate").value(String.valueOf(DEFAULT_EXPIRATION_DATE.getTime())))
                .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
                .andExpect(jsonPath("$.verified").value(DEFAULT_VERIFIED))
                .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED));
    }

    @Test
    @Transactional
    public void getNonExistingAnnouncement() throws Exception {
        // Get the announcement
        restAnnouncementMockMvc.perform(get("/api/announcements/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = DBUserMocker.ADVERTISER_USERNAME)
    public void updateAnnouncement() throws Exception {
        announcement.setAuthor(DBUserMocker.ADVERTISER);
        announcementService.save(announcement);

        int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        // Update the announcement
        Announcement updatedAnnouncement = announcementRepository.findOne(announcement.getId());
        updatedAnnouncement
                .price(UPDATED_PRICE)
                .dateAnnounced(UPDATED_DATE_ANNOUNCED)
                .dateModified(UPDATED_DATE_MODIFIED)
                .expirationDate(UPDATED_EXPIRATION_DATE)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .type(UPDATED_TYPE);

        restAnnouncementMockMvc.perform(put("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAnnouncement)))
                .andExpect(status().isOk());

        // Validate the Announcement in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);
        Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testAnnouncement.getDateAnnounced()).isEqualTo(UPDATED_DATE_ANNOUNCED);
        assertThat(testAnnouncement.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
        assertThat(testAnnouncement.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testAnnouncement.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN, username = DBUserMocker.ADMIN_USERNAME)
    public void updateAnnouncementAsAdmin() throws Exception {
        announcement.setAuthor(DBUserMocker.ADVERTISER);
        announcementService.save(announcement);

        // Update the announcement
        Announcement updatedAnnouncement = announcementRepository.findOne(announcement.getId());
        updatedAnnouncement
                .price(UPDATED_PRICE)
                .dateAnnounced(UPDATED_DATE_ANNOUNCED)
                .dateModified(UPDATED_DATE_MODIFIED)
                .expirationDate(UPDATED_EXPIRATION_DATE)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .type(UPDATED_TYPE);

        int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        restAnnouncementMockMvc.perform(put("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAnnouncement)))
                .andExpect(status().isOk());

        // Validate the Announcement in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);
        Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testAnnouncement.getDateAnnounced()).isEqualTo(UPDATED_DATE_ANNOUNCED);
        assertThat(testAnnouncement.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(UPDATED_EXPIRATION_DATE);
        assertThat(testAnnouncement.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testAnnouncement.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "not_owner_username")
    public void updateAnnouncementNotOwner() throws Exception {
        announcement.setAuthor(DBUserMocker.ADVERTISER);
        // Initialize the database
        announcementService.save(announcement);

        int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        // Update the announcement
        Announcement updatedAnnouncement = announcementRepository.findOne(announcement.getId());
        updatedAnnouncement
                .price(UPDATED_PRICE)
                .dateAnnounced(UPDATED_DATE_ANNOUNCED)
                .dateModified(UPDATED_DATE_MODIFIED)
                .expirationDate(UPDATED_EXPIRATION_DATE)
                .phoneNumber(UPDATED_PHONE_NUMBER)
                .type(UPDATED_TYPE)
                .images(null);

        restAnnouncementMockMvc.perform(put("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAnnouncement)))
                .andExpect(status().isBadRequest());

        // TODO Should this be checked when we get badRequest earlier? if it should user @Rollback
        // Validate the Announcement in the database
//        List<Announcement> announcements = announcementRepository.findAll();
//        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);
//        Announcement testAnnouncement = announcements.get(announcements.size() - 1);
//        assertThat(testAnnouncement.getPrice()).isNotEqualTo(UPDATED_PRICE);
//        assertThat(testAnnouncement.getDateAnnounced()).isNotEqualTo(UPDATED_DATE_ANNOUNCED);
//        assertThat(testAnnouncement.getDateModified()).isNotEqualTo(UPDATED_DATE_MODIFIED);
//        assertThat(testAnnouncement.getExpirationDate()).isNotEqualTo(UPDATED_EXPIRATION_DATE);
//        assertThat(testAnnouncement.getPhoneNumber()).isNotEqualTo(UPDATED_PHONE_NUMBER);
//        assertThat(testAnnouncement.getType()).isNotEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = DBUserMocker.ADVERTISER_USERNAME)
    public void deleteAnnouncement() throws Exception {
        announcement.setAuthor(DBUserMocker.ADVERTISER);
        // Initialize the database
        announcementService.save(announcement);

        int databaseSizeBeforeDelete = announcementRepository.findAll().size();

        // Get the announcement
        restAnnouncementMockMvc.perform(delete("/api/announcements/{id}", announcement.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN, username = "admin")
    public void deleteAnnouncementAsAdmin() throws Exception {
        // initialize author of announcement
        User userWithPermit = UserControllerTest.createEntity(Constants.Roles.ADVERTISER);
        userService.save(userWithPermit);

        announcement.setAuthor(userWithPermit);
        // Initialize the database
        announcementService.save(announcement);

        int databaseSizeBeforeDelete = announcementRepository.findAll().size();

        // Get the announcement
        restAnnouncementMockMvc
                .perform(delete("/api/announcements/{id}", announcement.getId())
                        .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = "not_owner_username")
    public void deleteAnnouncementNotOwner() throws Exception {
        announcement.setAuthor(DBUserMocker.ADVERTISER);
        // Initialize the database
        announcementService.save(announcement);

        int databaseSizeBeforeDelete = announcementRepository.findAll().size();

        // Get the announcement
        restAnnouncementMockMvc
                .perform(delete("/api/announcements/{id}", announcement.getId())
                        .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        // Validate the database is empty
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER, username = DBUserMocker.VERIFIER_USERNAME)
    public void deleteAnnouncementAsVerifier() throws Exception {
        announcement.setAuthor(DBUserMocker.VERIFIER);
        // Initialize the database
        announcementService.save(announcement);

        int databaseSizeBeforeDelete = announcementRepository.findAll().size();

        restAnnouncementMockMvc.perform(delete("/api/announcements/{id}", announcement.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void deleteNonExistingAnnouncementAsAdvertiser() throws Exception {
        int databaseSizeBeforeDelete = announcementRepository.findAll().size();

        final int id = databaseSizeBeforeDelete + 1;

        restAnnouncementMockMvc.perform(delete("/api/announcements/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void deleteAnnouncementAsGuest() throws Exception {
        // Initialize the database
        announcementService.save(announcement);

        int databaseSizeBeforeDelete = announcementRepository.findAll().size();

        restAnnouncementMockMvc.perform(delete("/api/announcements/{id}", announcement.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void uploadFile() throws Exception {
        MvcResult result = restAnnouncementMockMvc.perform(fileUpload("/api/announcements/upload")
                .file(fileToBeUpload)
                .contentType(MediaType.IMAGE_PNG))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(context.getEnvironment().getProperty("sct.file_upload.path"));


        String newFileName = result.getResponse().getContentAsString();
        String filePath = NEW_BASE_DIR + File.separator + Constants.FilePaths.ANNOUNCEMENTS + File.separator + newFileName;
        File newFile = new File(filePath);

        System.out.println(newFile.getAbsolutePath());

        assertThat(newFile.exists()).isTrue();

        // Delete created folder
        FileUtils.deleteDirectory(new File(NEW_BASE_DIR));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void uploadFolderDoesNotExist() throws Exception {
        File f = new File(NEW_BASE_DIR);
        if (f.exists()) {
            FileUtils.deleteDirectory(f);
        }

        // Assert that folder does not exist anymore
        assertThat(f.exists()).isFalse();

        restAnnouncementMockMvc.perform(fileUpload("/api/announcements/upload")
                .file(fileToBeUpload))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(f.exists()).isTrue();

        // Delete testing file upload folder
        FileUtils.deleteDirectory(f);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = DBUserMocker.ADVERTISER_USERNAME)
    public void extendExpirationDateAsAdvertiser() throws Exception {
        // Initialize the database
        announcement.setAuthor(DBUserMocker.ADVERTISER);
        announcementService.save(announcement);

        final int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        restAnnouncementMockMvc.perform(put("/api/announcements/{id}", announcement.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(EXPIRATION_DATE_JSON_AFTER))
                .andExpect(status().isOk());

        // Validate the Announcement in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);

        final Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(EXTENDED_DATE_AS_STRING);
    }

    @Test
    @Transactional
    public void extendExpirationDateAsGuest() throws Exception {
        // Initialize the database
        announcementService.save(announcement);

        final int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        restAnnouncementMockMvc.perform(put("/api/announcements/{id}", announcement.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(EXPIRATION_DATE_JSON_AFTER))
                .andExpect(status().isUnauthorized());

        // Validate the Announcement in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);

        final Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(DEFAULT_DATE_ANNOUNCED);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void extendExpirationDateAsVerifier() throws Exception {
        // Initialize the database
        announcementService.save(announcement);

        final int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        restAnnouncementMockMvc.perform(put("/api/announcements/{id}", announcement.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(EXPIRATION_DATE_JSON_AFTER))
                .andExpect(status().isForbidden());

        // Validate the Announcement in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);

        final Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(DEFAULT_DATE_ANNOUNCED);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void extendExpirationDateAsAdvertiserWithInvalidJsonObject() throws Exception {
        // Initialize the database
        announcementService.save(announcement);

        final int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        final MvcResult result = restAnnouncementMockMvc.perform(put("/api/announcements/{id}", announcement.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(EXPIRATION_DATE_JSON_INVALID_NAME))
                .andExpect(status().isBadRequest())
                .andReturn();


        final Integer errorCode = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorCode).isEqualTo(HeaderUtil.ERROR_CODE_NO_EXPIRATION_DATE);

        // Validate the Announcement in the database
        final List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);

        final Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(DEFAULT_DATE_ANNOUNCED);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void extendExpirationDateAsAdvertiserToNonExistingAdvertisement() throws Exception {

        final int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        final MvcResult result = restAnnouncementMockMvc.perform(put("/api/announcements/{id}", 99)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(EXPIRATION_DATE_JSON_AFTER))
                .andExpect(status().isNotFound())
                .andReturn();


        final Integer errorCode = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorCode).isEqualTo(HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY);

        // Validate the Announcement in the database
        final List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void extendExpirationDateAsAdvertiserWithInvalidJFormat() throws Exception {
        // Initialize the database
        announcementService.save(announcement);

        final int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        final MvcResult result = restAnnouncementMockMvc.perform(put("/api/announcements/{id}", announcement.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(EXPIRATION_DATE_JSON_INVALID_FORMAT))
                .andExpect(status().isBadRequest())
                .andReturn();


        final Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_INVALID_DATE_FORMAT);

        // Validate the Announcement in the database
        final List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);

        final Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(DEFAULT_DATE_ANNOUNCED);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void extendExpirationDateAsAdvertiserWithInvalidDate() throws Exception {
        // Initialize the database
        announcementService.save(announcement);

        final int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        final MvcResult result = restAnnouncementMockMvc.perform(put("/api/announcements/{id}", announcement.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(EXPIRATION_DATE_JSON_BEFORE))
                .andExpect(status().isBadRequest())
                .andReturn();

        final Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_PAST_DATE);

        // Validate the Announcement in the database
        final List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);

        final Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getExpirationDate()).isEqualTo(DEFAULT_DATE_ANNOUNCED);
    }

    @Test
    @Transactional
    public void getAllNonDeletedAnnouncementsAsGuest() throws Exception {
        // Add Non Deleted Announcement
        announcementRepository.saveAndFlush(announcement);

        final Long announcementsNonDeletedCount = announcementRepository.findAllByDeleted(false, null).getTotalElements();

        // Get all non deleted announcements
        restAnnouncementMockMvc.perform(get("/api/announcements/deleted/{status}", false))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(announcementsNonDeletedCount))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].dateAnnounced").value(hasItem((int) DEFAULT_DATE_ANNOUNCED.getTime())))
                .andExpect(jsonPath("$.[*].dateModified").value(hasItem((int) DEFAULT_DATE_MODIFIED.getTime())))
                .andExpect(jsonPath("$.[*].expirationDate").value(hasItem((int) DEFAULT_EXPIRATION_DATE.getTime())))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED)))
                .andReturn();
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getAllDeletedAnnouncementsAsAdmin() throws Exception {

        final boolean ANNOUNCEMENT_DELETED = true;

        announcement.deleted(ANNOUNCEMENT_DELETED);

        // Add Deleted Announcement
        announcementRepository.saveAndFlush(announcement);

        final Long announcementsDeletedCount = announcementRepository.findAllByDeleted(true, null).getTotalElements();

        // Get all non deleted announcements
        restAnnouncementMockMvc.perform(get("/api/announcements/deleted/{status}", true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(announcementsDeletedCount))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].dateAnnounced").value(hasItem((int) DEFAULT_DATE_ANNOUNCED.getTime())))
                .andExpect(jsonPath("$.[*].dateModified").value(hasItem((int) DEFAULT_DATE_MODIFIED.getTime())))
                .andExpect(jsonPath("$.[*].expirationDate").value(hasItem((int) DEFAULT_EXPIRATION_DATE.getTime())))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(ANNOUNCEMENT_DELETED)));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void getAllDeletedAnnouncementsAsVerifier() throws Exception {

        final boolean ANNOUNCEMENT_DELETED = true;

        // Get all non deleted announcements
        restAnnouncementMockMvc.perform(get("/api/announcements/deleted/{status}", ANNOUNCEMENT_DELETED))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER)
    public void getAllNonDeletedAnnouncementsAsVerifier() throws Exception {

        final boolean ANNOUNCEMENT_DELETED = false;

        announcement.deleted(ANNOUNCEMENT_DELETED);

        // Add Deleted Announcement
        announcementRepository.saveAndFlush(announcement);

        final Long announcementsDeletedCount = announcementRepository.findAllByDeleted(ANNOUNCEMENT_DELETED, null).getTotalElements();

        // Get all non deleted announcements
        restAnnouncementMockMvc.perform(get("/api/announcements/deleted/{status}", ANNOUNCEMENT_DELETED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(announcementsDeletedCount))))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].dateAnnounced").value(hasItem((int) DEFAULT_DATE_ANNOUNCED.getTime())))
                .andExpect(jsonPath("$.[*].dateModified").value(hasItem((int) DEFAULT_DATE_MODIFIED.getTime())))
                .andExpect(jsonPath("$.[*].expirationDate").value(hasItem((int) DEFAULT_EXPIRATION_DATE.getTime())))
                .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].verified").value(hasItem(DEFAULT_VERIFIED)))
                .andExpect(jsonPath("$.[*].deleted").value(hasItem(ANNOUNCEMENT_DELETED)));
    }

    // TODO Get announcements by company

    // TODO Get top announcements by company

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER, username = UserConstants.USER_USERNAME)
    public void verifyAnnouncementAsVerifier() throws Exception {

        announcementRepository.saveAndFlush(announcement);

        final int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        restAnnouncementMockMvc.perform(put("/api/announcements/{announcementId}/verify", announcement.getId()))
                .andExpect(status().isOk());

        // Validate the Announcement in the database
        final List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);

        final Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getVerified()).isEqualTo(UPDATED_VERIFIED);
    }

    @Test
    @Transactional
    public void verifyAnnouncementAsGuest() throws Exception {
        announcementRepository.saveAndFlush(announcement);

        restAnnouncementMockMvc.perform(put("/api/announcements/{announcementId}/verify", announcement.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER, username = UserConstants.NEW_USER_USERNAME)
    public void verifyAnnouncementAsAdvertiser() throws Exception {

        announcementRepository.saveAndFlush(announcement);

        restAnnouncementMockMvc.perform(put("/api/announcements/{announcementId}/verify", announcement.getId()))
                .andExpect(status().isForbidden());

        // Validate the Announcement in the database
        final List<Announcement> announcements = announcementRepository.findAll();

        final Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getVerified()).isEqualTo(DEFAULT_VERIFIED);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.VERIFIER, username = UserConstants.USER_USERNAME)
    public void verifyNonExistingAnnouncementAsVerifier() throws Exception {

        final int dbSize = announcementRepository.findAll().size();

        final MvcResult result = restAnnouncementMockMvc.perform(put("/api/announcements/{announcementId}/verify", dbSize + 1))
                .andExpect(status().isNotFound())
                .andReturn();

        final Integer errorCode = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorCode).isEqualTo(HeaderUtil.ERROR_CODE_NON_EXISTING_ENTITY);

        final List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(dbSize);
    }

    @Test
    @Transactional
    public void searchAnnouncementsWithoutAnyAttribute() throws Exception {
        final int dbSize = announcementRepository.findAllByDeleted(false, null).getContent().size();
        final int requiredSize = dbSize < PAGE_SIZE ? dbSize : PAGE_SIZE;

        restAnnouncementMockMvc.perform(get("/api/announcements/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(requiredSize))))
                .andReturn();
    }

    @Test
    @Transactional
    public void searchAnnouncementsByPrice() throws Exception {
        announcementRepository.saveAndFlush(announcement);

        restAnnouncementMockMvc.perform(get("/api/announcements/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("startPrice", String.valueOf(DEFAULT_PRICE - 1))
                .param("endPrice", String.valueOf(DEFAULT_PRICE + 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].price", everyItem(both(greaterThan(DEFAULT_PRICE - 1)).and(lessThan(DEFAULT_PRICE + 1)))))
                .andReturn();
    }

    @Test
    @Transactional
    public void searchAnnouncementsByLimitPriceInclude() throws Exception {
        announcementRepository.saveAndFlush(announcement);

        restAnnouncementMockMvc.perform(get("/api/announcements/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("startPrice", String.valueOf(DEFAULT_PRICE))
                .param("endPrice", String.valueOf(DEFAULT_PRICE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].price", everyItem(greaterThanOrEqualTo(DEFAULT_PRICE))))
                .andExpect(jsonPath("$.[*].price", everyItem(lessThanOrEqualTo(DEFAULT_PRICE))))
                .andExpect(jsonPath("$.[*].price", hasItem(DEFAULT_PRICE)))
                .andReturn();
    }

    @Test
    @Transactional
    public void searchAnnouncementsByAuthorNameAndTypeAndPhoneNumber() throws Exception {
        // prepare db data
        User author = userService.findOne(UserConstants.USER_ID);
        announcement.setAuthor(author);
        announcementService.save(announcement);

        final String randomAuthorName = getRandomCaseInsensitiveSubstring(author.getFirstName());
        final String randomType = getRandomCaseInsensitiveSubstring(DEFAULT_TYPE);
        final String randomPhoneNumber = getRandomCaseInsensitiveSubstring(DEFAULT_PHONE_NUMBER);

        restAnnouncementMockMvc.perform(get("/api/announcements/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("authorName", randomAuthorName)
                .param("type", randomType)
                .param("phoneNumber", randomPhoneNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].author.firstName", everyItem(containsIgnoringCase(randomAuthorName))))
                .andExpect(jsonPath("$.[*].author.firstName", hasItem(author.getFirstName())))
                .andExpect(jsonPath("$.[*].type", everyItem(containsIgnoringCase(randomType))))
                .andExpect(jsonPath("$.[*].type", hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].phoneNumber", everyItem(containsIgnoringCase(randomPhoneNumber))))
                .andExpect(jsonPath("$.[*].phoneNumber", hasItem(DEFAULT_PHONE_NUMBER)))
                .andReturn();
    }

    @Test
    @Transactional
    public void searchAnnouncementsByLocation() throws Exception {
        // prepare db data
        User author = userService.findOne(UserConstants.USER_ID);
        RealEstate realEstate = realEstateService.findOne(RealEstateConstants.ID);
        announcement.setAuthor(author);
        announcement.setRealEstate(realEstate);
        announcementService.save(announcement);
        Location location = realEstate.getLocation();

        final String randomCountry = getRandomCaseInsensitiveSubstring(location.getCountry());
        final String randomCity = getRandomCaseInsensitiveSubstring(location.getCity());
        final String randomCityRegion = getRandomCaseInsensitiveSubstring(location.getCityRegion());
        final String randomStreet = getRandomCaseInsensitiveSubstring(location.getStreet());

        restAnnouncementMockMvc.perform(get("/api/announcements/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("country", randomCountry)
                .param("cityRegion", randomCityRegion)
                .param("city", randomCity)
                .param("street", randomStreet)
                .param("streetNumber", location.getStreetNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].realEstate.location.country", everyItem(containsIgnoringCase(randomCountry))))
                .andExpect(jsonPath("$.[*].realEstate.location.country", hasItem(location.getCountry())))
                .andExpect(jsonPath("$.[*].realEstate.location.city", everyItem(containsIgnoringCase(randomCity))))
                .andExpect(jsonPath("$.[*].realEstate.location.city", hasItem(location.getCity())))
                .andExpect(jsonPath("$.[*].realEstate.location.cityRegion", everyItem(containsIgnoringCase(randomCityRegion))))
                .andExpect(jsonPath("$.[*].realEstate.location.cityRegion", hasItem(location.getCityRegion())))
                .andExpect(jsonPath("$.[*].realEstate.location.street", everyItem(containsIgnoringCase(randomStreet))))
                .andExpect(jsonPath("$.[*].realEstate.location.street", hasItem(location.getStreet())))
                .andExpect(jsonPath("$.[*].realEstate.location.streetNumber", everyItem(equalToIgnoringCase(location.getStreetNumber()))))
                .andReturn();
    }

    @Test
    @Transactional
    public void searchAnnouncementsWithWrongNameQueryKey() throws Exception {
        final int dbSize = announcementRepository.findAllByDeleted(false, null).getContent().size();
        final int requiredSize = dbSize < PAGE_SIZE ? dbSize : PAGE_SIZE;

        restAnnouncementMockMvc.perform(get("/api/announcements/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("nameeeee", "nameee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(requiredSize))))
                .andReturn();
    }


    @Test
    @Transactional
    public void searchDeletedAnnouncements() throws Exception {
        Announcement persisted = announcementRepository.saveAndFlush(announcement.deleted(true));

        final int dbSize = announcementRepository.findAllByDeleted(true, null).getContent().size();
        assertThat(dbSize).isGreaterThan(0);

        restAnnouncementMockMvc.perform(get("/api/announcements/search")
                .param("sort", "id,desc")
                .param("size", String.valueOf(PAGE_SIZE))
                .param("page", "0")
                .param("phoneNumber", announcement.getPhoneNumber()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].id", everyItem(not(comparesEqualTo(Integer.valueOf(persisted.getId().intValue()))))))
                .andReturn();
    }
}
