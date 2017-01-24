package rs.acs.uns.sw.sct.verification;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the VerificationToken entity.
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Get one token by value.
     *
     * @param tokenValue value of one token
     * @return one token
     */
    VerificationToken findByToken(String tokenValue);

    /**
     * Get one token by user id.
     *
     * @param userId status we've search for
     * @return one token
     */
    VerificationToken findByUser_Id(Long userId);
}