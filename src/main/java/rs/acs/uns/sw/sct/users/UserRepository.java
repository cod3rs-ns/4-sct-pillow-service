package rs.acs.uns.sw.sct.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the Report entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Get one user by email.
     *
     * @param email the id of the user
     * @return user
     */
    User findOneByEmail(String email);

    /**
     * Get all users from one company.
     *
     * @param companyId the id of the user
     * @param pageable the pagination information
     * @return list of users
     */
    Page<User> findByCompany_Id(Long companyId, Pageable pageable);
}
