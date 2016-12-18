package rs.acs.uns.sw.sct.realestates;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the RealEstate entity.
 */
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {
    /**
     * Get all real estates where 'delete' status is 'true' or 'false'.
     *
     * @param deleted  status we are searching for
     * @param pageable the pagination information
     * @return list of real estates
     */
    Page<RealEstate> findAllByDeleted(Boolean deleted, Pageable pageable);
}
