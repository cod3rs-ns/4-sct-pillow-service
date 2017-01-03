package rs.acs.uns.sw.e2e.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static rs.acs.uns.sw.e2e.pages.HomePage.HOME_URL;
import static rs.acs.uns.sw.e2e.pages.SigningPage.*;
import static rs.acs.uns.sw.e2e.util.Constants.*;

/**
 * Signing tests.
 */
public class SigningTest {

    private static WebDriver driver;

    // Wait in webdriver until some condition is not satisfied
    private static WebDriverWait wait;

    @BeforeClass
    public static void instanceDriver() {
        File file = new File(WEBDRIVER_PATH);
        System.setProperty(WEBDRIVER_NAME, file.getAbsolutePath());

        final String os = System.getProperty("os.name");

        final ChromeOptions options = new ChromeOptions();
        options.addArguments(("Mac OS X".equals(os)) ? MAXIMIZE_OSX : MAXIMIZE_WIN);

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WEBDRIVER_TIMEOUT);
    }

    @Before
    public void openBrowser() {
        driver.get(SIGNING_URL);
    }

    @Test
    public void isSigningPageProperlyOpened() {
        // Check if driver's URL is equal to wanted URL
        assertThat(driver.getCurrentUrl()).isEqualTo(SIGNING_URL);

        // Check if page contains all components required for login feature
        assertThat(driver.findElement(TEXTBOX_USERNAME)).isNotNull();
        assertThat(driver.findElement(TEXTBOX_PASSWORD)).isNotNull();
        assertThat(driver.findElement(BUTTON_LOGIN)).isNotNull();
    }

    @Test
    public void loginSuccessAsAdmin() {
        // Input Username
        final WebElement inputUsername = driver.findElement(TEXTBOX_USERNAME);
        inputUsername.sendKeys(ADMIN_USERNAME);

        // Input Password
        final WebElement inputPassword = driver.findElement(TEXTBOX_PASSWORD);
        inputPassword.sendKeys(ADMIN_PASSWORD);

        // Click on 'Uloguj se' button.
        final WebElement buttonSubmit = driver.findElement(BUTTON_LOGIN);
        buttonSubmit.click();

        // Wait until new page is loaded
        wait.until(ExpectedConditions.urlToBe(HOME_URL));

        assertThat(driver.getCurrentUrl()).isEqualTo(HOME_URL);

        assertThat(driver.findElements(LOGGED_USER_NAVBAR)).isNotNull();
        assertThat(driver.findElements(LOGGED_USER_NAVBAR).get(0).getText()).isEqualTo(loggedUserFormatter(ADMIN_USERNAME));
    }

    @Test
    public void loginFailed() {
        // Input Username
        final WebElement inputUsername = driver.findElement(TEXTBOX_USERNAME);
        inputUsername.sendKeys(WRONG_USERNAME);

        // Input Password
        final WebElement inputPassword = driver.findElement(TEXTBOX_PASSWORD);
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

    private String loggedUserFormatter(String username) {
        return String.format("Ulogovan kao %s (admin)", username);
    }
}
