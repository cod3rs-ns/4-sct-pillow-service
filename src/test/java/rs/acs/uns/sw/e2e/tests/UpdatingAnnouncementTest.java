package rs.acs.uns.sw.e2e.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rs.acs.uns.sw.e2e.util.ConfigUtil;
import rs.acs.uns.sw.e2e.util.DragAndDropUtil;
import rs.acs.uns.sw.e2e.util.LoginUtil;
import rs.acs.uns.sw.sct.SctServiceApplication;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static rs.acs.uns.sw.e2e.pages.UpdatingAnnouncementPage.*;
import static rs.acs.uns.sw.e2e.util.Constants.WEBDRIVER_TIMEOUT;

/**
 * Update announcements tests.
 */

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
@Transactional
public class UpdatingAnnouncementTest {

    private static WebDriver driver;

    // Wait in webdriver until some condition is not satisfied
    private static WebDriverWait wait;

    /**
     *  Creates instance of Chrome Driver
     */
    @BeforeClass
    public static void instanceDriver() {
        ChromeOptions options = ConfigUtil.chromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WEBDRIVER_TIMEOUT);
    }

    @AfterClass
    public static void closeDriver() {
        driver.close();
    }

    /**
     * Test which successfully updates announcement.
     *
     * First we logged as 'advertiser', then we open one our announcement.
     * We change values we want and click button 'Nastavi'.
     * Then we creates add images of real estate that will be bonded to our annoucement.
     * We finish updating announement clicking on button 'Zavrsi'.
     *
     * Then we open our announcement page again and check for updates.
     */
    @Test
    public void successfullyUpdateAnnouncement() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(ADVERTISER_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Announcement for update)
        wait.until(ExpectedConditions.urlToBe(ADVERTISER_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADVERTISER_ANNOUNCEMENT_URL);

        final WebElement updateButton = driver.findElement(UPDATE_BUTTON);
        updateButton.click();

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();
        assertThat(realEstateRegion).isNotNull();

        // Populate data
        announcementName.clear();
        announcementName.sendKeys(ANN_NAME_VALUE);

        realEstateRegion.clear();
        realEstateRegion.sendKeys(RE_REGION_VALUE);

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

        // Wait to redirect to added annoucement page
        wait.until(ExpectedConditions.urlContains(ADVERTISER_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADVERTISER_ANNOUNCEMENT_URL);
        assertThat(driver.findElement(ANN_NAME_HTML_VALUE).getText()).isEqualTo(ANN_NAME_VALUE);
    }

    /**
     * Test which successfully add announcement with existing real estate.
     *
     * First we logged as 'advertiser', then we open one our announcement.
     * We change values to an existing real estate values and click button 'Nastavi'.
     * Then we choose existing real estate and bond it to our announcement
     *
     * Then we open our announcement page again and check for updates.
     */
    @Test
    public void successufullyUpdateRealEstate() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(ADVERTISER_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Announcement for update)
        wait.until(ExpectedConditions.urlToBe(ADVERTISER_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADVERTISER_ANNOUNCEMENT_URL);

        final WebElement updateButton = driver.findElement(UPDATE_BUTTON);
        updateButton.click();

        // We need to populate same address for our real estate because our 'algorithm' works on that way.
        final WebElement realEstateArea = driver.findElement(REAL_ESTATE_AREA);
        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);

        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        realEstateArea.clear();
        realEstateArea.sendKeys(EXISTING_RE_AREA_VALUE);
        realEstateCountry.clear();
        realEstateCountry.sendKeys(EXISTING_RE_COUNTRY_VALUE);
        realEstateCity.clear();
        realEstateCity.sendKeys(EXISTING_RE_CITY_VALUE);
        realEstateRegion.clear();
        realEstateRegion.sendKeys(EXISTING_RE_REGION_VALUE);
        realEstateStreet.clear();
        realEstateStreet.sendKeys(EXISTING_RE_STREET_VALUE);
        realEstateStreetNo.clear();
        realEstateStreetNo.sendKeys(EXISTING_RE_STREET_NO_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(SIMILAR_REAL_ESTATE));

        // We choose similar real estate
        final WebElement firstSimilarRealEstate = driver.findElement(SIMILAR_REAL_ESTATE);
        firstSimilarRealEstate.click();

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

        // Wait to redirect to added annoucement page
        wait.until(ExpectedConditions.urlContains(ADVERTISER_ANNOUNCEMENT_URL));
    }



    /**
     * Test which tries to delete annoucement name and submit update
     *
     * When we logged as advertiser we try to submit empty announcement from.
     * We check if 'Nastavi' button is disabled.
     */
    @Test
    public void tryToUpdateWithoutName() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(ADVERTISER_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Announcement for update)
        wait.until(ExpectedConditions.urlToBe(ADVERTISER_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADVERTISER_ANNOUNCEMENT_URL);

        final WebElement updateButton = driver.findElement(UPDATE_BUTTON);
        updateButton.click();

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();

        // Populate data
        announcementName.clear();

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);

        assertThat(continueButton.isEnabled()).isFalse();
    }

    /**
     * Test which tries to update annoucement without added images.
     *
     * We fill first part of form properly, and try to submit without added images.
     * Then we check if 'Zavrsi' button is disabled.
     */
    @Test
    public void tryToUpdateAnnouncementWithoutImages() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(ADVERTISER_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Announcement for update)
        wait.until(ExpectedConditions.urlToBe(ADVERTISER_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADVERTISER_ANNOUNCEMENT_URL);

        final WebElement updateButton = driver.findElement(UPDATE_BUTTON);
        updateButton.click();

        // Get elements
        final WebElement announcementName = driver.findElement(ANNOUNCEMENT_NAME);

        // Assert that elements are presented
        assertThat(announcementName).isNotNull();

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        driver.findElements(DELETE_UPLOADED_IMAGE).forEach(button -> button.click());

        final WebElement finishButton = driver.findElement(FINISH_BUTTON);

        assertThat(finishButton.isEnabled()).isFalse();
    }

    /**
     * Test which tries to submit announcement with wrong address.
     * Wrong address is when Google Map service can't provide latitude and longitude.
     *
     * We input non existing address and try to submit it.
     * Then we check if toaster is presented and if error message is correct.
     */
    @Test
    public void wrongAddress() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(ADVERTISER_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Announcement for update)
        wait.until(ExpectedConditions.urlToBe(ADVERTISER_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADVERTISER_ANNOUNCEMENT_URL);

        final WebElement updateButton = driver.findElement(UPDATE_BUTTON);
        updateButton.click();

        final WebElement realEstateCountry = driver.findElement(REAL_ESTATE_CONUTRY);
        final WebElement realEstateCity = driver.findElement(REAL_ESTATE_CITY);
        final WebElement realEstateRegion = driver.findElement(REAL_ESTATE_REGION);
        final WebElement realEstateStreet = driver.findElement(REAL_ESTATE_STREET);
        final WebElement realEstateStreetNo = driver.findElement(REAL_ESTATE_STREET_NO);

        assertThat(realEstateCountry).isNotNull();
        assertThat(realEstateCity).isNotNull();
        assertThat(realEstateRegion).isNotNull();
        assertThat(realEstateStreet).isNotNull();
        assertThat(realEstateStreetNo).isNotNull();

        // Populate data
        realEstateCountry.sendKeys(WRONG_COUNTRY_VALUE);
        realEstateCity.sendKeys(WRONG_CITY_VALUE);
        realEstateRegion.sendKeys(EXISTING_RE_REGION_VALUE);
        realEstateStreet.sendKeys(WRONG_STREET_VALUE);

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(ERROR_ADDRESS));
        final WebElement errorToaster = driver.findElement(ERROR_ADDRESS);

        assertThat(errorToaster.getText()).isEqualTo(ERROR_ADDRESS_MSG);
    }

    /**
     * Test which tries to add image with size over 5 MB
     *
     * We try to add image, but then we assert if error toast message is displayed with
     * proper test defined.
     */
    @Test
    public void maxImageSizeExceeded() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(ADVERTISER_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Announcement for update)
        wait.until(ExpectedConditions.urlToBe(ADVERTISER_ANNOUNCEMENT_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(ADVERTISER_ANNOUNCEMENT_URL);

        final WebElement updateButton = driver.findElement(UPDATE_BUTTON);
        updateButton.click();

        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(ADVERTISER_ANNOUNCEMENT_URL)));
        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        // Second part of adding announcement
        final WebElement dropZone = driver.findElement(IMAGES_DROP_ZONE);
        final WebElement progress = driver.findElement(IMAGES_UPLOAD_PROGRESS);

        assertThat(dropZone).isNotNull();
        assertThat(progress).isNotNull();

        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_OVER_5MB);

        wait.until(ExpectedConditions.presenceOfElementLocated(ERROR_MAX_FILE_SIZE));
        final WebElement errorToaster = driver.findElement(ERROR_MAX_FILE_SIZE);

        assertThat(errorToaster.getText()).isEqualTo(ERROR_FILE_OVER_5MB);
    }


    /**
     * Test which tries to add more than 4 iamges
     *
     * We try to add more than 4 images, but then we assert if error toast message is displayed with
     * proper test defined.
     */
    @Test
    public void maxAllowedUploadImageNumberExceeded() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(ADVERTISER_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Announcement for update)
        wait.until(ExpectedConditions.urlToBe(ADVERTISER_ANNOUNCEMENT_URL));

        final WebElement updateButton = driver.findElement(UPDATE_BUTTON);
        updateButton.click();

        final WebElement continueButton = driver.findElement(CONTINUE_BUTTON);
        continueButton.click();

        // Second part of adding announcement
        final WebElement dropZone = driver.findElement(IMAGES_DROP_ZONE);
        final WebElement progress = driver.findElement(IMAGES_UPLOAD_PROGRESS);

        assertThat(dropZone).isNotNull();
        assertThat(progress).isNotNull();

        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_1);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_2);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_5);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_4);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_4);
        DragAndDropUtil.dropFile(driver, wait, dropZone, IMAGE_PATH_5);

        wait.until(ExpectedConditions.presenceOfElementLocated(ERROR_MAX_IMAGE_NUMBERS));
        final WebElement errorToaster = driver.findElement(ERROR_MAX_IMAGE_NUMBERS);

        assertThat(errorToaster.getText()).isEqualTo(ERROR_FILE_NUMBER_MSG);
    }

    /**
     * Test which tries to update other user annoucement
     *
     * We check if update button doesn't exist.
     */
    @Test(expected = NoSuchElementException.class)
    public void tryToUpdateNotMyAnnouncement() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(NOT_ADVERTISER_ANNOUNCEMENT_URL);

        // Check if we're on right URL (Announcement for update)
        wait.until(ExpectedConditions.urlToBe(NOT_ADVERTISER_ANNOUNCEMENT_URL));

        driver.findElement(UPDATE_BUTTON);
    }

    /**
     * Test which tries to update other user annoucement directly
     *
     * It's forbidden and we check if we get 'page not found' as answer.
     */
    @Test
    public void tryToUpdateNotMyAnnouncementDirectly() {
        LoginUtil.login(ADVERTISER_WITH_ANNOUNCEMENT_USERNAME, ADVERTISER_WITH_ANNOUNCEMENT_PASSWORD, driver, wait);

        driver.navigate().to(NOT_ADVERTISER_UPDATE_ANNOUNCEMENT_URL);

        wait.until(ExpectedConditions.urlToBe(PAGE_NOT_FOUND_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(PAGE_NOT_FOUND_URL);
    }
}
