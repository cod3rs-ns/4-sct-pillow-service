package rs.acs.uns.sw.sct.realestates;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing RealEstate.
 */
@Service
@Transactional
public class RealEstateService {

    @Autowired
    private RealEstateRepository realEstateRepository;

    /**
     * Save a realEstate.
     *
     * @param realEstate the entity to save
     * @return the persisted entity
     */
    public RealEstate save(RealEstate realEstate) {
        return realEstateRepository.save(realEstate);
    }

    /**
     * Get all the realEstates.
     *
     * @param status deleted or not deleted
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RealEstate> findAllByStatus(Boolean status, Pageable pageable) {
        return realEstateRepository.findAllByDeleted(status, pageable);
    }

    /**
     * Get all the realEstates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RealEstate> findAll(Pageable pageable) {
        return realEstateRepository.findAll(pageable);
    }

    /**
     * Get one realEstate by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public RealEstate findOne(Long id) {
        return realEstateRepository.findOne(id);
    }

    /**
     * Delete the  realEstate by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        realEstateRepository.delete(id);
    }
}
