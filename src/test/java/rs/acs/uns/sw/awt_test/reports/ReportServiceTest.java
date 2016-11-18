package rs.acs.uns.sw.awt_test.reports;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.awt_test.AwtTestSiitProject2016ApplicationTests;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static rs.acs.uns.sw.awt_test.constants.ReportConstants.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AwtTestSiitProject2016ApplicationTests.class)
public class ReportServiceTest {
    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    private Report newReport;
    private Report updatedReport;
    private Report existingReport;


    private void compareReports(Report report1, Report report2){
        if (report1.getId() != null && report2.getId() != null)
            assertThat(report1.getId()).isEqualTo(report2.getId());
        assertThat(report1.getType()).isEqualTo(report2.getType());
        assertThat(report1.getAnnouncement().getId()).isEqualTo(report2.getAnnouncement().getId());
        if (report1.getEmail() != null && report2.getEmail() != null)
            assertThat(report1.getEmail()).isEqualTo(report2.getEmail());
        if (report1.getReporter() != null && report2.getReporter() != null)
            assertThat(report1.getReporter().getId()).isEqualTo(report2.getReporter().getId());
        assertThat(report1.getContent()).isEqualTo(report2.getContent());
        assertThat(report1.getStatus()).isEqualTo(report2.getStatus());
    }


    @Before
    public void initTest() {
        existingReport = new Report(ID, EMAIL, TYPE, CONTENT, STATUS, REPORTER, DEAFULT_ANNOUNCEMENT);
        newReport =  new Report(null, NEW_EMAIL, NEW_TYPE, NEW_CONTENT, NEW_STATUS, NEW_REPORTER, DEAFULT_ANNOUNCEMENT);
        updatedReport = new Report(null, UPDATED_EMAIL, UPDATED_TYPE, UPDATED_CONTENT, UPDATED_STATUS, UPDATED_REPORTER, DEAFULT_ANNOUNCEMENT);
    }

    @Test
    public void testFindAllPageable() {
        PageRequest pageRequest = new PageRequest(0, PAGE_SIZE);
        Page<Report> reports = reportRepository.findAll(pageRequest);
        assertThat(reports).hasSize(PAGE_SIZE);
    }

    @Test
    public void testFindAll() {
        List<Report> reports = reportRepository.findAll();
        assertThat(reports).hasSize(DB_COUNT_REPORTS);
    }

    @Test
    public void testFindOne(){
        Report report = reportService.findOne(ID);
        assertThat(report).isNotNull();

        compareReports(report, existingReport);
    }

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

    @Test
    @Transactional
    public void testUpdate() {
        Report dbReport = reportService.findOne(ID);

        dbReport.setContent(UPDATED_CONTENT);
        dbReport.setReporter(UPDATED_REPORTER);
        dbReport.setStatus(UPDATED_STATUS);
        dbReport.setType(UPDATED_TYPE);

        Report updatedDbReport = reportService.save(dbReport);
        assertThat(updatedDbReport).isNotNull();

        compareReports(updatedDbReport, updatedReport);
    }

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

    /*
	 * Negative tests
	 */

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullEmail() {
        newReport.setEmail(null);
        reportService.save(newReport);
        // rollback previous email
        newReport.setEmail(NEW_EMAIL);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullType() {
        newReport.setType(null);
        reportService.save(newReport);
        // rollback previous type
        newReport.setType(NEW_TYPE);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullContent() {
        newReport.setContent(null);
        reportService.save(newReport);
        // rollback previous content
        newReport.setContent(NEW_CONTENT);
    }

    @Test(expected = ConstraintViolationException.class)
    @Transactional
    public void testAddNullStatus() {
        newReport.setStatus(null);
        reportService.save(newReport);
        // rollback previous status
        newReport.setStatus(NEW_STATUS);
    }
}
