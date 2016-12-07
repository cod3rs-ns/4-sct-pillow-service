package rs.acs.uns.sw.sct.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Report.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    /**
     * Get one user by email.
     *
     * @param email the id of the user
     * @return user
     */
    public User getUserByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }


    /**
     * Get all users by company id.
     *
     * @param pageable the pagination information
     * @param companyId id of one company
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<User> findAllByCompany(Long companyId, Pageable pageable) {
        return userRepository.findByCompany_Id(companyId, pageable);
    }

    /**
     * Get all the realEstates.
     *
     * @param status deleted or not deleted
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<User> findAllByStatus(Boolean status, Pageable pageable) {
        return userRepository.findAllByDeleted(status, pageable);
    }

    /**
     * Save new user.
     *
     * @param newUser the user to save
     * @return the persisted user
     */
    public User save(User newUser) {

        if (newUser.getPassword() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newUser.getPassword());
            newUser.setPassword(hashedPassword);
        }

        return userRepository.save(newUser);
    }

    /**
     * Get one User by id.
     *
     * @param id the id of the user
     * @return user
     */
    @Transactional(readOnly = true)
    public User findOne(Long id) {
        return userRepository.findOne(id);
    }
}
