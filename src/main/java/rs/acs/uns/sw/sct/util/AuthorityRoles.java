package rs.acs.uns.sw.sct.util;

/**
 * Represents all possible roles of users
 * Constants used in @PreAuthorize API methods.
 */
public final class AuthorityRoles {

    public static final String ADVERTISER = "advertiser";
    public static final String VERIFIER = "verifier";
    public static final String ADMIN = "admin";

    private AuthorityRoles() {
        super();
    }
}
