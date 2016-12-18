package rs.acs.uns.sw.sct.marks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Mark entity.
 */
public interface MarkRepository extends JpaRepository<Mark, Long> {

        /**
         * Get all marks that belong to one announcement.
         *
         * @param announcementId the id of the announcement
         * @param pageable the pagination information
         * @return list of marks
         */
        Page<Mark> findByAnnouncement_Id(Long announcementId, Pageable pageable); // NOSONAR - invalid warning
}
