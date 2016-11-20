package rs.acs.uns.sw.sct.announcements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Announcement.
 */
@Service
@Transactional
public class AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    /**
     * Save a announcement.
     *
     * @param announcement the entity to save
     * @return the persisted entity
     */
    public Announcement save(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    /**
     * Get all the announcements.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAll(Pageable pageable) {
        return announcementRepository.findAll(pageable);
    }

    /**
     * Get one announcement by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Announcement findOne(Long id) {
        return announcementRepository.findOne(id);
    }

    /**
     * Delete the  announcement by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        announcementRepository.delete(id);
    }

}
