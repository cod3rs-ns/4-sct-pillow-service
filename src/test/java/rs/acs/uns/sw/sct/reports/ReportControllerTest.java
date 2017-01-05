package rs.acs.uns.sw.sct.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.announcements.Announcement;
import rs.acs.uns.sw.sct.announcements.AnnouncementService;
import rs.acs.uns.sw.sct.constants.AnnouncementConstants;
import rs.acs.uns.sw.sct.constants.ReportConstants;
import rs.acs.uns.sw.sct.constants.UserConstants;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.*;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ReportResource REST controller.
 *
 * @see ReportController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
@TestPropertySource("classpath:application.properties")
public class ReportControllerTest {

    private static final String DEFAULT_EMAIL = "a@gmail.com";
    private static final String UPDATED_EMAIL = "b@gmail.com";

    private static final String DEFAULT_TYPE = "TYPE_A";
    private static final String UPDATED_TYPE = "TYPE_B";

    private static final String DEFAULT_STATUS = "STATUS_A";
    private static final String UPDATED_STATUS = "STATUS_B";

    private static final String DEFAULT_CONTENT = "CONTENT_A";
    private static final String UPDATED_CONTENT = "CONTENT_B";


    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc restReportMockMvc;

    private Report report;

    private Report anotherReport;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createEntity() {
        Announcement announcement = new Announcement()
                .id(AnnouncementConstants.ID)
                .author(new User().id(1L));

        return new Report()
                .email(DEFAULT_EMAIL)
                .type(DEFAULT_TYPE)
                .status(DEFAULT_STATUS)
                .content(DEFAULT_CONTENT)
                .announcement(announcement)
                .createdAt(new Date());
    }

