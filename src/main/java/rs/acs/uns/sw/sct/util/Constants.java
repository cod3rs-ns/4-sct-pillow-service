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
     * Constants representing Verified statuses.
     */
    public static final class  VerifiedStatuses{
        public static final String VERIFIED = "verified";
        public static final String NOT_VERIFIED = "not-verified";

        private VerifiedStatuses() {
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


    public static final class MailParameters {
        public static final long   TOKEN_EXPIRE_TIME = 1800000;
        public static final String TOKEN_CONFIRM_LINK = "http://localhost:8091/api/registration-confirm?token=";
        public static final String HOST_NAME = "smtp.gmail.com";
        public static final int    HOST_PORT = 587;
        public static final String AUTH_USER = "bacovakuhinja@gmail.com";
        public static final String AUTH_PASS = "jedanjebaco";
    }
}