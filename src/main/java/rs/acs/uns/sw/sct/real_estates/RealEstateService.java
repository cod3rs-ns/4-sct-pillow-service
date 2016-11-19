package rs.acs.uns.sw.sct.real_estates;

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
        RealEstate result = realEstateRepository.save(realEstate);
        return result;
    }

    /**
     * Get all the realEstates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<RealEstate> findAll(Pageable pageable) {
        Page<RealEstate> result = realEstateRepository.findAll(pageable);
        return result;
    }

    /**
     * Get one realEstate by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public RealEstate findOne(Long id) {
        RealEstate realEstate = realEstateRepository.findOne(id);
        return realEstate;
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
