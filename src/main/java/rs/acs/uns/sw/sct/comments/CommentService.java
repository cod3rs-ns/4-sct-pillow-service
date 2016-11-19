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
        Comment result = commentRepository.save(comment);
        return result;
    }

    /**
     * Get all the comments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Comment> findAll(Pageable pageable) {
        Page<Comment> result = commentRepository.findAll(pageable);
        return result;
    }

    /**
     * Get one comment by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Comment findOne(Long id) {
        Comment comment = commentRepository.findOne(id);
        return comment;
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
