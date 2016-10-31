package rs.acs.uns.sw.awt_test.real_estates;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the RealEstate entity.
 */
public interface RealEstateRepository extends JpaRepository<RealEstate, Long> {

}
