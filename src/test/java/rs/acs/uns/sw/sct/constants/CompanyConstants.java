package rs.acs.uns.sw.sct.constants;


import rs.acs.uns.sw.sct.users.User;

import java.util.Set;

public interface CompanyConstants {

    int PAGE_SIZE = 2;
    int DB_COUNT_COMPANIES = 3;

    Long ID = 1L;
    Long REMOVE_ID = 2L;

    String NAME = "company";
    String ADDRESS = "Trg Dositeja Obradovića";
    String TELEPHONE_NO = "5461963";

    String NEW_NAME = "company_new";
    String NEW_ADDRESS = "Trg Dositeja Obradovića 15";
    String NEW_TELEPHONE_NO = "5461963456";

    String UPDATED_NAME = "company_updated";
    String UPDATED_ADDRESS = "Trg Kralja Petra";
    String UPDATED_TELEPHONE_NO = "1239555";

    Set<User> DEFAULT_USERS = null;
}
