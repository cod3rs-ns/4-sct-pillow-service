package rs.acs.uns.sw.awt_test.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.awt_test.AwtTestSiitProject2016ApplicationTests;
import rs.acs.uns.sw.awt_test.util.TestUtil;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ReportResource REST controller.
 *
 * @see ReportController
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AwtTestSiitProject2016ApplicationTests.class)
public class ReportControllerTest {

    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";

    private static final String DEFAULT_TYPE = "AAAAA";
    private static final String UPDATED_TYPE = "BBBBB";

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private EntityManager em;

    private MockMvc restReportMockMvc;

    private Report report;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Report createEntity(EntityManager em) {
        Report report = new Report()
                .email(DEFAULT_EMAIL)
                .type(DEFAULT_TYPE);
        return report;
    }

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReportController reportCtrl = new ReportController();
        ReflectionTestUtils.setField(reportCtrl, "reportService", reportService);
        this.restReportMockMvc = MockMvcBuilders.standaloneSetup(reportCtrl)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        report = createEntity(em);
    }

    @Test
    @Transactional
    public void createReport() throws Exception {
        int databaseSizeBeforeCreate = reportRepository.findAll().size();

        // Create the Report

        restReportMockMvc.perform(post("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(report)))
                .andExpect(status().isCreated());

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeCreate + 1);
        Report testReport = reports.get(reports.size() - 1);
        assertThat(testReport.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testReport.getType()).isEqualTo(DEFAULT_TYPE);
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
    public void getAllReports() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get all the reports
        restReportMockMvc.perform(get("/api/reports?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(report.getId().intValue())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    public void getReport() throws Exception {
        // Initialize the database
        reportRepository.saveAndFlush(report);

        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", report.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(report.getId().intValue()))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingReport() throws Exception {
        // Get the report
        restReportMockMvc.perform(get("/api/reports/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReport() throws Exception {
        // Initialize the database
        reportService.save(report);

        int databaseSizeBeforeUpdate = reportRepository.findAll().size();

        // Update the report
        Report updatedReport = reportRepository.findOne(report.getId());
        updatedReport
                .email(UPDATED_EMAIL)
                .type(UPDATED_TYPE);

        restReportMockMvc.perform(put("/api/reports")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedReport)))
                .andExpect(status().isOk());

        // Validate the Report in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeUpdate);
        Report testReport = reports.get(reports.size() - 1);
        assertThat(testReport.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testReport.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void deleteReport() throws Exception {
        // Initialize the database
        reportService.save(report);

        int databaseSizeBeforeDelete = reportRepository.findAll().size();

        // Get the report
        restReportMockMvc.perform(delete("/api/reports/{id}", report.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(databaseSizeBeforeDelete - 1);
    }
}
