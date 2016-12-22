package rs.acs.uns.sw.sct.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.SctServiceApplication;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.sct.constants.ReportConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SctServiceApplication.class)
@TestPropertySource(locations = "classpath:application.properties")
public class ReportServiceTest {
    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    private Report newReport;
    private Report updatedReport;
    private Report existingReport;

    /**
     * Asserts equality of two Reports.
     *
     * @param report1 One of the Reports to be compared
     * @param report2 The other Reports to be compared
     */
    private void compareReports(Report report1, Report report2) {
        if (report1.getId() != null && report2.getId() != null)
            assertThat(report1.getId()).isEqualTo(report2.getId());
        assertThat(report1.getType()).isEqualTo(report2.getType());
        assertThat(report1.getAnnouncement().getId()).isEqualTo(report2.getAnnouncement().getId());
        if (report1.getEmail() != null && report2.getEmail() != null)
            System.out.println(report1.getEmail());
        System.out.println(report2.getEmail());
        assertThat(report1.getEmail()).isEqualTo(report2.getEmail());
        if (report1.getReporter() != null && report2.getReporter() != null)
            assertThat(report1.getReporter().getId()).isEqualTo(report2.getReporter().getId());
        assertThat(report1.getContent()).isEqualTo(report2.getContent());
        assertThat(report1.getStatus()).isEqualTo(report2.getStatus());
    }

    /**
     * Initializes all objects required for testing
     */
    @Before
    public void initTest() {
        existingReport = new Report()
                .id(ID)
                .email(EMAIL)
                .type(TYPE)
                .content(CONTENT)
                .status(STATUS)
                .reporter(REPORTER)
                .announcement(DEFAULT_ANNOUNCEMENT);
        newReport = new Report()
                .id(null)
                .email(NEW_EMAIL)
                .type(NEW_TYPE)
                .content(NEW_CONTENT)
                .status(NEW_STATUS)
                .reporter(NEW_REPORTER)
                .announcement(DEFAULT_ANNOUNCEMENT);
        updatedReport = new Report()
                .id(null)
                .email(UPDATED_EMAIL)
                .type(UPDATED_TYPE)
                .content(UPDATED_CONTENT)
                .status(UPDATED_STATUS)
                .reporter(UPDATED_REPORTER)
                .announcement(DEFAULT_ANNOUNCEMENT);
    }

