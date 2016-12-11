package rs.acs.uns.sw.sct.constants;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface UserConstants {
    int DB_COUNT = 7;

    Long USER_ID = 7L;
    String USER_FIRST_NAME = "Andrew";
    String USER_LAST_NAME = "Wiggins";
    String USER_EMAIL = "wiggins@gmail.com";
    String USER_PASSWORD = "$10$wDeKOR2hyQaoEQSw827CHubLgjV5F2fOeKm.tIu1QDszHHUdr.Rjq";
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

    String ADVERTISER_USERNAME = "james";

    // 1st page with 10 elements
    Pageable PAGEABLE = new PageRequest(0, 10);

    int USERS_IN_COMPANY = 3;
}