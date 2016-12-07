package rs.acs.uns.sw.sct.announcements;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    /**
     * Get all announcements where 'delete' status is 'true' or 'false'.
     *
     * @param deleted status we've search for
     * @param pageable the pagination information
     * @return list of announcements
     */
    Page<Announcement> findAllByDeleted(Boolean deleted, Pageable pageable);

    /**
     * Get all announcements created by one company.
     *
     * @param companyId the id of the company
     * @param pageable the pagination information
     * @return list of announcements
     */
    Page<Announcement> findByAuthor_Company_Id(Long companyId, Pageable pageable);

    /**
     * Get all announcements created by one company.
     *
     * @param companyId the id of the company
     * @return list of announcements
     */
    List<Announcement> findFirst3ByAuthor_Company_IdOrderByPriceAsc(Long companyId);

}