package rs.acs.uns.sw.e2e.pages;

import org.openqa.selenium.By;

/**
 * Constants from client's Adding Annoucement page
 */
public final class AddingAnnouncementPage {

    /**
     * Private constructor to enable instatiating constant's class
     */
    private AddingAnnouncementPage() {}

    // Constant element ids
    public static final String ADDING_ANNOUCEMENT_URL = "http://localhost:8080/#/announcement-add";
    public static final String ADDED_ANNOUCEMENT_URL  = "http://localhost:8080/#/announcement/";

    public static final By ADD_ANNOUNCEMENT_LINK = By.id("add-announcement");

    public static final By ANNOUNCEMENT_NAME        = By.id("ann-name");
    public static final By ANNOUNCEMENT_PHONE       = By.id("ann-phone");
    public static final By ANNOUNCEMENT_PRICE       = By.id("ann-price");
    public static final By DATE_PICKER_BUTTON       = By.id("date-picker-btn");
    // public static final By ANNOUNCEMENT_DESCRIPTION = By.id("trix-input-1");
    // Announcement type 'SALE'
    public static final By ANNOUNCEMENT_TYPE        = By.id("type-sale");
    public static final By IMAGES_DROP_ZONE         = By.id("drop-images");
    public static final By IMAGES_UPLOAD_PROGRESS   = By.id("progress-bar");


    public static final By REAL_ESTATE_AREA      = By.id("re-area");
    public static final By REAL_ESTATE_HEATING   = By.id("re-heating-type");
    // Real Estate type 'HOUSE'
    public static final By REAL_ESTATE_TYPE      = By.id("re-type-house");
    public static final By REAL_ESTATE_CONUTRY   = By.id("re-country");
    public static final By REAL_ESTATE_CITY      = By.id("re-city");
    public static final By REAL_ESTATE_REGION    = By.id("re-region");
    public static final By REAL_ESTATE_STREET    = By.id("re-street");
    public static final By REAL_ESTATE_STREET_NO = By.id("re-street-no");
    // TODO Checkboxes

    // Similar real estate
    public static final By SIMILAR_REAL_ESTATE  = By.name("similar-real-estate");

    public static final By CONTINUE_BUTTON      = By.id("continue-btn");
    public static final By SIMILAR_RE_CONTINUE  = By.id("similar-realestate-continue-btn");
    public static final By FINISH_BUTTON        = By.id("finish-btn");

    // Error toaster ids and messages
    public static final By ERROR_ADDRESS            = By.id("wrong-address");
    public static final By ERROR_REQUIRED_FIELDS    = By.id("required-fields");
    public static final By ERROR_MAX_FILE_SIZE      = By.id("wrong-file-size");
    public static final By ERROR_MAX_IMAGE_NUMBERS  = By.id("wrong-number-image");

    public static final String ERROR_ADDRESS_MSG     = "Adresa koju ste unijeli je nevalidna.";
    public static final String ERROR_FILE_OVER_5MB   = "Veličina fajla mora biti manja od 5MB.";
    public static final String ERROR_FILE_NUMBER_MSG = "Ne možete postaviti više od 4 slike.";


    // Fixed data for populating tests
    public static final String ADVERTISER_USERNAME = "david";
    public static final String ADVERTISER_PASSWORD = "123456";

    // New announcement data
    public static final String ANN_NAME_VALUE        = "Prodaja kuće na Pejićevim Salašima";
    public static final String ANN_PHONE_VALUE       = "0641234567";
    public static final String ANN_PRICE_VALUE       = "666666";
    public static final String WRONG_ANN_PRICE_VALUE = "Cijena oglasa";

    public static final String ANN_DESCRIPTION_VALUE = "Očuvana kuća na Pejićevim Salašima. Uz kuću dolazi i farma pilića <b>Čvarkov i baba</b>.";

    public static final String RE_AREA_VALUE       = "73";
    public static final String RE_HEATING_TYPE     = "remote";
    public static final String RE_COUNTRY_VALUE    = "Srbija";
    public static final String RE_CITY_VALUE       = "Novi Sad";
    public static final String RE_REGION_VALUE     = "Čenej";
    public static final String RE_STREET_VALUE     = "Pejićevi Salaši";
    public static final String RE_STREET_NO_VALUE  = "BB";

    public static final String WRONG_COUNTRY_VALUE = "الأولى ";
    public static final String WRONG_CITY_VALUE    = "فصل";
    public static final String WRONG_STREET_VALUE  = "لعملة بلديهما للمجه";

    // Existing Real Estate address and similar area
    public static final String EXISTING_RE_COUNTRY_VALUE    = "Srbija";
    public static final String EXISTING_RE_CITY_VALUE       = "Novi Sad";
    public static final String EXISTING_RE_REGION_VALUE     = "Grbavica";
    public static final String EXISTING_RE_STREET_VALUE     = "Trg Dositeja Obradovica";
    public static final String EXISTING_RE_STREET_NO_VALUE  = "15";
    public static final String EXISTING_RE_AREA_VALUE       = "213";

    public static final String IMAGE_PATH_1 = "/Users/dmarjanovic/Desktop/awt-test/awt-test-siit-project-2016-service/src/test/resources/pejicevi-salasi-1.jpg";
    public static final String IMAGE_PATH_2 = "/Users/dmarjanovic/Desktop/awt-test/awt-test-siit-project-2016-service/src/test/resources/pejicevi-salasi-2.jpg";
    public static final String IMAGE_PATH_3 = "/Users/dmarjanovic/Desktop/awt-test/awt-test-siit-project-2016-service/src/test/resources/3.png";
    public static final String IMAGE_PATH_4 = "/Users/dmarjanovic/Desktop/awt-test/awt-test-siit-project-2016-service/src/test/resources/4.jpg";
    public static final String IMAGE_PATH_5 = "/Users/dmarjanovic/Desktop/awt-test/awt-test-siit-project-2016-service/src/test/resources/5jpg";

    public static final String IMAGE_PATH_OVER_5MB = "/Users/dmarjanovic/Desktop/awt-test/awt-test-siit-project-2016-service/src/test/resources/5mb.jpg";
}