    /**
     * Create another entity for this test. Another entity is needed because of testing changing status
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createAnotherEntity() {
        Announcement announcement = new Announcement()
                .id(AnnouncementConstants.UPDATED_ID);

        return new Report()
                .email(UPDATED_EMAIL)
                .type(UPDATED_TYPE)
                .status(UPDATED_STATUS)
                .content(UPDATED_CONTENT)
                .announcement(announcement)
                .createdAt(new Date());
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReportController reportCtrl = new ReportController();
        ReflectionTestUtils.setField(reportCtrl, "reportService", reportService);
        ReflectionTestUtils.setField(reportCtrl, "userService", userService);
        ReflectionTestUtils.setField(reportCtrl, "announcementService", announcementService);

        this.restReportMockMvc = MockMvcBuilders
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
        report = createEntity();
        anotherReport = createAnotherEntity();
    }

    /**
     * Tests addition of Report objects as registered user.
     * <p>
     * This test uses a mock user to add a default Report
     * object to the database using a POST method.
     * It then proceeds to check whether the Report object was added successfully,
     * by comparing the number of objects in the database before and after the addition,
     * as well as the default Report's attributes to the Report in the database.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(username = UserConstants.USER_USERNAME)
    public void createReportAsRegisteredUser() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();
        User user = userService.getUserByUsername(UserConstants.USER_USERNAME);

        // Create the Report
        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isCreated());

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate + 1);
        Report testReport = reports.get(reports.size() - 1);
        assertThat(testReport.getEmail()).isEqualTo(user.getEmail());
        assertThat(testReport.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testReport.getStatus()).isEqualTo(Constants.ReportStatus.PENDING);
        assertThat(testReport.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    /**
     * Tests addition of Report which is already in database
     * <p>
     * This test saves a default Report to the database,
     * then attempts to add the same one again, which results in a bad request.
     * Then it asserts that the number of Reports on the database has not changed
     * and that the error message that was received is correct.
     * @throws Exception
     */
    @Test
    @Transactional
    public void createReportSameReportTwoTimesAsSameUser() throws Exception {
        reportService.save(report.status(Constants.ReportStatus.PENDING));
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        Report sameReport = new Report()
                .announcement(report.getAnnouncement())
                .email(report.getEmail())
                .type(report.getType())
                .content(report.getContent())
                .status(report.getStatus())
                .createdAt(new Date());

        // Create the Report
        MvcResult result = restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sameReport)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate);

        Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_CANNOT_POST_MULTIPLE_REPORTS);
    }

    /**
     * Tests addition of Report of a verified Announcement
     * <p>
     * This test attempts to add a Report of a verified Announcement to the database,
     * which results in a bad request.
     * Then it asserts that the number of Reports on the database has not changed
     * and that the error message that was received is correct.
     * @throws Exception
     */
    @Test
    @Transactional
    public void createReportAsGuestToVerifiedAnnouncement() throws Exception {

        // Set verified announcement
        report.setAnnouncement(new Announcement().id(2L));

        final int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report
        final MvcResult result = restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest())
                .andReturn();

        final String message = result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ALERT);
        assertThat(message).isEqualTo(HeaderUtil.ERROR_MSG_REPORT_VERIFIED_ANNOUNCEMENT);

        final List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate);
    }

    /**
     * Tests addition of Report of a non-existing Announcement
     * <p>
     * This test attempts to add a Report of a non-existing Announcement to the database,
     * which results in a bad request.
     * Then it asserts that the number of Reports on the database has not changed
     * and that the error message that was received is correct.
     * @throws Exception
     */
    @Test
    @Transactional
    public void createReportAsGuestToWrongAnnouncement() throws Exception {

        // Set wrong announcement
        report.setAnnouncement(new Announcement().id(Long.MAX_VALUE));

        final int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report
        final MvcResult result = restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest())
                .andReturn();

        final String message = result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ALERT);
        assertThat(message).isEqualTo(HeaderUtil.ERROR_MSG_NON_EXISTING_ENTITY);

        final List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate);
    }

    /**
     * Tests addition of Report with id that is already in the database
     * <p>
     * This test attempts to add a Report whose id matches an already existing
     * Report's id to the database,
     * which results in a bad request.
     * Then it asserts that the number of Reports on the database has not changed
     * and that the error message that was received is correct.
     * @throws Exception
     */
    @Test
    @Transactional
    public void createReportAsGuestWithSameReportId() throws Exception {

        // Already exists in database
        report.id(1L);

        final int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report
        final MvcResult result = restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest())
                .andReturn();

        final String message = result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ALERT);
        assertThat(message).isEqualTo(HeaderUtil.ERROR_MSG_CUSTOM_ID);

        final List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate);
    }


    /**
     * Tests addition of Report objects as unregistered user.
     * <p>
     * This test uses no authorization to add a default Report
     * object to the database using a POST method.
     * It then proceeds to check whether the Report object was added successfully,
     * by comparing the number of objects in the database before and after the addition,
     * as well as the default Report's attributes to the Report in the database.
     * @throws Exception
     */
    @Test
    @Transactional
    public void createReportAsGuest() throws Exception {

        final String email = "unregistered.user@mail.com";

        report.email(email);

        final int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report
        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isCreated());

        // Validate the Report in the database
        final List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate + 1);

        final Report testReport = reports.get(reports.size() - 1);
        assertThat(testReport.getEmail()).isEqualTo(email);
        assertThat(testReport.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testReport.getStatus()).isEqualTo(Constants.ReportStatus.PENDING);
        assertThat(testReport.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().size();
        // set the field null
        report.setEmail(null);

        // Create the Report, which fails.

        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest());

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests whether the "Type" field is nullable
     * <p>
     * This test attempts to add a Report object with a null "Type" value to the database,
     * this is forbidden as the "Type" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().size();
        // set the field null
        report.setType(null);

        // Create the Report, which fails.
        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest());

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests whether the "Status" field is nullable
     * <p>
     * This test attempts to add a Report object with a null "Status" value to the database,
     * this is forbidden as the "Status" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().size();
        // set the field null
        report.setStatus(null);

        // Create the Report, which fails.

        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest());

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests whether the "Content" field is nullable
     * <p>
     * This test attempts to add a Report object with a null "Content" value to the database,
     * this is forbidden as the "Content" field is non-nullable. Other than expecting a "Bad request" status,
     * the test asserts that the number of objects in database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = reportRepository.findAll().size();
        // set the field null
        report.setContent(null);

        // Create the Report, which fails.

        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isBadRequest());

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeTest);
    }

    /**
     * Tests getting all Reports as an Admin
     * <p>
     * This test uses a mocked Admin user to request all Reports
     * from the database. It then asserts that the received results
     * match what was expected.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getAllReports() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(get("/api/reports?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }

    /**
     * Tests getting all RealEstates as a Guest
     * <p>
     * This test uses a mocked Guest user to request all RealEstates
     * from the database, which fails and returns an Unauthorized status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllReportsWithoutAuthority() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(get("/api/reports?sort=id,desc"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests getting a single Report by id as an Admin
     * <p>
     * This test uses a mocked Admin to retrieve a Report object from the database
     * using its ID. It then checks whether the object's attributes have valid values.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getReportAsAdmin() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", report.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(report.getId().intValue()))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
                .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
                .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
    }

    /**
     * Tests getting single Report by id as an Advertiser
     * <p>
     * This test uses a mocked Advertiser user to retrieve a Report object from the database
     * using its ID, which fails and returns a Forbidden status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getReportAsAdvertiser() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", report.getId()))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests getting single Report by id as a Guest
     * <p>
     * This test uses a mocked Guest user to retrieve a Report object from the database
     * using its ID, which fails and returns an Unauthorized status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getReportAsGuest() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", report.getId()))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests invalid retrieval attempts
     * <p>
     * This tests attempts to retrieve an Report object which is not in the database
     * by searching for a non-existent id.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getNonExistingReport() throws Exception {
        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests Report updating as Guest.
     * <p>
     * This test saves a Report object to the database,
     * then updates the values of its attributes and uses no authorization to
     * attempt to use PUT to save the object, which fails because the user is unauthorized.
     * The test then asserts that the number of objects in the database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void updateReportAsGuest() throws Exception {
        report.reporter(DBUserMocker.ADVERTISER);
        // Initialize the database
        reportService.save(report);

        int databaseSizeBeforeUpdate = reportRepository.findAll().size();

        // Update the report
        Report updatedReport = reportRepository.findOne(report.getId());
        updatedReport
                .email(UPDATED_EMAIL)
                .type(UPDATED_TYPE)
                .status(Constants.ReportStatus.PENDING)
                .content(UPDATED_CONTENT);

        restReportMockMvc.perform(put("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedReport)))
                .andExpect(status().isUnauthorized());

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeUpdate);
    }

    /**
     * Tests Report deletion as a Guest
     * <p>
     * This tests attempts to delete a Report
     * on the database with no authorization,
     * which is not allowed. It then asserts
     * that the number of Reports on the
     * database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    public void deleteReportAsGuest() throws Exception {
        report.reporter(DBUserMocker.ADVERTISER);
        // Initialize the database
        reportService.save(report);

        int databaseSizeBeforeDelete = reportRepository.findAll().size();

        // Get the report
        restReportMockMvc.perform(delete("/api/reports/{id}", report.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isUnauthorized());

        // Validate the database is empty
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests deletion of non-existing Reports
     * <p>
     * This tests uses a mocked Admin user to
     * attempt to delete a RealEstate
     * on the database,
     * which is not allowed. It then asserts
     * that the number of RealEstates on the
     * database has not changed.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void deleteNonExistingReport() throws Exception {

        final Long reportId = Long.MAX_VALUE;

        final int databaseSizeBeforeDelete = reportRepository.findAll().size();

        restReportMockMvc.perform(delete("/api/reports/{id}", reportId)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

        final List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeDelete);
    }

    /**
     * Tests changing a Report's status
     * <p>
     * This test saves a Report to the database, then performs a search for
     * all reports with its status, asserting that it shows up in that list.
     * Then it changes the Report's status and saves it again. Now, when the
     * same search as before is done, the Report does not show up. Then
     * we assert that the Report shows up in a search for all Reports with
     * its status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void changeReportStatus() throws Exception {
        // Initialize the database
        Report persistReport = reportRepository.saveAndFlush(report);

        // When new report is added with one status, then it is available for validators that expects reports with that status
        // Get the reports by status
        restReportMockMvc.perform(get("/api/reports/status/{status}", DEFAULT_STATUS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].status").value(DEFAULT_STATUS))
                .andExpect(jsonPath("$.[?(@.id == " + report.getId() + ")]").exists());


        persistReport.setStatus(UPDATED_STATUS);
        persistReport.setAnnouncement(new Announcement().id(1L).author(new User().id(1L)));
        reportRepository.saveAndFlush(persistReport);

        // Create another report to have reports of two different statuses
        reportRepository.saveAndFlush(anotherReport.status(DEFAULT_STATUS)
        .announcement(new Announcement().id(1L).author(new User().id(1L))));


        // When report is solved (change its status), then it is not in the previous list
        restReportMockMvc.perform(get("/api/reports/status/{status}", DEFAULT_STATUS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].status").value(DEFAULT_STATUS))
                .andExpect(jsonPath("$.[?(@.id == " + persistReport.getId() + ")]").doesNotExist());

        // But it is in the another list of all solved reports
        restReportMockMvc.perform(get("/api/reports/status/{status}", UPDATED_STATUS))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].status").value(UPDATED_STATUS))
                .andExpect(jsonPath("$.[?(@.id == " + persistReport.getId() + ")]").exists());
    }

    /**
     * Tests searching for Reports by a specific User
     * <p>
     * This test saves a Report then asserts that it can be found
     * on the database by searching for Reports by its author.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getReportsByAuthor() throws Exception {
        
        // Initialize the database
        Report persistReport = reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/author/{email}", report.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[?(@.id == " + persistReport.getId() + ")]").exists());
    }

    /**
     * Tests getting all RealEstates as an Admin
     * <p>
     * This test uses a mocked Admin user to request all Reports that have a "pending" status
     * from the database. It then asserts that the received results
     * match what was expected.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getAllReportsByStatusAsAdmin() throws Exception {

        final String status = "pending";

        report.status(status);

        // Initialize the database
        reportRepository.saveAndFlush(report);

        final Long count = reportRepository.findByStatus(status, ReportConstants.PAGEABLE).getTotalElements();

        // Get all the reports by status
        restReportMockMvc.perform(get("/api/reports/status/{status}", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(Math.toIntExact(count))))
                .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].status").value(hasItem(status)))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }

    /**
     * Tests getting all Report as an Advertiser
     * <p>
     * This test uses a mocked Advertiser user to request all Reports with a "true" status
     * from the database, which fails and returns a Forbidden status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void getAllReportsByStatusAsAdvertiser() throws Exception {

        final String status = "true";

        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(get("/api/reports/status/{status}", status))
                .andExpect(status().isForbidden());
    }

    /**
     * Tests getting all Report as a Guest
     * <p>
     * This test uses no authorization to request all Reports with a "true" status
     * from the database, which fails and returns a Unauthorized status.
     * @throws Exception
     */
    @Test
    @Transactional
    public void getAllReportsByStatusAsGuest() throws Exception {

        final String status = "true";

        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(get("/api/reports/status/{status}", status))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests accepting Reports
     * <p>
     * This test saves a Report with "pending" status to the database,
     * then uses a mocked Admin user to change its status to "accepted".
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void acceptReport() throws Exception {
        final String status = Constants.ReportStatus.ACCEPTED;

        // Initialize the database
        report.setStatus(Constants.ReportStatus.PENDING);
        Report savedReport = reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(put("/api/reports/resolve/{id}", savedReport.getId())
                .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.status").value(equalTo(status)));
    }

    /**
     * Tests rejecting Reports
     * <p>
     * This test saves a Report with "pending" status to the database,
     * then uses a mocked Admin user to change its status to "rejected".
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void rejectReport() throws Exception {
        final String status = Constants.ReportStatus.REJECTED;

        // Initialize the database
        report.setStatus(Constants.ReportStatus.PENDING);
        Report savedReport = reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(put("/api/reports/resolve/{id}", savedReport.getId())
                .param("status", status))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.status").value(equalTo(status)));
    }

    /**
     * Tests resolving non-existing Reports
     * <p>
     * This test uses a mocked Admin user to attempt to change a non-existing
     * Report's status, which returns a "Not found" status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void resolveReportWrongId() throws Exception {
        final Long reportId = Long.MAX_VALUE;

        Report report = reportService.findOne(reportId);
        assertThat(report).isNull();

        final String status = Constants.ReportStatus.ACCEPTED;

        // Get all the reports
        restReportMockMvc.perform(put("/api/reports/resolve/{id}", reportId)
                .param("status", status))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests resolving a Report without a status
     * <p>
     * This test uses a mocked Admin user to attempt to resolve a Report
     * without using a status parameter, which results in a "Bad request" status.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void resolveReportWithoutStatus() throws Exception {
        report.setStatus(Constants.ReportStatus.PENDING);
        Report savedReport = reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(put("/api/reports/resolve/{id}", savedReport.getId()))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests resolving a Report using an invalid status
     * <p>
     * This test uses a mocked Admin user to attempt to resolve a Report
     * using an invalid status value, which results in a "Bad request" status.
     * It then asserts that the error key that is returned is the correct one.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void resolveReportWithWrongStatusQuery() throws Exception {
        report.setStatus(Constants.ReportStatus.PENDING);
        Report savedReport = reportRepository.saveAndFlush(report);

        // Get all the reports
        MvcResult result = restReportMockMvc.perform(put("/api/reports/resolve/{id}", savedReport.getId())
                .param("status", DEFAULT_STATUS))
                .andExpect(status().isBadRequest()).andReturn();

        Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_PROVIDED_UNKNOWN_REPORT_STATUS);
    }

    /**
     * Tests resolving a Report which was already resolved.
     * <p>
     * This test uses a mocked Admin user to attempt to resolve a Report
     * which was already resolved, which results in a "Bad request" status.
     * It then asserts that the error key that is returned is the correct one.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void resolveReportThatIsAlreadyResolved() throws Exception {
        report.setStatus(Constants.ReportStatus.ACCEPTED);
        Report savedReport = reportRepository.saveAndFlush(report);

        // Get all the reports
        MvcResult result = restReportMockMvc.perform(put("/api/reports/resolve/{id}", savedReport.getId())
                .param("status", Constants.ReportStatus.REJECTED))
                .andExpect(status().isBadRequest()).andReturn();

        Integer errorKey = Integer.valueOf(result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ERROR_KEY));
        assertThat(errorKey).isEqualTo(HeaderUtil.ERROR_CODE_REPORT_ALREADY_RESOLVED);
    }

    /**
     * Tests resolving a Report as a Guest
     * <p>
     * This attempts to resolve a Report without any authorization,
     * which results in a "Unauthorized" status.
     * It then asserts that the error key that is returned is the correct one.
     * @throws Exception
     */
    @Test
    @Transactional
    public void resolveReportAsGuest() throws Exception {
        final String status = Constants.ReportStatus.ACCEPTED;

        // Initialize the database
        report.setStatus(Constants.ReportStatus.PENDING);
        Report savedReport = reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(put("/api/reports/resolve/{id}", savedReport.getId())
                .param("status", status))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests resolving a Report as an Advertiser
     * <p>
     * This attempts to resolve a Report using a mocked Advertiser user,
     * which results in a "Forbidden" status.
     * It then asserts that the error key that is returned is the correct one.
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADVERTISER)
    public void resolveReportAsAdvertiser() throws Exception {
        final String status = Constants.ReportStatus.ACCEPTED;

        // Initialize the database
        report.setStatus(Constants.ReportStatus.PENDING);
        Report savedReport = reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(put("/api/reports/resolve/{id}", savedReport.getId())
                .param("status", status))
                .andExpect(status().isForbidden());
    }
}
