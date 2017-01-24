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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rs.acs.uns.sw.e2e.util.ConfigUtil;
import rs.acs.uns.sw.e2e.util.MailUtil;
import rs.acs.uns.sw.sct.SctServiceApplication;
import rs.acs.uns.sw.sct.users.User;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.awt.*;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static rs.acs.uns.sw.e2e.pages.HomePage.HOME_URL;
import static rs.acs.uns.sw.e2e.pages.HomePage.VERIFICATION_SUCCESS_URL;
import static rs.acs.uns.sw.e2e.pages.SigningPage.*;
import static rs.acs.uns.sw.e2e.util.Constants.WEB_DRIVER_TIMEOUT;

/**
 * Signing tests.
 */

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
@Transactional
public class SigningTest {

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
        driver.get(SIGNING_URL);
    }

    private static User createDefaultUser() {
        return new User()
                .username(REGISTRATION_USERNAME)
                .firstName(REGISTRATION_FIRST_NAME)
                .lastName(REGISTRATION_LAST_NAME)
                .email(REGISTRATION_EMAIL)
                .password(REGISTRATION_PASSWORD)
                .type(REGISTRATION_TYPE)
                .phoneNumber(REGISTRATION_PHONE);
    }

    /**
     * Util method for filling registration form
     *
     * @param user information that will be filled in form
     */
    private static void fillRegistrationForm(User user) {
        // Input username
        final WebElement inputUsername = driver.findElement(INPUT_USERNAME);
        inputUsername.sendKeys(user.getUsername());

        // Input password
        final WebElement inputPassword = driver.findElement(INPUT_PASSWORD);
        inputPassword.sendKeys(user.getPassword());

        // Input retyped password
        final WebElement inputRetypedPassword = driver.findElement(INPUT_RE_PASSWORD);
        inputRetypedPassword.sendKeys(user.getPassword());

        // Input first name
        final WebElement inputFirstName = driver.findElement(INPUT_FIRST_NAME);
        inputFirstName.sendKeys(user.getFirstName());

        // Input last name
        final WebElement inputLastName = driver.findElement(INPUT_LAST_NAME);
        inputLastName.sendKeys(user.getLastName());

        // Input type
        final WebElement inputType = driver.findElement(INPUT_TYPE);
        inputType.sendKeys(user.getType());

        // Input email
        final WebElement inputEmail = driver.findElement(INPUT_EMAIL);
        inputEmail.sendKeys(user.getEmail());

        // Input phone
        final WebElement inputPhone = driver.findElement(INPUT_PHONE);
        inputPhone.sendKeys(user.getPhoneNumber());
    }

    /**
     * Test login
     * <p>
     * We navigate to signing page.
     * Expectation: Expect all fields are present.
     */
    @Test
    public void isSigningPageProperlyOpened() {
        // Check if driver's URL is equal to wanted URL
        assertThat(driver.getCurrentUrl()).isEqualTo(SIGNING_URL);

        // Check if page contains all components required for login feature
        assertThat(driver.findElement(TEXT_BOX_USERNAME)).isNotNull();
        assertThat(driver.findElement(TEXT_BOX_PASSWORD)).isNotNull();
        assertThat(driver.findElement(BUTTON_LOGIN)).isNotNull();
    }

    /**
     * Test login successfully
     * <p>
     * First we navigate to signing page. Then we fill login form with admin credentials.
     * Expectation: User's name and role has shown in navigation bar.
     */
    @Test
    public void loginSuccessAsAdmin() {
        // Input Username
        final WebElement inputUsername = driver.findElement(TEXT_BOX_USERNAME);
        inputUsername.sendKeys(ADMIN_USERNAME);

        // Input Password
        final WebElement inputPassword = driver.findElement(TEXT_BOX_PASSWORD);
        inputPassword.sendKeys(ADMIN_PASSWORD);

        // Click on 'Uloguj se' button.
        final WebElement buttonSubmit = driver.findElement(BUTTON_LOGIN);
        buttonSubmit.click();

        // Wait until new page is loaded
        wait.until(ExpectedConditions.urlToBe(HOME_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(HOME_URL);

        assertThat(driver.findElements(LOGGED_USER_NAV_BAR)).isNotNull();
        assertThat(driver.findElements(LOGGED_USER_NAV_BAR).get(0).getText()).isEqualTo(loggedUserFormatter(ADMIN_USERNAME, "administrator"));
    }

    /**
     * Test login with wrong credentials
     * <p>
     * First we navigate to signing page. Then we improperly fill login form.
     * Expectation: Message about wrong credentials has shown.
     */
    @Test
    public void loginFailed() {
        // Input Username
        final WebElement inputUsername = driver.findElement(TEXT_BOX_USERNAME);
        inputUsername.sendKeys(WRONG_USERNAME);

        // Input Password
        final WebElement inputPassword = driver.findElement(TEXT_BOX_PASSWORD);
        inputPassword.sendKeys(WRONG_PASSWORD);

        // Click on 'Uloguj se' button.
        final WebElement buttonSubmit = driver.findElement(BUTTON_LOGIN);
        buttonSubmit.click();

        // Wait for Wrong Login Dialog to be displayed
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(WRONG_LOGIN_DIALOG)));

        // Check that URL is same and wrong login dialog has appeared
        assertThat(driver.getCurrentUrl()).isEqualTo(SIGNING_URL);
        assertThat(driver.findElements(WRONG_LOGIN_DIALOG)).hasSize(1);

        final WebElement errorDialog = driver.findElements(WRONG_LOGIN_DIALOG).get(0);
        assertThat(errorDialog.getText()).contains(WRONG_LOGIN_DIALOG_MESSAGE);

        assertThat(inputUsername.getAttribute("value")).isEqualTo(WRONG_USERNAME);
        assertThat(inputPassword.getAttribute("value")).isEqualTo("");
    }

    /**
     * Test registering new user successfully
     * <p>
     * First we navigate to signing page. Then we properly fill registration form
     * and after that click on register button.
     * Expectation: Success message to be shown
     */
    @Test
    public void registerSuccessfully() {
        User user = createDefaultUser();
        user.username(NEW_USERNAME);
        user.email(NEW_EMAIL);

        fillRegistrationForm(user);

        // Click on 'Registration' button.
        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isTrue();

        buttonSubmit.click();

        wait.until(visibilityOfElementLocated(SUCCESS_REGISTRATION_MSG));
        driver.findElement(SUCCESS_REGISTRATION_MSG).click();
    }

    /**
     * Test registering new user with username that already exists.
     * <p>
     * First we navigate to signing page. Then we try to register
     * new user with username that already exists.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void usernameExist() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);
        String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.USERNAME_EXISTS);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));

        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputUsername = driver.findElement(INPUT_USERNAME);
        inputUsername.sendKeys(USERNAME_IN_USE);

        // Wait until error message appear
        wait.until(ExpectedConditions.visibilityOf(errorMessage));

        assertThat(errorMessage.isDisplayed()).isTrue();
        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user with email that already exists.
     * <p>
     * First we navigate to signing page. Then we try to register
     * new user with email that already exists.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void emailExist() throws InterruptedException, AWTException {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);
        final WebElement errorMessage = registrationForm.findElement(EMAIL_EXISTS);

        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputEmail = driver.findElement(INPUT_EMAIL);
        inputEmail.sendKeys(EMAIL_IN_USE);

        // Wait until error message appear
        wait.until(visibilityOfElementLocated(EMAIL_EXISTS));
        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user with wrong format of email.
     * <p>
     * First we navigate to signing page. Then we try to register
     * new user with wrong format of email.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void wrongEmailFormat() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);
        String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.WRONG_EMAIL);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));

        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputEmail = driver.findElement(INPUT_EMAIL);
        inputEmail.sendKeys(WRONG_EMAIL);

        // Wait until error message appear
        wait.until(ExpectedConditions.visibilityOf(errorMessage));

        assertThat(errorMessage.isDisplayed()).isTrue();
        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user with weak password.
     * <p>
     * First we navigate to signing page. Then we try to register
     * new user with weak password (less than 6 characters).
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void weakPassword() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);
        String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.WEAK_PASSWORD);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));

        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputPassword = driver.findElement(INPUT_PASSWORD);
        inputPassword.sendKeys(WEAK_PASSWORD);

        // Wait until error message appear
        wait.until(ExpectedConditions.visibilityOf(errorMessage));

        assertThat(errorMessage.isDisplayed()).isTrue();
        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user when passwords does not match.
     * <p>
     * First we navigate to signing page. Then we try to register
     * new user when passwords does not match.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void passwordDoesNotMatch() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);
        final String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.PASSWORD_DOES_NOT_MATCH);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));

        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputPassword = driver.findElement(INPUT_PASSWORD);
        inputPassword.sendKeys(REGISTRATION_PASSWORD);

        final WebElement inputRetypedPassword = driver.findElement(INPUT_RE_PASSWORD);
        inputRetypedPassword.sendKeys(RETYPED_WRONG_PASSWORD);

        // Wait until error message appear
        wait.until(ExpectedConditions.visibilityOf(errorMessage));

        assertThat(errorMessage.isDisplayed()).isTrue();
        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user without setting username.
     * <p>
     * First we navigate to signing page. Then we try to
     * register new user without setting username.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void usernameRequired() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);
        final String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.USERNAME_REQUIRED);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        // Input username
        final WebElement inputUsername = driver.findElement(INPUT_USERNAME);
        inputUsername.sendKeys(REGISTRATION_USERNAME);
        inputUsername.clear();

        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        assertThat(errorMessage.isDisplayed()).isTrue();

        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user without setting password.
     * <p>
     * First we navigate to signing page. Then we try to
     * register new user without setting password.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void passwordRequired() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);

        String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.PASSWORD_REQUIRED);
        WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        // Input password
        final WebElement inputPassword = driver.findElement(INPUT_PASSWORD);
        inputPassword.sendKeys(REGISTRATION_PASSWORD);
        inputPassword.clear();

        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        assertThat(errorMessage.isDisplayed()).isTrue();


        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user without setting name.
     * <p>
     * First we navigate to signing page. Then we try to
     * register new user without setting name.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void firstNameRequired() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);

        String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.FIRST_NAME_REQUIRED);
        WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        // Input first name
        final WebElement inputFirstName = driver.findElement(INPUT_FIRST_NAME);
        inputFirstName.sendKeys(REGISTRATION_FIRST_NAME);
        inputFirstName.clear();

        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        assertThat(errorMessage.isDisplayed()).isTrue();

        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user without setting last name.
     * <p>
     * First we navigate to signing page. Then we try to
     * register new user without setting last name.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void lastNameRequired() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);

        String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.LAST_NAME_REQUIRED);
        WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        // Input last name
        final WebElement inputLastName = driver.findElement(INPUT_LAST_NAME);
        inputLastName.sendKeys(REGISTRATION_LAST_NAME);
        inputLastName.clear();

        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        assertThat(errorMessage.isDisplayed()).isTrue();

        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user without setting phone.
     * <p>
     * First we navigate to signing page. Then we try to
     * register new user without setting phone number.
     * Expectation: Error message to be shown and register button to be disabled
     */
    @Test
    public void phoneRequired() {
        final WebElement registrationForm = driver.findElement(FORM_REGISTER);

        String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.PHONE_REQUIRED);
        WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        // Input phone
        final WebElement inputPhone = driver.findElement(INPUT_PHONE);
        inputPhone.sendKeys(REGISTRATION_PHONE);
        inputPhone.clear();

        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        assertThat(errorMessage.isDisplayed()).isTrue();

        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isFalse();
    }

    /**
     * Test registering new user and verifying its account via mail.
     * <p>
     * First we navigate to signing page. Then we register properly and wait until mail
     * is sent for verification. Then we extract token from mail and verify account.
     * Expectation: User can login successfully.
     */
    @Test
    public void registerWithToken() throws IOException, MessagingException {
        User user = createDefaultUser();
        fillRegistrationForm(user);

        // Click on 'Registration' button.
        final WebElement buttonSubmit = driver.findElement(BUTTON_REGISTER);
        assertThat(buttonSubmit.isEnabled()).isTrue();

        buttonSubmit.click();

        // Wait until verification email is sent
        wait.until(ExpectedConditions.presenceOfElementLocated(EMAIL_SUCCESS_MESSAGE));

        // Extract verification token link from email
        final String link = MailUtil.getMailLink();

        driver.navigate().to(link);

        // Wait until new page is loaded
        wait.until(ExpectedConditions.urlToBe(VERIFICATION_SUCCESS_URL));

        driver.navigate().to(SIGNING_URL);
        // Wait until new page is loaded
        wait.until(ExpectedConditions.urlToBe(SIGNING_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(SIGNING_URL);

        // Input Username
        final WebElement inputUsername = driver.findElement(TEXT_BOX_USERNAME);
        inputUsername.sendKeys(REGISTRATION_USERNAME);

        // Input Password
        final WebElement inputPassword = driver.findElement(TEXT_BOX_PASSWORD);
        inputPassword.sendKeys(REGISTRATION_PASSWORD);

        // Click on login button.
        final WebElement loginBtn = driver.findElement(BUTTON_LOGIN);
        loginBtn.click();

        wait.until(ExpectedConditions.urlToBe(HOME_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(HOME_URL);

        assertThat(driver.findElements(LOGGED_USER_NAV_BAR)).isNotNull();
        assertThat(driver.findElements(LOGGED_USER_NAV_BAR).get(0).getText()).isEqualTo(loggedUserFormatter(REGISTRATION_USERNAME, REGISTRATION_TYPE));
    }

    /**
     * Util method for formatting user name in navigation bar
     *
     * @param username user's username
     * @param role     user's role
     * @return formatted string with username and role
     */
    private String loggedUserFormatter(String username, String role) {
        return String.format("Ulogovan kao %s (%s)", username, role);
    }
}
