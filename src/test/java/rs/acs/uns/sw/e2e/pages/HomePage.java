package rs.acs.uns.sw.e2e.pages;

import org.openqa.selenium.By;

/**
 * Constants from client's Home Page
 */
public class HomePage {

    public static final String HOME_URL = "http://localhost:8080/#/home";
    public static final String VERIFICATION_SUCCESS_URL = "http://localhost:8080/#/registration-confirm-success";

    public static final By NO_RESULTS= By.xpath("//*[contains(text(),'Nažalost, nema rezultata')]");

    public static final By ANNOUNCEMENT_TYPE_COLLAPSE = By.id("open-ann-type");
    public static final By ANNOUNCEMENT_CONTAINER = By.id("ann-container");
    public static final By SEARCH_BTN = By.id("search");
    public static final By SINGLE_ANNOUNCEMENT = By.className("announcement-home-outside");
    public static final By ANNOUNCEMENT_TYPE_DISPLAY = By.id("ann-type-disp");
    public static final By RENT_RADIO = By.id("rent");
    public static final By SALE_RADIO = By.id("sale");
    public static final By BUY_RADIO = By.id("buy");

    public static final By ANNOUNCEMENT_AUTHOR_COLLAPSE = By.id("open-ann-author");
    public static final By AUTHOR_NAME = By.id("author-name");
    public static final By AUTHOR_SURNAME = By.id("author-surname");
    public static final By PHONE_NUMBER = By.id("phone-number");

    public static final By ANNOUNCEMENT_LOCATION_COLLAPSE = By.id("open-ann-location");
    public static final By COUNTRY = By.id("country");
    public static final By CITY = By.id("city");
    public static final By REGION = By.id("region");
    public static final By STREET = By.id("street");
    public static final By STREET_NO = By.id("street-no");

    public static final By ANNOUNCEMENT_PRICE_COLLAPSE = By.id("open-ann-price");
    public static final By PRICE_BEGIN = By.id("price-begin");
    public static final By PRICE_END = By.id("price-end");
    public static final By PRICE_DISPLAY = By.id("ann-price");

    public static final By ANNOUNCEMENT_AREA_COLLAPSE = By.id("open-ann-area");
    public static final By AREA_BEGIN = By.id("area-begin");
    public static final By AREA_END = By.id("area-end");

    public static final String AUTHOR_NAME_VALUE = "TeSt";
    public static final String AUTHOR_SURNAME_VALUE = "advERT";
    public static final String PHONE_NUMBER_VALUE = "060";

    public static final String CITY_VALUE = "Ugljevik";
    public static final String REGION_VALUE = "Senjak";
    public static final String COUNTRY_VALUE = "Bosna i Hercegovina";
    public static final String STREET_VALUE = "Svetog Save";
    public static final String STREET_NO_VALUE = "51";

}
