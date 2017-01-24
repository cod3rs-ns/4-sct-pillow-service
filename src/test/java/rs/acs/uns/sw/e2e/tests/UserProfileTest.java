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
import rs.acs.uns.sw.e2e.util.LoginUtil;
import rs.acs.uns.sw.sct.SctServiceApplication;

import javax.transaction.Transactional;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static rs.acs.uns.sw.e2e.pages.SigningPage.*;
import static rs.acs.uns.sw.e2e.pages.UserProfile.*;
import static rs.acs.uns.sw.e2e.util.Constants.WEB_DRIVER_TIMEOUT;

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
@Transactional
public class UserProfileTest {
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

    /**
     * Util login method
     *
     * @param username user username
     * @param password user password
     */
    private void login(String username, String password) {
        LoginUtil.login(username, password, driver, wait);
    }


    /**
     * Test editing verifier personal information
     * <p>
     * First we need to log in as verifier. Then we navigate to profile
     * page and properly update his/her personal information.
     * Expectation: After updating we expect that previous information
     * on user profile are replaced with new one.
     */
    @Test
    public void editVerifierProfile() {
        login(VERIFIER_USERNAME, DEFAULT_PASSWORD);

        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");

        wait.until(urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));

        wait.until(visibilityOfElementLocated(EDIT_BUTTON));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        wait.until(visibilityOfElementLocated(EDIT_FIRST_NAME));
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
        wait.until(visibilityOfElementLocated(SUCCESS_UPDATED_INF));
        driver.findElement(SUCCESS_UPDATED_INF).click();

        final WebElement displayedName = driver.findElement(DISPLAYED_NAME);
        final WebElement displayedPhone = driver.findElement(DISPLAYED_PHONE);

