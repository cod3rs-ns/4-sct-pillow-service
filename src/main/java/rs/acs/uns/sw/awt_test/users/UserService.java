package rs.acs.uns.sw.awt_test.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    public User findOne() {

        User user = new User();
        user.setFirstName("Sergio");
        user.setLastName("Ramos");

        // User user1 = userDAO.findOne(1);

        return user;
    }
}
