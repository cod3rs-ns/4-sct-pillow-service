package rs.acs.uns.sw.e2e.pages;

import org.openqa.selenium.By;

/**
 * Constants from client's Adding Annoucement page
 */
public final class UpdatingAnnouncementPage {

    /**
     * Private constructor to enable instantiating constant's class
     */
    private UpdatingAnnouncementPage() {}

    // Constant element ids
    public static final String ADVERTISER_ANNOUNCEMENT_URL = "http://localhost:8080/#/announcement/4";
    public static final String NOT_ADVERTISER_ANNOUNCEMENT_URL = "http://localhost:8080/#/announcement/1";

    public static final String NOT_ADVERTISER_UPDATE_ANNOUNCEMENT_URL = "http://localhost:8080/#/update/announcement/1";
    public static final String PAGE_NOT_FOUND_URL = "http://localhost:8080/#/page-not-found";

    public static final By UPDATE_BUTTON            = By.id("update-announcement");

    public static final By ANNOUNCEMENT_NAME        = By.id("ann-name");

    public static final By IMAGES_DROP_ZONE         = By.id("drop-images");
    public static final By IMAGES_UPLOAD_PROGRESS   = By.id("progress-bar");
    public static final By DELETE_UPLOADED_IMAGE    = By.name("delete-image-button");

    public static final By REAL_ESTATE_AREA      = By.id("re-area");
    public static final By REAL_ESTATE_CONUTRY   = By.id("re-country");
    public static final By REAL_ESTATE_CITY      = By.id("re-city");
    public static final By REAL_ESTATE_REGION    = By.id("re-region");
    public static final By REAL_ESTATE_STREET    = By.id("re-street");
    public static final By REAL_ESTATE_STREET_NO = By.id("re-street-no");

    public static final String WRONG_COUNTRY_VALUE = "الأولى ";
    public static final String WRONG_CITY_VALUE    = "فصل";
    public static final String WRONG_STREET_VALUE  = "لعملة بلديهما للمجه";


    public static final By ANN_NAME_HTML_VALUE      = By.id("announcement-name-value");

    // Similar real estate
    public static final By SIMILAR_REAL_ESTATE  = By.name("similar-real-estate");

    public static final By CONTINUE_BUTTON      = By.id("continue-btn");
    public static final By SIMILAR_RE_CONTINUE  = By.id("similar-realestate-continue-btn");
    public static final By FINISH_BUTTON        = By.id("finish-btn");

    // Error toaster ids and messages
    public static final By ERROR_ADDRESS            = By.id("wrong-address");
    public static final By ERROR_MAX_FILE_SIZE      = By.id("wrong-file-size");
    public static final By ERROR_MAX_IMAGE_NUMBERS  = By.id("wrong-number-image");

    public static final String ERROR_ADDRESS_MSG     = "Adresa koju ste unijeli je nevalidna.";
    public static final String ERROR_FILE_OVER_5MB   = "Veličina fajla mora biti manja od 5MB.";
    public static final String ERROR_FILE_NUMBER_MSG = "Ne možete postaviti više od 4 slike.";


    // Fixed data for populating tests
    public static final String ADVERTISER_WITH_ANNOUNCEMENT_USERNAME = "test_advertiser_company_member";
    public static final String ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD = "123456";

    // New announcement data
    public static final String ANN_NAME_VALUE  = "Izmijenjeno ime oglasa";
    public static final String RE_REGION_VALUE = "Izmijenjeni region";

    // Existing Real Estate address and similar area
    public static final String EXISTING_RE_COUNTRY_VALUE    = "Srbija";
    public static final String EXISTING_RE_CITY_VALUE       = "Novi Sad";
    public static final String EXISTING_RE_REGION_VALUE     = "Grbavica";
    public static final String EXISTING_RE_STREET_VALUE     = "Trg Dositeja Obradovica";
    public static final String EXISTING_RE_STREET_NO_VALUE  = "15";
    public static final String EXISTING_RE_AREA_VALUE       = "220";

    public static final String IMAGE_PATH_1 = "src/test/resources/pejicevi-salasi-1.jpg";
    public static final String IMAGE_PATH_2 = "src/test/resources/pejicevi-salasi-2.jpg";
    public static final String IMAGE_PATH_4 = "src/test/resources/4.jpg";
    public static final String IMAGE_PATH_5 = "src/test/resources/5.jpg";

    public static final String IMAGE_PATH_OVER_5MB = "src/test/resources/5mb.jpg";
}
