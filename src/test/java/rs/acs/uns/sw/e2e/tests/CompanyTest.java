package rs.acs.uns.sw.e2e.tests;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rs.acs.uns.sw.e2e.util.ConfigUtil;
import rs.acs.uns.sw.e2e.util.LoginUtil;
import rs.acs.uns.sw.sct.SctServiceApplication;

import javax.transaction.Transactional;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

import static org.assertj.core.api.Assertions.assertThat;
import static rs.acs.uns.sw.e2e.pages.CompanyPage.*;
import static rs.acs.uns.sw.e2e.util.Constants.WEBDRIVER_TIMEOUT;

/**
 * Company tests.
 */

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
@Transactional
public class CompanyTest {

    private static WebDriver driver;

    // Wait in webdriver until some condition is not satisfied
    private static WebDriverWait wait;

    /**
     *  Creates instace of Chrome Driver
     */
    @BeforeClass
    public static void instanceDriver() {
        ChromeOptions options = ConfigUtil.chromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WEBDRIVER_TIMEOUT);
    }

    /**
     * Send request for company membership
     *
     * We log like advertiser without company, then we show all companies and choose one.
     * Then we click on button 'membership request' and check for message.
     */
    @Test
    public void sendCompanyRequest() {
        loginAsAdvertiserWithoutCompany();

        driver.navigate().to(COMPANIES_URL);

        // Check if we're on right URL (Companies URL)
        wait.until(ExpectedConditions.urlToBe(COMPANIES_URL));

        final WebElement company = driver.findElement(COMPANY_IDENTIFIER);
        company.click();

        wait.until(ExpectedConditions.urlContains(COMPANY_URL));

        final WebElement requestButton = driver.findElement(REQUEST_MEMBERSHIP_BUTTON);
        requestButton.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(REQUEST_SENT_ID));
        final WebElement successToaster = driver.findElement(REQUEST_SENT_ID);

        assertThat(successToaster.getText()).isEqualTo(REQUEST_SENT_MESSAGE);
    }

    /**
     * Send request for company membership on the same company twice
     *
     * We log like advertiser without company, then we show all companies and choose one.
     * We send request and check if button status is disabled for second time.
     */
    @Test
    public void sendCompanyRequestTwice() {
        loginAsAdvertiserWithoutCompany();

        driver.navigate().to(COMPANIES_URL);

        // Check if we're on right URL (Companies URL)
        wait.until(ExpectedConditions.urlToBe(COMPANIES_URL));

        final WebElement company = driver.findElement(COMPANY_IDENTIFIER);
        company.click();

        wait.until(ExpectedConditions.urlContains(COMPANY_URL));

        final WebElement requestButton = driver.findElement(REQUEST_MEMBERSHIP_BUTTON);
        requestButton.click();

        assertThat(requestButton.isDisplayed()).isTrue();
    }

    /**
     * Create new company
     *
     * We log as admin and successful populate data. Then we click on 'Dodaj' button.
     */
    @Test
    @Ignore
    public void createNewCompanySuccessfully() throws AWTException, InterruptedException {
        loginAsAdmin();

        driver.navigate().to(ADD_COMPANY_URL);

        wait.until(ExpectedConditions.urlToBe(ADD_COMPANY_URL));

        final WebElement inputName = driver.findElement(FORM_COMPANY_NAME);
        final WebElement inputAddress = driver.findElement(FORM_COMPANY_ADDRESS);
        final WebElement inputPhone = driver.findElement(FORM_COMPANY_PHONE);
        final WebElement inputUser = driver.findElement(FORM_COMPANY_USER);

        assertThat(inputName).isNotNull();
        assertThat(inputAddress).isNotNull();
        assertThat(inputPhone).isNotNull();
        assertThat(inputUser).isNotNull();

        inputName.sendKeys(COMPANY_NAME_VALUE);
        inputAddress.sendKeys(COMPANY_ADDRESS_VALUE);
        inputPhone.sendKeys(COMPANY_PHONE_VALUE);

        // Press Enter
        inputUser.sendKeys(COMPANY_USER_VALUE);
        inputUser.sendKeys("\n");

        //final WebElement chooseLogoButton = driver.findElement(CHOOSE_LOGO_BUTTON);
        //chooseLogoButton.click();

        StringSelection ss = new StringSelection(COMPANY_LOGO_IMAGE);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

        /*
        Robot robot = new Robot();
        robot.delay(500);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.delay(500);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.delay(500);
        robot.keyRelease(KeyEvent.VK_ENTER);
        */
    }

    /**
     * Create new company without image.
     *
     * We log as admin and populate data except image.
     * Then we check if our 'Dodaj agenciju' button is disabled.
     */
    @Test
    public void createNewCompanyWitoutImage() {
        loginAsAdmin();

        driver.navigate().to(ADD_COMPANY_URL);

        wait.until(ExpectedConditions.urlToBe(ADD_COMPANY_URL));

        final WebElement inputName = driver.findElement(FORM_COMPANY_NAME);
        final WebElement inputAddress = driver.findElement(FORM_COMPANY_ADDRESS);
        final WebElement inputPhone = driver.findElement(FORM_COMPANY_PHONE);
        final WebElement inputUser = driver.findElement(FORM_COMPANY_USER);

        assertThat(inputName).isNotNull();
        assertThat(inputAddress).isNotNull();
        assertThat(inputPhone).isNotNull();
        assertThat(inputUser).isNotNull();

        inputName.sendKeys(COMPANY_NAME_VALUE);
        inputAddress.sendKeys(COMPANY_ADDRESS_VALUE);
        inputPhone.sendKeys(COMPANY_PHONE_VALUE);

        // Press Enter
        inputUser.sendKeys(COMPANY_USER_VALUE);
        inputUser.sendKeys("\n");

        final WebElement createButton = driver.findElement(ADD_COMPANY_BUTTON);

        assertThat(createButton.isEnabled()).isFalse();
    }

    /**
     * Test which checks for required address
     *
     * We log as admin and populate data where we set empty address.
     * Then we check if our 'Dodaj agenciju' button is disabled.
     * Also check if proper message is displayed.
     */
    @Test
    public void checkForRequiredAddressField() {
        loginAsAdmin();

        driver.navigate().to(ADD_COMPANY_URL);

        wait.until(ExpectedConditions.urlToBe(ADD_COMPANY_URL));

        final WebElement inputName = driver.findElement(FORM_COMPANY_NAME);
        final WebElement inputAddress = driver.findElement(FORM_COMPANY_ADDRESS);
        final WebElement inputPhone = driver.findElement(FORM_COMPANY_PHONE);
        final WebElement inputUser = driver.findElement(FORM_COMPANY_USER);

        assertThat(inputName).isNotNull();
        assertThat(inputAddress).isNotNull();
        assertThat(inputPhone).isNotNull();
        assertThat(inputUser).isNotNull();

        inputName.sendKeys(COMPANY_NAME_VALUE);
        inputAddress.sendKeys(COMPANY_ADDRESS_VALUE);
        inputAddress.clear();
        inputPhone.sendKeys(COMPANY_PHONE_VALUE);

        // Press Enter
        inputUser.sendKeys(COMPANY_USER_VALUE);
        inputUser.sendKeys("\n");

        final WebElement createButton = driver.findElement(ADD_COMPANY_BUTTON);

        assertThat(createButton.isEnabled()).isFalse();
        assertThat(driver.findElement(REQUIRED_ADDRESS).getText()).isEqualTo(REQUIRED_ADDRESS_MESSAGE);
    }

    /**
     * Private util method for logging as Admin
     */
    private void loginAsAdmin() {
        LoginUtil.login(ADMIN_USERNAME, ADMIN_PASSWORD, driver, wait);
    }

    /**
     * Private util method for logging as Advertiser without Company
     */
    private void loginAsAdvertiserWithoutCompany() {
        LoginUtil.login(ADVERTISER_USERNAME_WITHOUT_COMPANY, ADVERTISER_PASSWORD_WITHOUT_COMPANY, driver, wait);
    }
}
