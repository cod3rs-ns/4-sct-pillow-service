package rs.acs.uns.sw.awt_test.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Report.
 */
@Service
@Transactional
public class ReportService {

    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private ReportRepository reportRepository;

    /**
     * Save a report.
     *
     * @param report the entity to save
     * @return the persisted entity
     */
    public Report save(Report report) {
        log.debug("Request to save Report : {}", report);
        Report result = reportRepository.save(report);
        return result;
    }

    /**
     * Get all the reports.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Report> findAll(Pageable pageable) {
        log.debug("Request to get all Reports");
        Page<Report> result = reportRepository.findAll(pageable);
        return result;
    }

    /**
     * Get one report by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Report findOne(Long id) {
        log.debug("Request to get Report : {}", id);
        Report report = reportRepository.findOne(id);
        return report;
    }

    /**
     * Delete the  report by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Report : {}", id);
        reportRepository.delete(id);
    }
}