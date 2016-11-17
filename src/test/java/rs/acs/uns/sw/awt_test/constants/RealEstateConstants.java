package rs.acs.uns.sw.awt_test.constants;


import rs.acs.uns.sw.awt_test.announcements.Announcement;

import java.util.Set;

public interface RealEstateConstants {

    int PAGE_SIZE = 2;
    int DB_COUNT_REAL_ESTATES = 2;

    Long REMOVE_ID = 2L;

    Long ID = 1L;
    String NAME = "RealEstate1";
    String TYPE = "flat";
    Double AREA = 120D;
    String HEATING_TYPE = "coal";

    String NEW_NAME = "RealEstate8";
    String NEW_TYPE = "restaurant";
    Double NEW_AREA = 300D;
    String NEW_HEATING_TYPE = "flat";

    String UPDATED_NAME = "RealEstate5";
    String UPDATED_TYPE = "house";
    Double UPDATED_AREA = 500D;
    String UPDATED_HEATING_TYPE = "central_heating";

    Set<Announcement> DEFAULT_ANNOUNCEMENTS = null;
    Boolean DEFAULT_DELETED = false;

}
