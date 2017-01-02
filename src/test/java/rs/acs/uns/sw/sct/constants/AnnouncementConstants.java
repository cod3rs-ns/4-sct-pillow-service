package rs.acs.uns.sw.sct.constants;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import rs.acs.uns.sw.sct.realestates.RealEstate;
import rs.acs.uns.sw.sct.users.User;
import rs.acs.uns.sw.sct.util.DateUtil;

import java.io.File;
import java.sql.Timestamp;

public interface AnnouncementConstants {

    int DB_COUNT_ANNOUNCEMENT = 4;
    int PAGE_SIZE = 2;

    Long ID = 1L;
    Double PRICE = 50D;
    String TELEPHONE_NO = "0654887612";
    String TYPE = "flat";
    RealEstate REAL_ESTATE = new RealEstate().id(1L);
    User AUTHOR = new User().id(1L);
    Timestamp DATE_ANNOUNCED = DateUtil.date("01-01-1994");
    Timestamp DATE_MODIFIED = DateUtil.date("03-01-1994");
    Timestamp EXPIRATION_DATE = DateUtil.date("20-01-1994");

    Double NEW_PRICE = 40D;
    String NEW_PHONE_NUMBER = "0654887612";
    String NEW_TYPE = "house";
    User NEW_AUTHOR = new User().id(2L);
    Timestamp NEW_DATE_ANNOUNCED = DateUtil.date("01-01-1995");
    Timestamp NEW_DATE_MODIFIED = DateUtil.date("03-01-1995");
    Timestamp NEW_EXPIRATION_DATE = DateUtil.date("20-01-1995");

    Long UPDATED_ID = 1L;
    Double UPDATED_PRICE = 15D;
    String UPDATED_PHONE_NUMBER = "06548812";
    String UPDATED_TYPE = "restaurant";
    RealEstate UPDATED_REAL_ESTATE = new RealEstate().id(1L);
    User UPDATED_AUTHOR = new User().id(3L);
    Timestamp UPDATED_DATE_ANNOUNCED = DateUtil.date("01-01-1996");
    Timestamp UPDATED_DATE_MODIFIED = DateUtil.date("03-01-1996");
    Timestamp UPDATED_EXPIRATION_DATE = DateUtil.date("20-01-1996");

    String DEFAULT_VERIFIED = "not-verified";
    Boolean DEFAULT_DELETED = false;

    // Location parameter
    String CITY = "Novi Sad";
    String CITY_REGION = "Grbavica";
    String COUNTRY = "Serbia";
    String STREET = "Narodnog Fronta";
    String STREET_NUMBER = "15";

    // Real estate
    String RE_EQUIPMENT = "everything";
    String RE_NAME = "real name";
    String RE_TYPE = "sell";
    Double RE_AREA = 120D;
    String RE_HEATING_TYPE = "central";
    Boolean RE_DELETED = false;

    // Companies
    Long COMPANY_ID = 1L;
    Integer COUNT_OF_COMPANY_ANN = 2;

    // 1st page with 10 elements
    Pageable PAGEABLE = new PageRequest(0, 10, Sort.Direction.ASC, "dateAnnounced", "price");

    // number of top elements
    Integer TOP = 3;

    // Number of "deleted = true" database records
    Long DB_COUNT_ANNOUNCEMENT_DELETED_TRUE = 1L;
    // Number of "deleted = false" database records
    Long DB_COUNT_ANNOUNCEMENT_DELETED_FALSE = 3L;
    // Number of "deleted = false" database records


    // file to be upload
    String FILE_TO_BE_UPLOAD = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "test_upload.jpg";

    String NEW_BASE_DIR = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator + "uploads";
}