    /**
     * Tests pageable retrieval of Reports
     * <p>
     * This test uses a PageRequest object to specify the number
     * of results it wants to receive when it requests Reports,
     * then asserts that the number of returned results matches
     * the page size in our request.
     */
    @Test
    @Transactional(readOnly = true)
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Report> reports = reportRepository.findAll(pageRequest);
        assertThat(reports).hasSize(PAGE_SIZE);
    }

    /**
     * Tests retrieval of all Reports
     * <p>
     * This test finds all Reports on the repository and asserts
     * that the number of returned results is equal to the number of
     * Reports on the database
     */
    @Test
    @Transactional(readOnly = true)
    public void testFindAll() {
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(DB_COUNT_REPORTS);
    }

    /**
     * Tests retrieval of a single Report.
     * <p>
     * This test uses the id of an Report that is in the repository
     * to search for it, then asserts that the returned value is not null
     * and compares the returned Report to an existing Report.
     */
    @Test
    @Transactional(readOnly = true)
    public void testFindOne() {
        Report report = reportService.findOne(ID);
        assertThat(report).isNotNull();

        compareReports(report, existingReport);
    }

    /**
     * Tests addition of Reports
     * <p>
     * This test saves a new Report using the ReportService,
     * then it finds all Reports and asserts that the size of the results
     * has increased by one. It also asserts that the new Report that is on
     * the database equals the Report we added.
     */
    @Test
    @Transactional
    public void testAdd() {
        int dbSizeBeforeAdd = reportRepository.findAll().size();

        Report dbReport = reportService.save(newReport);
        assertThat(dbReport).isNotNull();

        // Validate that new report is in the database
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(dbSizeBeforeAdd + 1);

        compareReports(dbReport, newReport);
    }

    /**
     * Tests updating of Reports.
     * <p>
     * This test retrieves a Report using the service, then changes
     * its attributes and saves it to the database. Then it asserts that
     * the object on the database is not null and equals our updated Report.
     */
    @Test
    @Transactional
    public void testUpdate() {
        Report dbReport = reportService.findOne(ID);

        dbReport.setContent(UPDATED_CONTENT);
        dbReport.setReporter(UPDATED_REPORTER);
        dbReport.setEmail(UPDATED_EMAIL);
        dbReport.setStatus(UPDATED_STATUS);
        dbReport.setType(UPDATED_TYPE);

        Report updatedDbReport = reportService.save(dbReport);
        assertThat(updatedDbReport).isNotNull();

        compareReports(updatedDbReport, updatedReport);
    }

    /**
     * Tests removal of Reports
     * <p>
     * This test deletes a Report using the service, then
     * asserts that the number of Reports on the database
     * has been reduced by one. It also asserts that an object
     * with the deleted Report's id does not exists on the
     * database.
     */
    @Test
    @Transactional
    public void testRemove() {
        int dbSizeBeforeRemove = reportRepository.findAll().size();
        reportService.delete(ID);

        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(dbSizeBeforeRemove - 1);

        Report dbReport = reportService.findOne(ID);
        assertThat(dbReport).isNull();
    }

    /**
     * Tests searching for Reports by their status
     * <p>
     * This test finds all Reports that have a certain status,
     * asserts that every one of the results has that status
     */
    @Test
    public void testFindByStatus() {
        Page<Report> reports = reportService.findByStatus(FIND_STATUS, PAGEABLE);

        for (Report report : reports.getContent()) {
            assertThat(report.getStatus()).isEqualTo(FIND_STATUS);
        }
    }

    /**
     * Tests searching for Reports by their author's e-mail
     * <p>
     * This test finds all Reports that have a certain author's email,
     * asserts that every one of the results has that author's email.
     */
    @Test
    public void testFindByAuthorEmail() {
        Page<Report> reports = reportService.findByAuthorEmail(FIND_AUTHOR_EMAIL, PAGEABLE);

        for (Report report : reports.getContent()) {
            assertThat(report.getEmail()).isEqualTo(FIND_AUTHOR_EMAIL);
        }
    }

    /*
     * Negative tests
	 */

    /**
     * Tests adding an Report with a null emial value
     * <p>
     * This test sets an Report's emial to null, then
     * attempts to add it to the database. As emial is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullEmail() {
        newReport.setEmail(null);
        reportService.save(newReport);
        // rollback previous email
        newReport.setEmail(NEW_EMAIL);
    }

    /**
     * Tests adding an Report with a null type value
     * <p>
     * This test sets an Report's type to null, then
     * attempts to add it to the database. As type is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullType() {
        newReport.setType(null);
        reportService.save(newReport);
        // rollback previous type
        newReport.setType(NEW_TYPE);
    }

    /**
     * Tests adding an Report with a null content value
     * <p>
     * This test sets an Report's content to null, then
     * attempts to add it to the database. As content is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullContent() {
        newReport.setContent(null);
        reportService.save(newReport);
        // rollback previous content
        newReport.setContent(NEW_CONTENT);
    }

    /**
     * Tests adding an Report with a null status value
     * <p>
     * This test sets an Report's status to null, then
     * attempts to add it to the database. As status is a
     * non-nullable field, the test receives a Constraint
     * Violation exception.
     */
    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullStatus() {
        newReport.setStatus(null);
        reportService.save(newReport);
        // rollback previous status
        newReport.setStatus(NEW_STATUS);
    }

    /**
     * Tests searching for Reports by specific arguments
     * <p>
     * This test saves a Report to the database, then searches for
     * it using all the same arguments, then asserts that the returned value is not null.
     */
    @Test
    @Transactional
    public void findSameReports() {
        Report savedReport = reportService.save(newReport);

        Report same = reportService.findByReporterEmailAndStatusAndAnnouncementId(
                savedReport.getEmail(), savedReport.getStatus(), savedReport.getAnnouncement().getId());

        assertThat(same).isNotNull();
    }
}
