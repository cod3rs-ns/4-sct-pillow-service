package rs.acs.uns.sw.awt_test.marks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Mark.
 */
@Service
@Transactional
public class MarkService {

    private final Logger log = LoggerFactory.getLogger(MarkService.class);

    @Autowired
    private MarkRepository markRepository;

    /**
     * Save a mark.
     *
     * @param mark the entity to save
     * @return the persisted entity
     */
    public Mark save(Mark mark) {
        log.debug("Request to save Mark : {}", mark);
        Mark result = markRepository.save(mark);
        return result;
    }

    /**
     * Get all the marks.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Mark> findAll(Pageable pageable) {
        log.debug("Request to get all Marks");
        Page<Mark> result = markRepository.findAll(pageable);
        return result;
    }

    /**
     * Get one mark by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Mark findOne(Long id) {
        log.debug("Request to get Mark : {}", id);
        Mark mark = markRepository.findOne(id);
        return mark;
    }

    /**
     * Delete the  mark by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Mark : {}", id);
        markRepository.delete(id);
    }
}