package rs.acs.uns.sw.sct.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.acs.uns.sw.sct.real_estates.RealEstateService;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(RealEstateService.class);

    @Autowired
    UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }


    /**
     * Save a newUser.
     *
     * @param newUser the entity to save
     * @return the persisted entity
     */
    public User save(User newUser) {
        log.debug("Request to save User : {}", newUser);

        if (newUser.getPassword() != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(newUser.getPassword());
            newUser.setPassword(hashedPassword);
        }

        User result = userRepository.save(newUser);
        return result;
    }


    /**
     * Get one realEstate by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public User findOne(Long id) {
        log.debug("Request to get RealEstate : {}", id);
        User user = userRepository.findOne(id);
        return user;
    }
}
