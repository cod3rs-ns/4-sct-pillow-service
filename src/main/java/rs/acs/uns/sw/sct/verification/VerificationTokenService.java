package rs.acs.uns.sw.sct.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing RealEstate.
 */
@Service
@Transactional
public class VerificationTokenService {
    @Autowired
    VerificationTokenRepository tokenRepository;

    /**
     * Save a verification token.
     *
     * @param verificationToken the entity to save
     * @return the persisted entity
     */
    public VerificationToken save(VerificationToken verificationToken) {
        return tokenRepository.save(verificationToken);
    }

    /**
     * Get one verification token by token value.
     *
     * @param tokenValue the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public VerificationToken findOneByToken(String tokenValue) {
        return tokenRepository.findByToken(tokenValue);
    }

    /**
     * Get one verification token by user id.
     *
     * @param userId the id of the user
     * @return the entity
     */
    @Transactional(readOnly = true)
    public VerificationToken findByUserId(Long userId) {
        return tokenRepository.findByUser_Id(userId);
    }

    /**
     * Delete the verification token by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        tokenRepository.delete(id);
    }
}
