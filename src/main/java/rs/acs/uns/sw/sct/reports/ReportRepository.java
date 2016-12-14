package rs.acs.uns.sw.sct.reports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Report entity.
 */
public interface ReportRepository extends JpaRepository<Report, Long> {
    /**
     * Get all reports with one status.
     *
     * @param status   the status of one report
     * @param pageable the pagination information
     * @return list of announcements
     */
    Page<Report> findByStatus(String status, Pageable pageable);

    /**
     * Get all reports by author email.
     *
     * @param email    the username of one reporter
     * @param pageable the pagination information
     * @return list of announcements
     */
    Page<Report> findByEmail(String email, Pageable pageable);

    /**
     * Get all reports by status and reporter email.
     *
     * @param email  email of reporter
     * @param status status of report
     * @return list of announcements
     */
    Report findByEmailAndStatusAndAnnouncement_Id(String email, String status, Long id);
}
