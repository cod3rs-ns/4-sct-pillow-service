package rs.acs.uns.sw.sct.constants;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public interface RealEstateConstants {

    int PAGE_SIZE = 2;
    int DB_COUNT_REAL_ESTATES = 4;

    Long REMOVE_ID = 3L;

    Long ID = 1L;
    String NAME = "RealEstate1";
    String TYPE = "flat";
    Double AREA = 220D;
    String HEATING_TYPE = "coal";

    String NEW_NAME = "RealEstate8";
    String NEW_TYPE = "restaurant";
    Double NEW_AREA = 300D;
    String NEW_HEATING_TYPE = "flat";

    String UPDATED_NAME = "RealEstate5";
    String UPDATED_TYPE = "house";
    Double UPDATED_AREA = 500D;
    String UPDATED_HEATING_TYPE = "central_heating";

    Boolean DEFAULT_DELETED = false;

    Pageable PAGEABLE = new PageRequest(0, 10);

    Long DB_COUNT_REAL_ESTATES_DELETED_TRUE = 1L;
    Long DB_COUNT_REAL_ESTATES_DELETED_FALSE = 3L;
    Long DB_COUNT_REAL_ESTATES_SIMILAR_NON_DELETED = 1L;

    String SIMILAR_AREA      = "230";
    String SIMILAR_COUNTRY   = "Srbija";
    String SIMILAR_CITY      = "Novi Sad";
    String SIMILAR_REGION    = "Grbavica";
    String SIMILAR_STREET    = "Trg Dositeja Obradovica";
    String SIMILAR_STREET_NO = "15";
}
