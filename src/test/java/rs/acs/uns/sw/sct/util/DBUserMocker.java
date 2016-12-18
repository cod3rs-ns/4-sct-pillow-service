package rs.acs.uns.sw.sct.util;

import rs.acs.uns.sw.sct.users.User;

/**
 * Creates instances of users that exists in database for testing;
 */
public final class DBUserMocker {
    public static final User VERIFIER = new User().id(1L).username("isco").email("isco@gmail.com").type("verifier");
    public static final User ADMIN = new User().id(11L).username("admin").email("admin@admin.com").type("admin");
    public static final User ADVERTISER = new User().id(5L).username("damian").email("lillard@gmail.com").type("advertiser");
    public static final String VERIFIER_USERNAME = "isco";
    public static final String ADVERTISER_USERNAME = "damian";
    public static final String ADMIN_USERNAME = "admin";

    private DBUserMocker() {
    }
}
