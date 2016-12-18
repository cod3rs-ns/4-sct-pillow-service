package rs.acs.uns.sw.sct.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the Report entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {

    /**
     * Get one user by email.
     *
     * @param email the email of the user
     * @return user
     */
    User findOneByEmail(String email);

    /**
     * Get one user by username.
     *
     * @param username the username of the user
     * @return user
     */
    User findOneByUsername(String username);


    /**
     * Get all users by 'deleted' status
     *
     * @param deleted  status of user - deleted or not
     * @param pageable the pagination information
     * @return list of users
     */
    Page<User> findAllByDeleted(Boolean deleted, Pageable pageable);


    /**
     * Get all users from one company.
     *
     * @param companyId the id of the user
     * @param pageable  the pagination information
     * @return list of users
     */
    Page<User> findByCompany_Id(Long companyId, Pageable pageable); // NOSONAR - invalid warning


    /**
     * Get all users from one company.
     *
     * @param companyId       the id of the user
     * @param companyVerified the status of user request for membership
     * @param pageable        the pagination information
     * @return list of users
     */
    Page<User> findByCompany_IdAndCompanyVerified(Long companyId, String companyVerified, Pageable pageable); // NOSONAR - invalid warning
}
