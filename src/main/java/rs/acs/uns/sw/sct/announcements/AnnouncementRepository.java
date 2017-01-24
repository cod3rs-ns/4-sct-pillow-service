package rs.acs.uns.sw.sct.announcements;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.Date;
import java.util.List;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, Long>, QueryDslPredicateExecutor<Announcement> {

    /**
     * Get all announcements where 'delete' status is 'true' or 'false'.
     *
     * @param deleted  status we've search for
     * @param pageable the pagination information
     * @return list of announcements
     */
    Page<Announcement> findAllByDeleted(Boolean deleted, Pageable pageable);

    /**
     * Get all announcements created by one company.
     *
     * @param companyId the id of the company
     * @param pageable  the pagination information
     * @return list of announcements
     */
    Page<Announcement> findByAuthor_Company_IdAndExpirationDateAfter(Long companyId, Date date, Pageable pageable);

    /**
     * Get all announcements created by specified Author ID.
     *
     * @param authorId the id of the announcements author
     * @param pageable the pagination information
     * @return list of announcements
     */
    Page<Announcement> findByAuthor_Id(Long authorId, Pageable pageable);

    /**
     * Get all announcements created by specified Author ID and deletion status.
     *
     * @param authorId the id of the announcement's author
     * @param deleted  announcement's status - deleted
     * @param pageable the pagination information
     * @return list of announcements
     */
    Page<Announcement> findByAuthor_Id_AndDeleted(Long authorId, Boolean deleted, Pageable pageable);

    /**
     * Get first 3 announcements by Company order by price ascending.
     *
     * @param companyId the id of the company
     * @return list of announcements
     */
    List<Announcement> findFirst3ByAuthor_Company_IdAndExpirationDateAfterOrderByPriceAsc(Long companyId, Date date);

}