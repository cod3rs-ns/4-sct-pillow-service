package rs.acs.uns.sw.sct.users;

import org.springframework.security.core.authority.AuthorityUtils;

/**
 * A user factory which creates instances of SecurityUser object.
 */
public class UserFactory {

    private UserFactory() {
    }

    /**
     * Method that creates new SecurityUser.
     *
     * @param user user to be converted
     * @return Security User
     */
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
