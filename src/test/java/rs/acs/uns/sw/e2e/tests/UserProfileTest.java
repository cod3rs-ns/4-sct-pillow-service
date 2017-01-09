package rs.acs.uns.sw.e2e.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rs.acs.uns.sw.e2e.util.ConfigUtil;
import rs.acs.uns.sw.sct.SctServiceApplication;

import javax.transaction.Transactional;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static rs.acs.uns.sw.e2e.pages.HomePage.HOME_URL;
import static rs.acs.uns.sw.e2e.pages.SigningPage.*;
import static rs.acs.uns.sw.e2e.pages.UserProfile.*;
import static rs.acs.uns.sw.e2e.util.Constants.WEBDRIVER_TIMEOUT;

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
@Transactional
public class UserProfileTest {
    private static WebDriver driver;

    // Wait in webdriver until some condition is not satisfied
    private static WebDriverWait wait;

    @BeforeClass
    public static void instanceDriver() {
        ChromeOptions options = ConfigUtil.chromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WEBDRIVER_TIMEOUT);
    }

    @Before
    public void openBrowser() {
        driver.get(SIGNING_URL);
    }

    public void login(String username, String password) {
        // Input Username
        final WebElement inputUsername = driver.findElement(TEXTBOX_USERNAME);
        inputUsername.sendKeys(username);

        // Input Password
        final WebElement inputPassword = driver.findElement(TEXTBOX_PASSWORD);
        inputPassword.sendKeys(password);

        // Click on 'Uloguj se' button.
        final WebElement buttonSubmit = driver.findElement(BUTTON_LOGIN);
        buttonSubmit.click();

        wait.until(ExpectedConditions.urlToBe(HOME_URL));
    }

    @Test
    public void editVerifierProfile() {
        login(VERIFIER_USERNAME, VERIFIER_PASSWORD);

        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");

        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));

        wait.until(ExpectedConditions.visibilityOf(driver.findElement(EDIT_BUTTON)));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        final WebElement inputFirstName = driver.findElement(EDIT_FIRST_NAME);
        inputFirstName.clear();
        inputFirstName.sendKeys(UPDATED_FIRST_NAME);

        final WebElement inputLastName = driver.findElement(EDIT_LAST_NAME);
        inputLastName.clear();
        inputLastName.sendKeys(UPDATED_LAST_NAME);

        final WebElement inputPhone = driver.findElement(EDIT_PHONE);
        inputPhone.clear();
        inputPhone.sendKeys(UPDATED_PHONE);

        final WebElement buttonSave = driver.findElement(SAVE_EDIT_BUTTON);
        buttonSave.click();

        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));

        final WebElement displayedName = driver.findElement(DISPLAYED_NAME);
        final WebElement displayedPhone = driver.findElement(DISPLAYED_PHONE);

        assertThat(displayedName.getText()).isEqualTo(UPDATED_FIRST_NAME + " " + UPDATED_LAST_NAME);
        assertThat(displayedPhone.getText().trim()).isEqualTo(UPDATED_PHONE);
    }

    @Test
    public void cancelEditingProfile() {
        login(VERIFIER_USERNAME, VERIFIER_PASSWORD);

        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");

        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));

        final WebElement displayedName = driver.findElement(DISPLAYED_NAME);
        final WebElement displayedPhone = driver.findElement(DISPLAYED_PHONE);

        final String previousName = displayedName.getText().trim();
        final String previousPhone = displayedPhone.getText().trim();

        wait.until(ExpectedConditions.visibilityOf(driver.findElement(EDIT_BUTTON)));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        final WebElement inputFirstName = driver.findElement(EDIT_FIRST_NAME);
        inputFirstName.clear();
        inputFirstName.sendKeys(Long.toHexString(Double.doubleToLongBits(Math.random())));

        final WebElement inputLastName = driver.findElement(EDIT_LAST_NAME);
        inputLastName.clear();
        inputLastName.sendKeys(Long.toHexString(Double.doubleToLongBits(Math.random())));

        final WebElement inputPhone = driver.findElement(EDIT_PHONE);
        inputPhone.clear();
        inputPhone.sendKeys(Long.toHexString(Double.doubleToLongBits(Math.random())));

        final WebElement buttonCancel = driver.findElement(CANCEL_EDIT_BUTTON);
        buttonCancel.click();

        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));

        assertThat(displayedName.getText()).isEqualTo(previousName);
        assertThat(displayedPhone.getText().trim()).isEqualTo(previousPhone);
    }

    @Test
    public void editFirstNameRequired() {
        login(VERIFIER_USERNAME, VERIFIER_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(EDIT_BUTTON)));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        final WebElement registrationForm = driver.findElement(FORM_EDIT_PERSONAL_DATA);
        final String xpath = String.format(X_PATH_ERROR_MESSAGE_P, ValidationMessages.FIRST_NAME_REQUIRED);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputFirstName = driver.findElement(EDIT_FIRST_NAME);
        inputFirstName.clear();

        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        assertThat(errorMessage.isDisplayed()).isTrue();
    }

    @Test
    public void editLastNameRequired() {
        login(VERIFIER_USERNAME, VERIFIER_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(EDIT_BUTTON)));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        final WebElement registrationForm = driver.findElement(FORM_EDIT_PERSONAL_DATA);
        final String xpath = String.format(X_PATH_ERROR_MESSAGE_P, ValidationMessages.LAST_NAME_REQUIRED);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputLastName = driver.findElement(EDIT_LAST_NAME);
        inputLastName.clear();

        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        assertThat(errorMessage.isDisplayed()).isTrue();
    }

    @Test
    public void editPhoneRequired() {
        login(VERIFIER_USERNAME, VERIFIER_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(EDIT_BUTTON)));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        final WebElement registrationForm = driver.findElement(FORM_EDIT_PERSONAL_DATA);
        final String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.PHONE_REQUIRED);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputPhone = driver.findElement(EDIT_PHONE);
        inputPhone.clear();

        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        assertThat(errorMessage.isDisplayed()).isTrue();
    }

    @Test
    public void changeProfileImage() throws AWTException {
        login(VERIFIER_USERNAME, VERIFIER_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(EDIT_BUTTON)));

        final WebElement buttonImageChange = driver.findElement(CHANGE_PROFILE_PICTURE_BUTTON);
        buttonImageChange.click();

        File file = new File("./src/test/resources/test_upload.jpg");

        StringSelection ss = new StringSelection(file.getAbsolutePath());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);

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
    }

    @Test
    public void changeUserPassword() throws AWTException {
        login(USERNAME_FOR_PASS_CHANGING, VERIFIER_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + USERNAME_FOR_PASS_CHANGING + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + USERNAME_FOR_PASS_CHANGING + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final WebElement buttonPassChange = driver.findElement(CHANGE_PASSWORD_BUTTON);
        buttonPassChange.click();

        final WebElement inputPassword = driver.findElement(EDIT_PASSWORD);
        inputPassword.clear();
        inputPassword.sendKeys(UPDATED_PASSWORD);

        final WebElement inputRetypedPassword = driver.findElement(EDIT_RE_PASSWORD);
        inputRetypedPassword.clear();
        inputRetypedPassword.sendKeys(UPDATED_PASSWORD);

        wait.until(elementToBeClickable(SAVE_PASS_BUTTON));

        final WebElement savePassButton = driver.findElement(SAVE_PASS_BUTTON);
        savePassButton.click();

        wait.until(presenceOfElementLocated(CHANGE_PASSWORD_SUCCESS_MESSAGE));

        final WebElement successNotification = driver.findElement(CHANGE_PASSWORD_SUCCESS_MESSAGE);
        successNotification.click();

        wait.until(invisibilityOfElementLocated(CHANGE_PASSWORD_SUCCESS_MESSAGE));

        final WebElement linkUserMenu = driver.findElement(USER_MENU);
        linkUserMenu.click();

        wait.until(visibilityOfElementLocated(LOGOUT_LINK));
        final WebElement logoutLink = driver.findElement(LOGOUT_LINK);
        logoutLink.click();

        wait.until(ExpectedConditions.urlToBe(SIGNING_URL));
        login(USERNAME_FOR_PASS_CHANGING, UPDATED_PASSWORD);
    }
}
