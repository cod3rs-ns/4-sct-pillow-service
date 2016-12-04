package rs.acs.uns.sw.sct.util;

/**
 * App wide used constants.
 */
public final class Constants {

    private Constants() {

    }

    /**
     * Constants representing USER roles.
     */
    public static final class Roles {
        public static final String ADVERTISER = "advertiser";
        public static final String VERIFIER = "verifier";
        public static final String ADMIN = "admin";

        private Roles() {
        }
    }

    /**
     * Constants representing COMPANY statuses.
     */
    public static final class CompanyStatus {
        public static final String ACCEPTED = "accepted";
        public static final String REJECTED = "rejected";
        public static final String PENDING = "pending";

        private CompanyStatus() {
        }
    }

    /**
     * Constants representing file paths for image upload.
     */
    public static final class FilePaths {
        // need not to be final because of reflection
        public static String BASE = new String("C:\\uploads");
        public static final String ANNOUNCEMENTS = "announcements";
        public static final String COMPANIES = "companies";
        public static final String USERS = "users";

        public FilePaths() {
        }
    }
}