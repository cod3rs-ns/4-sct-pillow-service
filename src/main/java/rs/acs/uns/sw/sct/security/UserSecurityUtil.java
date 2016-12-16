package rs.acs.uns.sw.sct.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.users.UserService;
import rs.acs.uns.sw.sct.util.AuthorityRoles;

import java.util.Collection;

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

    public Collection<? extends GrantedAuthority> getLoggedUserAuthorities() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities();
    }

    public String getLoggedUserUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public boolean checkAuthType(String authType) {
        return getLoggedUserAuthorities().contains(new SimpleGrantedAuthority(authType));
    }

    public boolean checkPermission(String currentUsername, String userRole){
        if(checkAuthType(userRole) && !currentUsername.equals(getLoggedUserUsername())) {
            return false;
        }
        return true;
    }

}
