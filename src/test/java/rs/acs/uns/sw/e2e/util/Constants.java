package rs.acs.uns.sw.e2e.util;

/**
 * Mutual constants for all tests
 */
public interface Constants {

    String PROFILE_NAME = "test";

    // Chrome WebDriver constants
    String WEB_DRIVER_NAME = "webdriver.chrome.driver";
    Integer WEB_DRIVER_TIMEOUT = 20;

    // IMPORTANT: Change this locally to path of your driver
    String WEB_DRIVER_PATH = "/Users/Korisnik/Downloads/chromedriver.exe";

    String MAXIMIZE_OSX = "--kiosk";
    String MAXIMIZE_WIN = "--start-maximized";
}