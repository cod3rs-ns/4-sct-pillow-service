package rs.acs.uns.sw.awt_test.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }
}