        assertThat(displayedName.getText()).isEqualTo(UPDATED_FIRST_NAME + " " + UPDATED_LAST_NAME);
        assertThat(displayedPhone.getText().trim()).isEqualTo(UPDATED_PHONE);
    }

    /**
     * Test cancel editing verifier personal information
     * <p>
     * First we need to log in as verifier. Then we navigate to profile page and properly
     * update his/her personal information. Finally, we give up from changing information form.
     * Expectation: We expect that previous information are not replaced with new one.
     */
    @Test
    public void cancelEditingProfile() {
        login(VERIFIER_USERNAME, DEFAULT_PASSWORD);

        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");

        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));

        final WebElement displayedName = driver.findElement(DISPLAYED_NAME);
        final WebElement displayedPhone = driver.findElement(DISPLAYED_PHONE);

        final String previousName = displayedName.getText().trim();
        final String previousPhone = displayedPhone.getText().trim();

        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

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

    /**
     * Test editing verifier personal information without setting first name
     * <p>
     * First we need to log in as verifier. Then we navigate to profile page and try
     * to update personal information with setting first name to be empty.
     * Expectation: We expect error message to be present and save button to be disabled.
     */
    @Test
    public void editFirstNameRequired() {
        login(VERIFIER_USERNAME, DEFAULT_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        final WebElement registrationForm = driver.findElement(FORM_EDIT_PERSONAL_DATA);
        final String xpath = String.format(X_PATH_ERROR_MESSAGE_P, ValidationMessages.FIRST_NAME_REQUIRED);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputFirstName = driver.findElement(EDIT_FIRST_NAME);
        inputFirstName.clear();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        assertThat(errorMessage.isDisplayed()).isTrue();

        final WebElement buttonSave = driver.findElement(SAVE_EDIT_BUTTON);
        assertThat(buttonSave.isEnabled()).isFalse();
    }

    /**
     * Test editing verifier personal information without setting last name
     * <p>
     * First we need to log in as verifier. Then we navigate to profile page and try
     * to update personal information with setting last name to be empty.
     * Expectation: We expect error message to be present and save button to be disabled.
     */
    @Test
    public void editLastNameRequired() {
        login(VERIFIER_USERNAME, DEFAULT_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        final WebElement registrationForm = driver.findElement(FORM_EDIT_PERSONAL_DATA);
        final String xpath = String.format(X_PATH_ERROR_MESSAGE_P, ValidationMessages.LAST_NAME_REQUIRED);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        final WebElement inputLastName = driver.findElement(EDIT_LAST_NAME);
        inputLastName.clear();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        assertThat(errorMessage.isDisplayed()).isTrue();

        final WebElement buttonSave = driver.findElement(SAVE_EDIT_BUTTON);
        assertThat(buttonSave.isEnabled()).isFalse();
    }

    /**
     * Test editing verifier personal information without setting phone
     * <p>
     * First we need to log in as verifier. Then we navigate to profile page and try
     * to update personal information with setting phone field to be empty.
     * Expectation: We expect error message to be present and save button to be disabled.
     */
    @Test
    public void editPhoneRequired() {
        login(VERIFIER_USERNAME, DEFAULT_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final WebElement buttonEdit = driver.findElement(EDIT_BUTTON);
        buttonEdit.click();

        final WebElement registrationForm = driver.findElement(FORM_EDIT_PERSONAL_DATA);
        final String xpath = String.format(X_PATH_ERROR_MESSAGE_SPAN, ValidationMessages.PHONE_REQUIRED);
        final WebElement errorMessage = registrationForm.findElement(By.xpath(xpath));
        assertThat(errorMessage.isDisplayed()).isFalse();

        wait.until(visibilityOfElementLocated(EDIT_PHONE));
        final WebElement inputPhone = driver.findElement(EDIT_PHONE);
        inputPhone.clear();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        assertThat(errorMessage.isDisplayed()).isTrue();

        final WebElement buttonSave = driver.findElement(SAVE_EDIT_BUTTON);
        assertThat(buttonSave.isEnabled()).isFalse();
    }

    /**
     * Test changing profile image picture
     * <p>
     * First we need to log in as verifier. Then we navigate to
     * profile page and try to upload new profile picture.
     * Expectation: We expect success message to be shown.
     */
    @Test
    public void changeProfileImage() throws AWTException {
        login(VERIFIER_USERNAME, DEFAULT_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + VERIFIER_USERNAME + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + VERIFIER_USERNAME + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final WebElement buttonImageChange = driver.findElement(CHANGE_PROFILE_PICTURE_BUTTON);
        buttonImageChange.click();

        File file = new File(TEST_IMG_PATH);

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

        wait.until(visibilityOfElementLocated(IMAGE_MSG_SUCCESS));
        WebElement imgSuccess = driver.findElement(IMAGE_MSG_SUCCESS);
        assertThat(imgSuccess.isDisplayed()).isTrue();
        imgSuccess.click();
    }

    /**
     * Test changing user password
     * <p>
     * First we need to log in. Then we navigate to profile page and
     * set new password. After that we log in with new password.
     * Expectation: We expect success message to be shown
     * and to successfully log in with new pass.
     */
    @Test
    public void changeUserPassword() {
        login(USERNAME_FOR_PASS_CHANGING, DEFAULT_PASSWORD);
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
        wait.until(visibilityOfElementLocated(CHANGE_PASSWORD_SUCCESS_MESSAGE));

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

    /**
     * Test extending expiration date
     * <p>
     * First we need to log in. Then we navigate to profile page and
     * extend date of announcement that user created before.
     * Expectation: We expect success message to be shown and
     * displayed date to be updated.
     */
    @Test
    public void extendExpirationDate() {
        login(USERNAME_FOR_EXTENDING_DATE, DEFAULT_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + USERNAME_FOR_EXTENDING_DATE + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + USERNAME_FOR_EXTENDING_DATE + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final String xpath = String.format(X_PATH_ANNOUNCEMENT_FRAGMENT, ANNOUNCEMENT_LINK + ANNOUNCEMENT_ID);
        wait.until(presenceOfElementLocated(By.xpath(xpath)));
        final WebElement annFragment = driver.findElement(By.xpath(xpath));

        final WebElement inputDate = annFragment.findElement(EXTENDED_DATE);
        inputDate.clear();
        inputDate.sendKeys(UPDATED_EXTENDED_DATE);

        final WebElement btnExtendingDate = annFragment.findElement(EXTENDED_DATE_BTN);
        btnExtendingDate.click();

        wait.until(visibilityOfElementLocated(EXTENDED_DATE_SUCCESS_MSG));
        final WebElement successMessage = driver.findElement(EXTENDED_DATE_SUCCESS_MSG);
        assertThat(successMessage.getText()).isEqualTo(String.format(DATE_EXPIRATION_SUCCESS_MESSAGE_CONTENT, UPDATED_EXTENDED_DATE));
        successMessage.click();
    }


    /**
     * Test extending expiration date before today
     * <p>
     * First we need to log in. Then we navigate to profile page and
     * try to extend date of announcement that is before today.
     * Expectation: We expect error message to be shown
     */
    @Test
    public void expirationDateBeforeToday() {
        login(USERNAME_FOR_EXTENDING_DATE, DEFAULT_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + USERNAME_FOR_EXTENDING_DATE + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + USERNAME_FOR_EXTENDING_DATE + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final String xpath = String.format(X_PATH_ANNOUNCEMENT_FRAGMENT, ANNOUNCEMENT_LINK + ANNOUNCEMENT_ID);
        wait.until(presenceOfElementLocated(By.xpath(xpath)));
        final WebElement annFragment = driver.findElement(By.xpath(xpath));

        final WebElement inputDate = annFragment.findElement(EXTENDED_DATE);
        inputDate.clear();
        inputDate.sendKeys(WRONG_EXTENDED_DATE);

        final WebElement btnExtendingDate = annFragment.findElement(EXTENDED_DATE_BTN);
        btnExtendingDate.click();

        wait.until(visibilityOfElementLocated(EXTENDED_DATE_ERROR_MSG));
        final WebElement errorMessage = driver.findElement(EXTENDED_DATE_ERROR_MSG);
        assertThat(errorMessage.getText()).isEqualTo(DATE_EXPIRATION_ERROR_MESSAGE_CONTENT);
        errorMessage.click();
    }

    /**
     * Test accepting membership request
     * <p>
     * First we need to log in. Then we navigate to profile page and
     * try to accept one user's request for company membership.
     * Expectation: We expect that success message has shown such as
     * company link on user's profile
     */
    @Test
    public void acceptCompanyMembershipRequest() {
        login(USERNAME_FOR_EXTENDING_DATE, DEFAULT_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + USERNAME_FOR_EXTENDING_DATE + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + USERNAME_FOR_EXTENDING_DATE + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final WebElement membershipRequestsTab = driver.findElement(MEMBERSHIP_REQUESTS_TAB);
        membershipRequestsTab.click();

        final String xpath = String.format(X_PATH_USER_MEM_REQ_FRAGMENT, USER_MEMBERSHIP_LINK_ACCEPT);
        wait.until(presenceOfElementLocated(By.xpath(xpath)));
        final WebElement userFragment = driver.findElement(By.xpath(xpath));

        final WebElement acceptBtn = userFragment.findElement(ACCEPT_REQUEST_BTN);
        acceptBtn.click();

        wait.until(invisibilityOfElementLocated(By.xpath(xpath)));
        wait.until(visibilityOfElementLocated(MEM_REQ_ACCEPTED));
        driver.findElement(MEM_REQ_ACCEPTED).click();

        final String userProfileUrl = URL_PREFIX + USER_MEMBERSHIP_LINK_ACCEPT.substring(0, USER_MEMBERSHIP_LINK_ACCEPT.lastIndexOf("/") + 1);
        driver.navigate().to(userProfileUrl);
        wait.until(ExpectedConditions.urlToBe(userProfileUrl));

        final WebElement userCompany = driver.findElement(USER_COMPANY);
        assertThat(userCompany.getAttribute("href")).containsIgnoringCase(COMPANY_MEMBERS_LINK);
    }

    /**
     * Test rejecting membership request
     * <p>
     * First we need to log in. Then we navigate to profile page and
     * try to reject one user's request for company membership.
     * Expectation: We expect that success message is shown
     */
    @Test
    public void denyCompanyMembershipRequest() {
        login(USERNAME_FOR_EXTENDING_DATE, DEFAULT_PASSWORD);
        driver.navigate().to(USER_PROFILE_URL + USERNAME_FOR_EXTENDING_DATE + "/");
        wait.until(ExpectedConditions.urlToBe(USER_PROFILE_URL + USERNAME_FOR_EXTENDING_DATE + "/"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(EDIT_BUTTON));

        final WebElement membershipRequestsTab = driver.findElement(MEMBERSHIP_REQUESTS_TAB);
        membershipRequestsTab.click();

        final String xpath = String.format(X_PATH_USER_MEM_REQ_FRAGMENT, USER_MEMBERSHIP_LINK_DENY);
        wait.until(presenceOfElementLocated(By.xpath(xpath)));
        final WebElement userFragment = driver.findElement(By.xpath(xpath));

        final WebElement denyBtn = userFragment.findElement(DENY_REQUEST_BTN);
        denyBtn.click();

        wait.until(invisibilityOfElementLocated(By.xpath(xpath)));
        wait.until(visibilityOfElementLocated(MEM_REQ_REJECTED));
        driver.findElement(MEM_REQ_REJECTED).click();
    }
}
