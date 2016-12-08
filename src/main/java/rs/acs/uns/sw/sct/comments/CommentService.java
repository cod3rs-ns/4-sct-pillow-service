package rs.acs.uns.sw.sct.comments;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing Comment.
 */
@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    /**
     * Save a comment.
     *
     * @param comment the entity to save
     * @return the persisted entity
     */
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    /**
     * Get all the comments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Comment> findAll(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    /**
     * Get all the comments by announcement id.
     *
     * @param pageable the pagination information
     * @param announcementId id of one announcement
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Comment> findAllByAnnouncement(Long announcementId, Pageable pageable) {
        return commentRepository.findByAnnouncement_Id(announcementId, pageable);
    }

    /**
     * Get one comment by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Comment findOne(Long id) {
        return commentRepository.findOne(id);
    }

    /**
     * Delete the  comment by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        commentRepository.delete(id);
    }
}
