package rs.acs.uns.sw.sct.users;

import org.springframework.security.core.authority.AuthorityUtils;

public class UserFactory {

    public static SecurityUser create(User user) {
        return new SecurityUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getType(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(user.getType())
        );
    }
}
