package rs.acs.uns.sw.sct.reports;

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

    @Autowired
    private ReportRepository reportRepository;

    /**
     * Save a report.
     *
     * @param report the entity to save
     * @return the persisted entity
     */
    public Report save(Report report) {
        return reportRepository.save(report);
    }

    /**
     * Get all the reports.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Report> findAll(Pageable pageable) {
        return reportRepository.findAll(pageable);
    }

    /**
     * Get one report by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Report findOne(Long id) {
        return reportRepository.findOne(id);
    }

    /**
     * Get all reports by status.
     *
     * @param status the id of the entity
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Report> findByStatus(String status, Pageable pageable) {
        return reportRepository.findByStatus(status, pageable);
    }

    /**
     * Get all reports by author email.
     *
     * @param email the id of the entity
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Report> findByAuthorEmail(String email, Pageable pageable) {
        return reportRepository.findByEmail(email, pageable);
    }


    /**
     * Delete the  report by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        reportRepository.delete(id);
    }


    /**
     * Get all reports by author email.
     *
     * @param email the reporter email
     * @param status status of report
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    Report findByReaporterEmailAndStatus(String email, String status) {
        return reportRepository.findByEmailAndStatus(email, status);
    }
}
