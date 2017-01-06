package rs.acs.uns.sw.sct.announcements;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.search.AnnouncementSearchWrapper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
     * @param status   deleted or not deleted
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAllByStatus(Boolean status, Pageable pageable) {
        return announcementRepository.findAllByDeleted(status, pageable);
    }

    /**
     * Get all the announcements.
     *
     * @param x1    Top right corner longitude
     * @param y1    Top right corner latitude
     * @param x2    Bottom left corner longitude
     * @param y2    Bottom left corner latitude
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAllInArea(Double x1, Double y1, Double x2, Double y2, Pageable pageable) {
        final List<Announcement> announcementsInArea = announcementRepository.findAllByDeleted(false, pageable)
                .getContent().stream()
                .filter(announcement -> announcement.getRealEstate().getLocation().isInArea(x1, y1, x2, y2))
                .collect(Collectors.toList());

        return new PageImpl<>(announcementsInArea, pageable, announcementsInArea.size());
    }


    /**
     * Get all the announcements by company id.
     *
     * @param pageable  the pagination information
     * @param companyId id of one company
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAllByCompany(Long companyId, Pageable pageable) {
        return announcementRepository.findByAuthor_Company_IdAndExpirationDateAfter(companyId, new Date(), pageable);
    }

    /**
     * Get all the announcements by Author ID.
     *
     * @param pageable the pagination information
     * @param authorId id of one announcements author
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAllByAuthor(Long authorId, Pageable pageable) {
        return announcementRepository.findByAuthor_Id(authorId, pageable);
    }

    /**
     * Get all the announcements by Author ID and deletion status.
     *
     * @param pageable the pagination information
     * @param authorId id of one announcements author
     * @param deleted  announcement's status - deleted
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findAllByAuthorAndStatus(Long authorId, Boolean deleted, Pageable pageable) {
        return announcementRepository.findByAuthor_Id_AndDeleted(authorId, deleted, pageable);
    }


    /**
     * Get top the announcements by company id.
     *
     * @param companyId id of one company
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Announcement> findTopByCompany(Long companyId) {
        return announcementRepository.findFirst3ByAuthor_Company_IdAndExpirationDateAfterOrderByPriceAsc(companyId, new Date());
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
     * @param pageable      the pagination information
     * @return list of founded Announcements
     */
    @Transactional(readOnly = true)
    public Page<Announcement> findBySearchTerm(AnnouncementSearchWrapper searchWrapper, Pageable pageable) {
        Predicate searchPredicate = search(searchWrapper);
        Page<Announcement> searchResults = announcementRepository.findAll(searchPredicate, pageable);
        return searchResults;
    }
}
