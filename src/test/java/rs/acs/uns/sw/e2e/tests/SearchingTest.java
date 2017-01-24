package rs.acs.uns.sw.e2e.tests;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import rs.acs.uns.sw.e2e.util.ConfigUtil;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static rs.acs.uns.sw.e2e.pages.HomePage.*;
import static rs.acs.uns.sw.e2e.util.Constants.WEB_DRIVER_TIMEOUT;

public class SearchingTest {

    private static WebDriver driver;

    // Wait in web driver until some condition is not satisfied
    private static WebDriverWait wait;

    @BeforeClass
    public static void instanceDriver() {
        ChromeOptions options = ConfigUtil.chromeOptions();
        options.addArguments("incognito");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WEB_DRIVER_TIMEOUT);
    }

    @AfterClass
    public static void closeDriver() {
        driver.close();
    }

    @Before
    public void openBrowser() {
        driver.get(HOME_URL);
    }

    /**
     * Test searching announcement by type rent
     * <p>
     * There is no precondition to be logged. First we choose to search announcements that
     * should be rent. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements with type 'Iznajmljivanje'
     */
    @Test
    public void searchAnnForRent() throws InterruptedException {
        // Check if driver's URL is equal to wanted URL
        searchByType(RENT_RADIO);

        driver.findElement(SEARCH_BTN).click();

        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        List<WebElement> result = container.findElements(ANNOUNCEMENT_TYPE_DISPLAY);
        for (WebElement e : result) {
            assertThat(e.getText()).isEqualTo("Iznajmljivanje");
        }
    }

    /**
     * Test searching announcement by type sale
     * <p>
     * There is no precondition to be logged. First we choose to search announcements that
     * should be sold. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements with type 'Prodaja'
     */
    @Test
    public void searchAnnForSale() throws InterruptedException {
        // Check if driver's URL is equal to wanted URL
        searchByType(SALE_RADIO);
        driver.findElement(SEARCH_BTN).click();

        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        List<WebElement> result = container.findElements(ANNOUNCEMENT_TYPE_DISPLAY);
        for (WebElement e : result) {
            assertThat(e.getText()).isEqualTo("Prodaja");
        }
    }

    /**
     * Test searching announcement by type buy
     * <p>
     * There is no precondition to be logged. First we choose to search announcements that
     * should be bought. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements with type 'Kupovina'
     */
    @Test
    public void searchAnnForBuy() throws InterruptedException {
        // Check if driver's URL is equal to wanted URL
        searchByType(BUY_RADIO);
        driver.findElement(SEARCH_BTN).click();

        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        List<WebElement> result = container.findElements(ANNOUNCEMENT_TYPE_DISPLAY);
        for (WebElement e : result) {
            assertThat(e.getText()).isEqualTo("Kupovina");
        }
    }

    /**
     * Test searching announcement by author
     * <p>
     * There is no precondition to be logged. First we choose to search announcements
     * whose author has specified first name, last name and phone number. Then we
     * click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose author
     * has specified
     */
    @Test
    public void searchAnnByAuthor() throws InterruptedException {
        // Check if driver's URL is equal to wanted URL
        searchByAuthor(AUTHOR_NAME_VALUE, AUTHOR_SURNAME_VALUE, PHONE_NUMBER_VALUE);
        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        assertThat(container.findElements(SINGLE_ANNOUNCEMENT).size()).isEqualTo(2);
    }

    /**
     * Test searching announcement by author without setting author's last name
     * <p>
     * There is no precondition to be logged. First we choose to search
     * announcements whose author has specified first name and phone number.
     * Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose
     * author has specified.
     */
    @Test
    public void searchAnnByAuthorWithEmptyField() throws InterruptedException {
        // Check if driver's URL is equal to wanted URL
        searchByAuthor(AUTHOR_NAME_VALUE, "", PHONE_NUMBER_VALUE);
        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(2000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        assertThat(container.findElements(SINGLE_ANNOUNCEMENT).size()).isEqualTo(2);
    }

    /**
     * Test searching announcement by location
     * <p>
     * There is no precondition to be logged. First we choose to search announcements
     * whose location has specified country, city, region, street and street number.
     * Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose location
     * is specified with parameters.
     */
    @Test
    public void searchAnnByLocation() throws InterruptedException {
        // Check if driver's URL is equal to wanted URL
        searchByLocation(COUNTRY_VALUE, CITY_VALUE, REGION_VALUE, STREET_VALUE, STREET_NO_VALUE);
        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        assertThat(container.findElements(SINGLE_ANNOUNCEMENT).size()).isEqualTo(1);
    }

    /**
     * Test searching announcement by location with some missing field
     * <p>
     * There is no precondition to be logged. First we choose to search
     * announcements whose location has specified city, region and street
     * number. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose
     * location is specified with parameters.
     */
    @Test
    public void searchAnnByLocationMissingSomeFields() throws InterruptedException {
        // Check if driver's URL is equal to wanted URL
        searchByLocation("", CITY_VALUE, REGION_VALUE, "", STREET_NO_VALUE);
        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        assertThat(container.findElements(SINGLE_ANNOUNCEMENT).size()).isEqualTo(1);
    }

    /**
     * Test searching announcement by part of location
     * <p>
     * There is no precondition to be logged. First we choose to search announcements whose
     * location has part of name for country and city and specific values for region and street
     * and doesn't have street number. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose
     * location is specified with parameters.
     */
    @Test
    public void searchAnnByLocationContaining() throws InterruptedException {
        // Check if driver's URL is equal to wanted URL
        searchByLocation(COUNTRY_VALUE.substring(2), CITY_VALUE.substring(0, 3), REGION_VALUE, STREET_VALUE, "");

        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        assertThat(container.findElements(SINGLE_ANNOUNCEMENT).size()).isEqualTo(1);
    }

    /**
     * Test searching announcement by location and author
     * <p>
     * There is no precondition to be logged. First we choose to search announcements whose
     * author has specified name, surname and phone number and whose location has specified
     * parameters. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose
     * location and author are specified with parameters.
     */
    @Test
    public void searchByLocationAndAuthor() throws InterruptedException {
        searchByLocation(COUNTRY_VALUE.substring(2), CITY_VALUE.substring(0, 3), REGION_VALUE, STREET_VALUE, "");
        searchByAuthor(AUTHOR_NAME_VALUE, AUTHOR_SURNAME_VALUE, PHONE_NUMBER_VALUE);

        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        assertThat(container.findElements(SINGLE_ANNOUNCEMENT).size()).isEqualTo(1);

        container.findElement(NO_RESULTS);
    }

    /**
     * Test searching announcement by area
     * <p>
     * There is no precondition to be logged. First we choose to search announcements whose
     * area is between specified values. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose displayed area
     * is between previously specified values.
     **/
    @Test
    public void searchByArea() throws InterruptedException {
        searchByArea("25", "56");
        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        List<WebElement> adds = container.findElements(SINGLE_ANNOUNCEMENT);

        for (WebElement element : adds) {
            assertThat(Integer.valueOf(element.findElement(PRICE_DISPLAY).getText())).isBetween(25, 56);
        }
    }

    /**
     * Test searching announcement by price
     * <p>
     * There is no precondition to be logged. First we choose to search announcements whose
     * price is less than specified values. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose displayed price
     * is less previously specified values.
     **/
    @Test
    public void searchByPrice() throws InterruptedException {
        searchByPrice("", "100");
        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        List<WebElement> adds = container.findElements(SINGLE_ANNOUNCEMENT);

        for (WebElement element : adds) {
            assertThat(Integer.valueOf(element.findElement(PRICE_DISPLAY).getText())).isLessThan(100);
        }
    }

    /**
     * Test searching announcement by price
     * <p>
     * There is no precondition to be logged. First we choose to search announcements whose
     * price is greater than specified values and area less than specified value and type is
     * rent. Then we click on button 'Pretra&#x17e;i' to start searching.
     * Expectation: On the home page should appear only announcements whose displayed price
     * is less than specified, type is rent and area is greater than specified value.
     **/
    @Test
    public void searchByAreaAndTypeAndPrice() throws InterruptedException {
        searchByPrice("50", "");
        searchByArea("", "500");
        searchByType(RENT_RADIO);
        driver.findElement(SEARCH_BTN).click();

        // Wait for results to load
        Thread.sleep(1000);
        WebElement container = driver.findElement(ANNOUNCEMENT_CONTAINER);
        List<WebElement> adds = container.findElements(SINGLE_ANNOUNCEMENT);

        for (WebElement element : adds) {
            assertThat(Integer.valueOf(element.findElement(PRICE_DISPLAY).getText())).isGreaterThan(50);
            assertThat(Integer.valueOf(element.findElement(ANNOUNCEMENT_TYPE_DISPLAY).getText())).isEqualTo("Iznajmljivanje");
        }
    }

    /**
     * Helper method for searching announcements by type
     *
     * @param element type element to be clicked on
     */
    private static void searchByType(By element) {
        // Check if driver's URL is equal to wanted URL
        assertThat(driver.getCurrentUrl()).isEqualTo(HOME_URL);
        wait.until(visibilityOfElementLocated(ANNOUNCEMENT_TYPE_COLLAPSE));

        driver.findElement(ANNOUNCEMENT_TYPE_COLLAPSE).click();

        wait.until(visibilityOfElementLocated(element));
        driver.findElement(element).click();
    }

    /**
     * Helper method for inputting values in search area for announcement's author
     *
     * @param name        author's name
     * @param surname     author's surname
     * @param phoneNumber author's phone number
     */
    private static void searchByAuthor(String name, String surname, String phoneNumber) {
        assertThat(driver.getCurrentUrl()).isEqualTo(HOME_URL);
        wait.until(visibilityOfElementLocated(ANNOUNCEMENT_AUTHOR_COLLAPSE));

        driver.findElement(ANNOUNCEMENT_AUTHOR_COLLAPSE).click();

        wait.until(visibilityOfElementLocated(AUTHOR_NAME));

        WebElement authorName = driver.findElement(AUTHOR_NAME);
        authorName.sendKeys(name);

        WebElement authorSurname = driver.findElement(AUTHOR_SURNAME);
        authorSurname.sendKeys(surname);

        WebElement authorPhone = driver.findElement(PHONE_NUMBER);
        authorPhone.sendKeys(phoneNumber);
    }

    /**
     * Helper method for inputting values in search area for announcement's author
     *
     * @param country  announcement's country
     * @param city     announcement's city
     * @param region   announcement's region
     * @param street   announcement's street
     * @param streetNo announcement't street number
     */
    private static void searchByLocation(String country, String city, String region, String street, String streetNo) {
        assertThat(driver.getCurrentUrl()).isEqualTo(HOME_URL);
        wait.until(visibilityOfElementLocated(ANNOUNCEMENT_LOCATION_COLLAPSE));

        driver.findElement(ANNOUNCEMENT_LOCATION_COLLAPSE).click();
        wait.until(visibilityOfElementLocated(COUNTRY));

        driver.findElement(COUNTRY).sendKeys(country);
        driver.findElement(CITY).sendKeys(city);
        driver.findElement(REGION).sendKeys(region);
        driver.findElement(STREET).sendKeys(street);
        driver.findElement(STREET_NO).sendKeys(streetNo);
    }

    /**
     * Helper method for inputting values in search area for announcement's price
     *
     * @param beginPrice starting price bound
     * @param endPrice   ending price bound
     */
    private static void searchByPrice(String beginPrice, String endPrice) {
        // Check if driver's URL is equal to wanted URL
        assertThat(driver.getCurrentUrl()).isEqualTo(HOME_URL);
        wait.until(visibilityOfElementLocated(ANNOUNCEMENT_PRICE_COLLAPSE));

        driver.findElement(ANNOUNCEMENT_PRICE_COLLAPSE).click();
        wait.until(visibilityOfElementLocated(PRICE_BEGIN));

        driver.findElement(PRICE_BEGIN).sendKeys(beginPrice);
        driver.findElement(PRICE_END).sendKeys(endPrice);
    }

    /**
     * Helper method for inputting values in search area for announcement's area
     *
     * @param beginArea starting bound
     * @param endArea   ending bound
     */
    private static void searchByArea(String beginArea, String endArea) {
        // Check if driver's URL is equal to wanted URL
        wait.until(visibilityOfElementLocated(ANNOUNCEMENT_AREA_COLLAPSE));

        driver.findElement(ANNOUNCEMENT_AREA_COLLAPSE).click();
        wait.until(visibilityOfElementLocated(AREA_BEGIN));

        driver.findElement(AREA_BEGIN).sendKeys(endArea);
        driver.findElement(AREA_END).sendKeys(beginArea);
    }
}
