package rs.acs.uns.sw.sct.constants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface UserConstants {
    int DB_COUNT = 17;

    Long USER_ID = 7L;
    String USER_FIRST_NAME = "Andrew";
    String USER_LAST_NAME = "Wiggins";
    String USER_EMAIL = "wiggins@gmail.com";
    String USER_PASSWORD = "$2a$10$iZ.kdcySt1n2BnLIkksLdOSe22jVI4kUYa1OBpZ64QboxruXetmFO";
    String USER_TELEPHONE = "065161665";
    String USER_TYPE = "verifier";
    String USER_USERNAME = "wiggins";

    String NEW_USER_FIRST_NAME = "Michael";
    String NEW_USER_LAST_NAME = "Jordan";
    String NEW_USER_EMAIL = "jordan@gmail.com";
    String NEW_USER_PASSWORD = "michael";
    String NEW_USER_TELEPHONE = "064161665";
    String NEW_USER_TYPE = "oglašavač";
    String NEW_USER_USERNAME = "jordan";

    String UPDATED_USER_FIRST_NAME = "Steve";
    String UPDATED_USER_LAST_NAME = "Nash";
    String UPDATED_USER_EMAIL = "nash@gmail.com";
    String UPDATED_USER_PASSWORD = "nash";
    String UPDATED_USER_TELEPHONE = "064163665";
    String UPDATED_USER_TYPE = "oglašavač";
    String UPDATED_USER_USERNAME = "steve";

    String ADVERTISER_USERNAME = "lbj";
    String ADMIN_USERNAME = "admin";
    String ADVERTISER_COMPANY_USERNAME = "damian";
    String VERIFIER_ISCO_USERNAME = "isco";

    // 1st page with 10 elements
    Pageable PAGEABLE = new PageRequest(0, 10);

    int USERS_IN_COMPANY = 3;

    // Number of "deleted = true" database records
    Long DB_COUNT_USERS_DELETED_TRUE  = 2L;
    // Number of "deleted = false" database records
    Long DB_COUNT_USERS_DELETED_FALSE = 15L;

    // Number of "companyStatus = 'status'" database records for Company (ID = 3)
    Long USER_COMPANY_ID = 3L;
    Long USER_COMPANY_3_ACCEPTED = 4L;
    Long USER_COMPANY_3_PENDING  = 1L;

    // Existing username in database
    String EXISTING_USERNAME = "isco";
    String EXISTING_EMAIL = "isco@gmail.com";

}