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
                .id(AnnouncementConstants.ID);

        return new Report()
                .email(DEFAULT_EMAIL)
                .type(DEFAULT_TYPE)
                .status(DEFAULT_STATUS)
                .content(DEFAULT_CONTENT)
                .announcement(announcement);
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
                .announcement(announcement);
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

    @Before
    public void initTest() {
        report = createEntity();
        anotherReport = createAnotherEntity();
    }

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
                .status(report.getStatus());

        // Create the Report
        MvcResult result = restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sameReport)))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate);

        final String message = result.getResponse().getHeader(HeaderUtil.SCT_HEADER_ALERT);
        assertThat(message).isEqualTo("You can't have more reports for the same advert unless they are with pending status");
    }

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
        assertThat(message).isEqualTo(HeaderUtil.ERROR_MSG_NON_EXISTING_ANNOUNCEMENT);

        final List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate);
    }

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

    @Test
    @Transactional
    public void getAllReportsWithoutAuthority() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(get("/api/reports?sort=id,desc"))
                .andExpect(status().isUnauthorized());
    }

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

    @Test
    @Transactional
    public void getReportAsGuest() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", report.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = AuthorityRoles.ADMIN)
    public void getNonExistingReport() throws Exception {
        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

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
                .andExpect(status().isBadRequest());
        // TODO make fix to throw isOK()

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reports.get(reports.size() - 1);
        assertThat(testReport.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testReport.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testReport.getStatus()).isEqualTo(Constants.ReportStatus.PENDING);
        assertThat(testReport.getContent()).isEqualTo(UPDATED_CONTENT);
    }

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
                .andExpect(status().isBadRequest());
        // TODO make fix to throw isOK();

        // Validate the database is empty
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    public void deleteNonExistingReport() throws Exception {

        final Long reportId = Long.MAX_VALUE;

        final int databaseSizeBeforeDelete = reportRepository.findAll().size();

        restReportMockMvc.perform(delete("/api/reports/{id}", reportId)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

        final List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeDelete);
    }

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
        reportRepository.saveAndFlush(persistReport);

        // Create another report to have reports of two different statuses
        reportRepository.saveAndFlush(anotherReport.status(DEFAULT_STATUS));


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

    @Test
    @Transactional
    public void getReportsByAuthor() throws Exception {

        // FIXME @bblagojevic94

        // Initialize the database
        Report persistReport = reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/author/{email}", report.getEmail()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[?(@.id == " + persistReport.getId() + ")]").exists());
    }

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
                .andExpect(jsonPath("$.status").value(equalTo(status)))
                .andExpect(jsonPath("$.announcement.deleted").value(equalTo(true)));
    }

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

        assertThat("Wrong status of report").isEqualTo(result.getResponse().getContentAsString());
    }

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

        assertThat("Can't modified status of accepted or rejected report!").isEqualTo(result.getResponse().getContentAsString());
    }

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
