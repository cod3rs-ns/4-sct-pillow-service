package rs.acs.uns.sw.sct.announcements;

import com.querydsl.core.types.Predicate;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.search.AnnouncementSearchWrapper;

import java.util.List;

import static rs.acs.uns.sw.sct.search.AnnouncementPredicates.search;

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
     * Get all the announcements.
     *
     * @param status deleted or not deleted
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAllByStatus(Boolean status, Pageable pageable) {
        return announcementRepository.findAllByDeleted(status, pageable);
    }

    /**
     * Get all the announcements by company id.
     *
     * @param pageable the pagination information
     * @param companyId id of one company
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAllByCompany(Long companyId, Pageable pageable) {
        return announcementRepository.findByAuthor_Company_Id(companyId, pageable);
    }

    /**
     * Get top the announcements by company id.
     *
     * @param companyId id of one company
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Announcement> findTopByCompany(Long companyId) {
        return announcementRepository.findFirst3ByAuthor_Company_IdOrderByPriceAsc(companyId);
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

    /**
     * Find all announcement that satisfied criteria defined by query params.
     *
     * @param searchWrapper wrapper of all query params
     * @param pageable the pagination information
     */
    @Transactional(readOnly = true)
    public List<Announcement> findBySearchTerm(AnnouncementSearchWrapper searchWrapper, Pageable pageable) {
        Predicate searchPredicate = search(searchWrapper);
        Iterable<Announcement> searchResults = announcementRepository.findAll(searchPredicate, pageable);
        return IteratorUtils.toList(searchResults.iterator());
    }
}
