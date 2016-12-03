package rs.acs.uns.sw.sct.announcements;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.GreaterOrEqual;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.constants.AnnouncementConstants;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.DateUtil;
import rs.acs.uns.sw.sct.util.TestUtil;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AnnouncementResource REST controller.
 *
 * @see AnnouncementController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
public class AnnouncementControllerTest {

    private static final Double DEFAULT_PRICE = 0D;
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
    private static final String UPDATED_VERIFIED = "VERIFIED_BBB";

    private static final String EXISTING_USERNAME = "isco";

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restAnnouncementMockMvc;

    private Announcement announcement;

    private AnnouncementDTO announcementDTO;

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
    public static AnnouncementDTO createDTO(){
        return createEntity().convertToDTO();
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AnnouncementController announcementCtrl = new AnnouncementController();
        ReflectionTestUtils.setField(announcementCtrl, "announcementService", announcementService);
        ReflectionTestUtils.setField(announcementCtrl, "userService", userService);
        this.restAnnouncementMockMvc = MockMvcBuilders.standaloneSetup(announcementCtrl)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        announcement = createEntity();
        announcementDTO = createDTO();

        // Set authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(EXISTING_USERNAME);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @Transactional
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
    public void getAllAnnouncements() throws Exception {
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
    public void updateAnnouncement() throws Exception {
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
    public void deleteAnnouncement() throws Exception {
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
}
