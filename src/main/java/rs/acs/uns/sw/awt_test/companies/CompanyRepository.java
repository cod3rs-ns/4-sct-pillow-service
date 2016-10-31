package rs.acs.uns.sw.awt_test.companies;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Company entity.
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
