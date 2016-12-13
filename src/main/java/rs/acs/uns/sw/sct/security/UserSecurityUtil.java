package rs.acs.uns.sw.sct.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;

@Component
public class UserSecurityUtil {

    @Autowired
    UserService userService;

    public User getLoggedUser() {

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        return userService.getUserByUsername(authentication.getName());
    }

}
