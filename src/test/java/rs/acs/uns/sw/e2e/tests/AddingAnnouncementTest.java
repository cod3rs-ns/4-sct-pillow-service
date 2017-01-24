package rs.acs.uns.sw.e2e.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rs.acs.uns.sw.e2e.util.ConfigUtil;
import rs.acs.uns.sw.e2e.util.DragAndDropUtil;
import rs.acs.uns.sw.e2e.util.LoginUtil;
import rs.acs.uns.sw.sct.SctServiceApplication;

import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static rs.acs.uns.sw.e2e.pages.AddingAnnouncementPage.*;
import static rs.acs.uns.sw.e2e.util.Constants.WEB_DRIVER_TIMEOUT;

/**
 * Advertising tests.
 */

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
@Transactional
public class AddingAnnouncementTest {

    private static WebDriver driver;

    // Wait in web driver until some condition is not satisfied
    private static WebDriverWait wait;

    /**
     * Creates instance of Chrome Driver
     */
    @BeforeClass
    public static void instanceDriver() {
        ChromeOptions options = ConfigUtil.chromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WEB_DRIVER_TIMEOUT);
    }

    @AfterClass
    public static void closeDriver() {
        driver.close();
    }


    /**
     * Test which successfully add announcement.
     * <p>
     * First we logged as 'advertiser', then we click on link 'Dodaj oglas' where we check for existence of elements.
     * We populate all required fields with fixed data and click on button 'Nastavi'.
     * Then we creates add images of real estate that will be bonded to our announcement.
     * We finish adding announcement clicking on button 'Zavrsi' and check if announcement is added.
     */
    @Test
    public void addAdvertisementSuccessfullyNonExistingRealEstate() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement announcementPrice = driver.findElement(ANNOUNCEMENT_PRICE);
        final WebElement announcementPhone = driver.findElement(ANNOUNCEMENT_PHONE);
        final WebElement announcementDateExp = driver.findElement(DATE_PICKER_BUTTON);
        final WebElement annoucementTypeSale = driver.findElement(ANNOUNCEMENT_TYPE);

        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateType = driver.findElement(REAL_ESTATE_TYPE);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);
        final Select realEstateHeatingType = new Select(driver.findElement(REAL_ESTATE_HEATING));

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(announcementPrice).isNotNull();
        assertThat(announcementPhone).isNotNull();
        assertThat(announcementDateExp).isNotNull();
        //assertThat(announcementDescription).isNotNull();
        assertThat(annoucementTypeSale).isNotNull();

        assertThat(realEstateArea).isNotNull();
        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        announcementName.sendKeys(ANN_NAME_VALUE);
        // Set Announcement Type to 'SALE'
        annoucementTypeSale.click();
        announcementPrice.sendKeys(ANN_PRICE_VALUE);
        announcementPhone.sendKeys(ANN_PHONE_VALUE);
        announcementDateExp.click();

        realEstateArea.sendKeys(RE_AREA_VALUE);
        realEstateHeatingType.selectByValue(RE_HEATING_TYPE);
        // Set Real Estate type to 'HOUSE'
        realEstateType.click();
        realEstateCountry.sendKeys(RE_COUNTRY_VALUE);
        realEstateCity.sendKeys(RE_CITY_VALUE);
        realEstateRegion.sendKeys(RE_REGION_VALUE);
        realEstateStreet.sendKeys(RE_STREET_VALUE);
        realEstateStreetNo.sendKeys(RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        // Second part of adding announcement
        final WebElement dropZone = driver.findElement(IMAGES_DROP_ZONE);
        final WebElement progress = driver.findElement(IMAGES_UPLOAD_PROGRESS);

        assertThat(dropZone).isNotNull();
        assertThat(progress).isNotNull();

        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_1);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_2);

        final WebElement finishButton = driver.findElement(FINISH_BUTTON);
        finishButton.click();

        // Wait to redirect to added announcement page
        wait.until(ExpectedConditions.urlContains(ADDED_ANNOUNCEMENT_URL));

        LoginUtil.logout(driver, wait);
    }

    /**
     * Test which successfully add announcement with existing announcement.
     * <p>
     * First we logged as 'advertiser', then we click on link 'Dodaj oglas' where we check for existence of elements.
     * We populate all required fields with fixed data and click on button 'Nastavi'.
     * We recognize similar real estate and we click on first recommended real estate.
     * Then we creates add images of real estate that will be bonded to our announcement.
     * We finish adding announcement clicking on button 'Zavrsi' and check if announcement is added.
     */
    @Test
    public void addAdvertisementSuccessfullyExistingRealEstate() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement announcementPrice = driver.findElement(ANNOUNCEMENT_PRICE);
        final WebElement announcementPhone = driver.findElement(ANNOUNCEMENT_PHONE);
        final WebElement announcementDateExp = driver.findElement(DATE_PICKER_BUTTON);
        //final WebElement announcementDescription = driver.findElement(ANNOUNCEMENT_DESCRIPTION);
        final WebElement annoucementTypeSale = driver.findElement(ANNOUNCEMENT_TYPE);

        // We need to populate same address for our real estate because our 'algorithm' works on that way.
        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateType = driver.findElement(REAL_ESTATE_TYPE);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);
        final Select realEstateHeatingType = new Select(driver.findElement(REAL_ESTATE_HEATING));

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(announcementPrice).isNotNull();
        assertThat(announcementPhone).isNotNull();
        assertThat(announcementDateExp).isNotNull();
        //assertThat(announcementDescription).isNotNull();
        assertThat(annoucementTypeSale).isNotNull();

        assertThat(realEstateArea).isNotNull();
        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        announcementName.sendKeys(ANN_NAME_VALUE);
        // Set Announcement Type to 'SALE'
        annoucementTypeSale.click();
        announcementPrice.sendKeys(ANN_PRICE_VALUE);
        announcementPhone.sendKeys(ANN_PHONE_VALUE);
        announcementDateExp.click();

        realEstateArea.sendKeys(EXISTING_RE_AREA_VALUE);
        realEstateHeatingType.selectByValue(RE_HEATING_TYPE);
        // Set Real Estate type to 'HOUSE'
        realEstateType.click();
        realEstateCountry.sendKeys(EXISTING_RE_COUNTRY_VALUE);
        realEstateCity.sendKeys(EXISTING_RE_CITY_VALUE);
        realEstateRegion.sendKeys(EXISTING_RE_REGION_VALUE);
        realEstateStreet.sendKeys(EXISTING_RE_STREET_VALUE);
        realEstateStreetNo.sendKeys(EXISTING_RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(SIMILAR_REAL_ESTATE));

        // We choose similar real estate
        wait.until(ExpectedConditions.visibilityOfElementLocated(SIMILAR_REAL_ESTATE));
        final WebElement firstSimilarRealEstate = driver.findElement(SIMILAR_REAL_ESTATE);
        firstSimilarRealEstate.click();

        final WebElement continueRealEstateButton = driver.findElement(SIMILAR_RE_CONTINUE);
        wait.until(ExpectedConditions.presenceOfElementLocated(SIMILAR_RE_CONTINUE));
        continueRealEstateButton.click();

        // Last part of adding announcement
        final WebElement dropZone = driver.findElement(IMAGES_DROP_ZONE);
        final WebElement progress = driver.findElement(IMAGES_UPLOAD_PROGRESS);

        assertThat(dropZone).isNotNull();
        assertThat(progress).isNotNull();

        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_1);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_2);

        final WebElement finishButton = driver.findElement(FINISH_BUTTON);
        finishButton.click();

        // Wait to redirect to added annoucement page
        wait.until(ExpectedConditions.urlContains(ADDED_ANNOUNCEMENT_URL));

        LoginUtil.logout(driver, wait);
    }

    /**
     * Test which successfully add announcement with skipping existing announcement.
     * <p>
     * First we logged as 'advertiser', then we click on link 'Dodaj oglas' where we check for existence of elements.
     * We populate all required fields with fixed data and click on button 'Nastavi'.
     * We recognize similar real estate but we skip (that means we'll create our own real estate.
     * Then we creates add images of real estate that will be bonded to our announcement.
     * We finish adding announcement clicking on button 'Zavrsi' and check if announcement is added.
     */
    @Test
    public void addAdvertisementSuccessfullySkipExistingRealEstate() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement announcementPrice = driver.findElement(ANNOUNCEMENT_PRICE);
        final WebElement announcementPhone = driver.findElement(ANNOUNCEMENT_PHONE);
        final WebElement announcementDateExp = driver.findElement(DATE_PICKER_BUTTON);
        //final WebElement announcementDescription = driver.findElement(ANNOUNCEMENT_DESCRIPTION);
        final WebElement annoucementTypeSale = driver.findElement(ANNOUNCEMENT_TYPE);

        // We need to populate same address for our real estate because our 'algorithm' works on that way.
        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateType = driver.findElement(REAL_ESTATE_TYPE);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);
        final Select realEstateHeatingType = new Select(driver.findElement(REAL_ESTATE_HEATING));

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(announcementPrice).isNotNull();
        assertThat(announcementPhone).isNotNull();
        assertThat(announcementDateExp).isNotNull();
        //assertThat(announcementDescription).isNotNull();
        assertThat(annoucementTypeSale).isNotNull();

        assertThat(realEstateArea).isNotNull();
        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        announcementName.sendKeys(ANN_NAME_VALUE);
        // Set Announcement Type to 'SALE'
        annoucementTypeSale.click();
        announcementPrice.sendKeys(ANN_PRICE_VALUE);
        announcementPhone.sendKeys(ANN_PHONE_VALUE);
        announcementDateExp.click();

        realEstateArea.sendKeys(EXISTING_RE_AREA_VALUE);
        realEstateHeatingType.selectByValue(RE_HEATING_TYPE);
        // Set Real Estate type to 'HOUSE'
        realEstateType.click();
        realEstateCountry.sendKeys(EXISTING_RE_COUNTRY_VALUE);
        realEstateCity.sendKeys(EXISTING_RE_CITY_VALUE);
        realEstateRegion.sendKeys(EXISTING_RE_REGION_VALUE);
        realEstateStreet.sendKeys(EXISTING_RE_STREET_VALUE);
        realEstateStreetNo.sendKeys(EXISTING_RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(SIMILAR_REAL_ESTATE));

        wait.until(ExpectedConditions.visibilityOfElementLocated(SIMILAR_RE_CONTINUE));
        final WebElement continueRealEstateButton = driver.findElement(SIMILAR_RE_CONTINUE);
        continueRealEstateButton.click();

        // Last part of adding announcement
        final WebElement dropZone = driver.findElement(IMAGES_DROP_ZONE);
        final WebElement progress = driver.findElement(IMAGES_UPLOAD_PROGRESS);

        assertThat(dropZone).isNotNull();
        assertThat(progress).isNotNull();

        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_1);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_2);

        final WebElement finishButton = driver.findElement(FINISH_BUTTON);
        finishButton.click();

        // Wait to redirect to added announcement page
        wait.until(ExpectedConditions.urlContains(ADDED_ANNOUNCEMENT_URL));
        LoginUtil.logout(driver, wait);
    }

    /**
     * Test which tries to add empty announcement's form
     * <p>
     * When we logged as advertiser we try to submit empty announcement from.
     * We check if 'Nastavi' button is disabled.
     */
    @Test
    public void addEmptyAnnouncement() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);

        assertThat(continueButton.isEnabled()).isFalse();
        LoginUtil.logout(driver, wait);
    }

    /**
     * Test which tries to add announcement without added images.
     * <p>
     * We fill first part of form properly, and try to submit without added images.
     * Then we check if 'Zavrsi' button is disabled.
     */
    @Test
    public void addAnnouncementWithoutImages() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement announcementPrice = driver.findElement(ANNOUNCEMENT_PRICE);
        final WebElement announcementPhone = driver.findElement(ANNOUNCEMENT_PHONE);
        final WebElement announcementDateExp = driver.findElement(DATE_PICKER_BUTTON);
        //final WebElement announcementDescription = driver.findElement(ANNOUNCEMENT_DESCRIPTION);
        final WebElement announcementTypeSale = driver.findElement(ANNOUNCEMENT_TYPE);

        // We need to populate same address for our real estate because our 'algorithm' works on that way.
        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateType = driver.findElement(REAL_ESTATE_TYPE);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);
        final Select realEstateHeatingType = new Select(driver.findElement(REAL_ESTATE_HEATING));

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(announcementPrice).isNotNull();
        assertThat(announcementPhone).isNotNull();
        assertThat(announcementDateExp).isNotNull();
        //assertThat(announcementDescription).isNotNull();
        assertThat(announcementTypeSale).isNotNull();

        assertThat(realEstateArea).isNotNull();
        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        announcementName.sendKeys(ANN_NAME_VALUE);
        // Set Announcement Type to 'SALE'
        announcementTypeSale.click();
        announcementPrice.sendKeys(ANN_PRICE_VALUE);
        announcementPhone.sendKeys(ANN_PHONE_VALUE);
        announcementDateExp.click();

        // Mix Inputs because we don't want similar real estates
        realEstateArea.sendKeys(EXISTING_RE_AREA_VALUE);
        realEstateHeatingType.selectByValue(RE_HEATING_TYPE);
        // Set Real Estate type to 'HOUSE'
        realEstateType.click();
        realEstateCountry.sendKeys(RE_COUNTRY_VALUE);
        realEstateCity.sendKeys(RE_CITY_VALUE);
        realEstateRegion.sendKeys(EXISTING_RE_REGION_VALUE);
        realEstateStreet.sendKeys(RE_STREET_VALUE);
        realEstateStreetNo.sendKeys(RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        final WebElement finishButton = driver.findElement(FINISH_BUTTON);

        assertThat(finishButton.isEnabled()).isFalse();
        LoginUtil.logout(driver, wait);
    }

    /**
     * Test which tries to submit announcement with wrong price type.
     * Price can only be postive number value.
     * <p>
     * We input string as price.
     * Then we check if toaster is presented and if error message is correct.
     */
    @Test
    public void wrongPriceType() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement announcementPrice = driver.findElement(ANNOUNCEMENT_PRICE);
        final WebElement announcementPhone = driver.findElement(ANNOUNCEMENT_PHONE);
        final WebElement announcementDateExp = driver.findElement(DATE_PICKER_BUTTON);
        //final WebElement announcementDescription = driver.findElement(ANNOUNCEMENT_DESCRIPTION);
        final WebElement announcementTypeSale = driver.findElement(ANNOUNCEMENT_TYPE);

        // We need to populate same address for our real estate because our 'algorithm' works on that way.
        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateType = driver.findElement(REAL_ESTATE_TYPE);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);
        final Select realEstateHeatingType = new Select(driver.findElement(REAL_ESTATE_HEATING));

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(announcementPrice).isNotNull();
        assertThat(announcementPhone).isNotNull();
        assertThat(announcementDateExp).isNotNull();
        //assertThat(announcementDescription).isNotNull();
        assertThat(announcementTypeSale).isNotNull();

        assertThat(realEstateArea).isNotNull();
        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        announcementName.sendKeys(ANN_NAME_VALUE);
        // Set Announcement Type to 'SALE'
        announcementTypeSale.click();
        announcementPrice.sendKeys(WRONG_ANN_PRICE_VALUE);
        announcementPhone.sendKeys(ANN_PHONE_VALUE);
        announcementDateExp.click();

        // Mix Inputs because we don't want similar real estates
        realEstateArea.sendKeys(RE_AREA_VALUE);
        realEstateHeatingType.selectByValue(RE_HEATING_TYPE);
        // Set Real Estate type to 'HOUSE'
        realEstateType.click();
        realEstateCountry.sendKeys(RE_COUNTRY_VALUE);
        realEstateCity.sendKeys(RE_CITY_VALUE);
        realEstateRegion.sendKeys(RE_REGION_VALUE);
        realEstateStreet.sendKeys(RE_STREET_VALUE);
        realEstateStreetNo.sendKeys(RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        assertThat(announcementPrice.getAttribute("class")).contains("ng-invalid-number");
        assertThat(announcementPrice.getAttribute("class")).contains("ng-invalid");
        LoginUtil.logout(driver, wait);
    }

    /**
     * Test which tries to submit announcement with wrong address.
     * Wrong address is when Google Map service can't provide latitude and longitude.
     * <p>
     * We input non existing address and try to submit it.
     * Then we check if toaster is presented and if error message is correct.
     */
    @Test
    public void wrongAddress() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement announcementPrice = driver.findElement(ANNOUNCEMENT_PRICE);
        final WebElement announcementPhone = driver.findElement(ANNOUNCEMENT_PHONE);
        final WebElement announcementDateExp = driver.findElement(DATE_PICKER_BUTTON);
        //final WebElement announcementDescription = driver.findElement(ANNOUNCEMENT_DESCRIPTION);
        final WebElement announcementTypeSale = driver.findElement(ANNOUNCEMENT_TYPE);

        // We need to populate same address for our real estate because our 'algorithm' works on that way.
        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateType = driver.findElement(REAL_ESTATE_TYPE);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);
        final Select realEstateHeatingType = new Select(driver.findElement(REAL_ESTATE_HEATING));

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(announcementPrice).isNotNull();
        assertThat(announcementPhone).isNotNull();
        assertThat(announcementDateExp).isNotNull();
        //assertThat(announcementDescription).isNotNull();
        assertThat(announcementTypeSale).isNotNull();

        assertThat(realEstateArea).isNotNull();
        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        announcementName.sendKeys(ANN_NAME_VALUE);
        // Set Announcement Type to 'SALE'
        announcementTypeSale.click();
        announcementPrice.sendKeys(ANN_PRICE_VALUE);
        announcementPhone.sendKeys(ANN_PHONE_VALUE);
        announcementDateExp.click();

        // Mix Inputs because we don't want similar real estates
        realEstateArea.sendKeys(EXISTING_RE_AREA_VALUE);
        realEstateHeatingType.selectByValue(RE_HEATING_TYPE);
        // Set Real Estate type to 'HOUSE'
        realEstateType.click();
        realEstateCountry.sendKeys(WRONG_COUNTRY_VALUE);
        realEstateCity.sendKeys(WRONG_CITY_VALUE);
        realEstateRegion.sendKeys(EXISTING_RE_REGION_VALUE);
        realEstateStreet.sendKeys(WRONG_STREET_VALUE);
        realEstateStreetNo.sendKeys(RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_ADDRESS));
        final WebElement errorToaster = driver.findElement(ERROR_ADDRESS);

        assertThat(errorToaster.getText()).isEqualTo(ERROR_ADDRESS_MSG);
        errorToaster.click();

        LoginUtil.logout(driver, wait);
    }

    /**
     * Test which tries to add image with size over 5 MB
     * <p>
     * We try to add image, but then we assert if error toast message is displayed with
     * proper test defined.
     */
    @Test
    public void maxImageSizeExceeded() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement announcementPrice = driver.findElement(ANNOUNCEMENT_PRICE);
        final WebElement announcementPhone = driver.findElement(ANNOUNCEMENT_PHONE);
        final WebElement announcementDateExp = driver.findElement(DATE_PICKER_BUTTON);
        //final WebElement announcementDescription = driver.findElement(ANNOUNCEMENT_DESCRIPTION);
        final WebElement announcementTypeSale = driver.findElement(ANNOUNCEMENT_TYPE);

        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateType = driver.findElement(REAL_ESTATE_TYPE);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);
        final Select realEstateHeatingType = new Select(driver.findElement(REAL_ESTATE_HEATING));

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(announcementPrice).isNotNull();
        assertThat(announcementPhone).isNotNull();
        assertThat(announcementDateExp).isNotNull();
        //assertThat(announcementDescription).isNotNull();
        assertThat(announcementTypeSale).isNotNull();

        assertThat(realEstateArea).isNotNull();
        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        announcementName.sendKeys(ANN_NAME_VALUE);
        // Set Announcement Type to 'SALE'
        announcementTypeSale.click();
        announcementPrice.sendKeys(ANN_PRICE_VALUE);
        announcementPhone.sendKeys(ANN_PHONE_VALUE);
        announcementDateExp.click();

        realEstateArea.sendKeys(RE_AREA_VALUE);
        realEstateHeatingType.selectByValue(RE_HEATING_TYPE);
        // Set Real Estate type to 'HOUSE'
        realEstateType.click();
        realEstateCountry.sendKeys(RE_COUNTRY_VALUE);
        realEstateCity.sendKeys(RE_CITY_VALUE);
        realEstateRegion.sendKeys(RE_REGION_VALUE);
        realEstateStreet.sendKeys(RE_STREET_VALUE);
        realEstateStreetNo.sendKeys(RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        // Second part of adding announcement
        final WebElement dropZone = driver.findElement(IMAGES_DROP_ZONE);
        final WebElement progress = driver.findElement(IMAGES_UPLOAD_PROGRESS);

        assertThat(dropZone).isNotNull();
        assertThat(progress).isNotNull();

        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_OVER_5MB);

        wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MAX_FILE_SIZE));
        final WebElement errorToaster = driver.findElement(ERROR_MAX_FILE_SIZE);

        assertThat(errorToaster.getText()).isEqualTo(ERROR_FILE_OVER_5MB);
        errorToaster.click();

        LoginUtil.logout(driver, wait);
    }

    /**
     * Test which tries to add announcement as guest.
     * <p>
     * We try to navigate to adding announcement url
     * and then get unauthorized message
     */
    @Test
    public void tryToAddAnnouncementAsAGuest() {
        driver.navigate().to(ADDING_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(UNAUTHORIZED_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(UNAUTHORIZED_URL);
    }

    /**
     * Test which tries to add more than 4 images
     * <p>
     * We try to add more than 4 images, but then we assert if error toast message is displayed with
     * proper test defined.
     */
    @Test
    public void maxAllowedUploadImageNumberExceeded() {
        LoginUtil.login(ADVERTISER_USERNAME, ADVERTISER_PASSWORD, driver, wait);

        final WebElement addAnnouncementLink = driver.findElement(ADD_ANNOUNCEMENT_LINK);
        addAnnouncementLink.click();

        // Check if we're on right URL (Adding Announcement Form)
        wait.until(ExpectedConditions.urlToBe(ADDING_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADDING_ANNOUNCEMENT_URL);

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement announcementPrice = driver.findElement(ANNOUNCEMENT_PRICE);
        final WebElement announcementPhone = driver.findElement(ANNOUNCEMENT_PHONE);
        final WebElement announcementDateExp = driver.findElement(DATE_PICKER_BUTTON);
        final WebElement announcementTypeSale = driver.findElement(ANNOUNCEMENT_TYPE);

        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateType = driver.findElement(REAL_ESTATE_TYPE);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);
        final Select realEstateHeatingType = new Select(driver.findElement(REAL_ESTATE_HEATING));

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(announcementPrice).isNotNull();
        assertThat(announcementPhone).isNotNull();
        assertThat(announcementDateExp).isNotNull();
        //assertThat(announcementDescription).isNotNull();
        assertThat(announcementTypeSale).isNotNull();

        assertThat(realEstateArea).isNotNull();
        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        announcementName.sendKeys(ANN_NAME_VALUE);
        // Set Announcement Type to 'SALE'
        announcementTypeSale.click();
        announcementPrice.sendKeys(ANN_PRICE_VALUE);
        announcementPhone.sendKeys(ANN_PHONE_VALUE);
        announcementDateExp.click();

        realEstateArea.sendKeys(RE_AREA_VALUE);
        realEstateHeatingType.selectByValue(RE_HEATING_TYPE);
        // Set Real Estate type to 'HOUSE'
        realEstateType.click();
        realEstateCountry.sendKeys(RE_COUNTRY_VALUE);
        realEstateCity.sendKeys(RE_CITY_VALUE);
        realEstateRegion.sendKeys(RE_REGION_VALUE);
        realEstateStreet.sendKeys(RE_STREET_VALUE);
        realEstateStreetNo.sendKeys(RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        // Second part of adding announcement
        final WebElement dropZone = driver.findElement(IMAGES_DROP_ZONE);
        final WebElement progress = driver.findElement(IMAGES_UPLOAD_PROGRESS);

        assertThat(dropZone).isNotNull();
        assertThat(progress).isNotNull();

        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_1);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_2);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_3);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_4);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_3);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_4);

        wait.until(ExpectedConditions.presenceOfElementLocated(ERROR_MAX_IMAGE_NUMBERS));
        List<WebElement> errorToasters = driver.findElements(ERROR_MAX_IMAGE_NUMBERS);
        for (WebElement msg : errorToasters) {
            assertThat(msg.getText()).isEqualTo(ERROR_FILE_NUMBER_MSG);
            msg.click();
        }

        LoginUtil.logout(driver, wait);
    }
}
