package rs.acs.uns.sw.sct.comments;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Comment entity.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Get all comments that belong to one announcement.
     *
     * @param announcementId the id of the announcement
     * @param pageable       the pagination information
     * @return list of comments
     */
    Page<Comment> findByAnnouncement_Id(Long announcementId, Pageable pageable);

}
