package rs.acs.uns.sw.e2e.tests;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import rs.acs.uns.sw.e2e.util.ConfigUtil;
import rs.acs.uns.sw.e2e.util.LoginUtil;
import rs.acs.uns.sw.sct.SctServiceApplication;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static rs.acs.uns.sw.e2e.pages.AnnouncementPage.*;
import static rs.acs.uns.sw.e2e.pages.SigningPage.SIGNING_URL;
import static rs.acs.uns.sw.e2e.util.ConditionUtil.disabledCondition;
import static rs.acs.uns.sw.e2e.util.ConditionUtil.enabledCondition;
import static rs.acs.uns.sw.e2e.util.Constants.WEBDRIVER_TIMEOUT;
import static rs.acs.uns.sw.e2e.util.LoginUtil.logout;

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
public class AnnouncementTest {

    private static WebDriver driver;

    // Wait in web driver until some condition is not satisfied
    private static WebDriverWait wait;

    private static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy.");

    @BeforeClass
    public static void instanceDriver() {
        ChromeOptions options = ConfigUtil.chromeOptions();
        options.addArguments("incognito");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WEBDRIVER_TIMEOUT);
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
     * Test reporting announcement as guest
     * <p>
     * There is no precondition. First we navigate to announcement page and click
     * on add report button. After that we properly fill report creation form and submit it.
     * Expectation: Success message appear.
     */
    @Test
    public void reportAnnouncementAsGuest() throws InterruptedException {
        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.visibilityOfElementLocated(REPORT_BTN));
        WebElement reportBtn = driver.findElement(REPORT_BTN);
        reportBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(MODAL_DIALOG));
        WebElement modalDialog = driver.findElement(MODAL_DIALOG);

        WebElement addReport = modalDialog.findElement(ADD_REPORT_BTN);
        assertThat(addReport.getAttribute("disabled")).isEqualTo("true");

        WebElement reporter = modalDialog.findElement(REPORTER_EMAIL_INPUT);
        reporter.sendKeys(REPORTER_EMAIL);

        WebElement reportContent = modalDialog.findElement(REPORT_CONTENT_INPUT);
        reportContent.sendKeys(REPORT_CONTENT);

        wait.until(enabledCondition(driver, ADD_REPORT_BTN));
        addReport.click();

        wait.until(visibilityOfElementLocated(SUCCESS_REPORTED_MSG));
        driver.findElement(SUCCESS_REPORTED_MSG).click();
    }

    /**
     * Test reporting announcement as guest twice
     * <p>
     * There is no precondition. First we navigate to announcement page and click
     * on add report button. After that we properly fill report creation form and submit it.
     * Finally, we try again to create report for the same announcement with th same email.
     * Expectation: Report submit button to be disabled.
     */
    @Test
    public void reportAnnouncementAsGuestTwice() throws InterruptedException, AWTException {
        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.presenceOfElementLocated(REPORT_BTN));
        WebElement reportBtn = driver.findElement(REPORT_BTN);

        // Wait report btn to be clickable
        Thread.sleep(2000);
        reportBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(MODAL_DIALOG));
        WebElement modalDialog = driver.findElement(MODAL_DIALOG);

        WebElement addReport = modalDialog.findElement(ADD_REPORT_BTN);
        assertThat(addReport.getAttribute("disabled")).isEqualTo("true");

        WebElement reporter = modalDialog.findElement(REPORTER_EMAIL_INPUT);
        reporter.sendKeys(REPORTER_EMAIL_TWICE);

        WebElement reportContent = modalDialog.findElement(REPORT_CONTENT_INPUT);
        reportContent.sendKeys(REPORT_CONTENT);

        wait.until(enabledCondition(driver, ADD_REPORT_BTN));
        addReport.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(REPORT_BTN));
        reportBtn = driver.findElement(REPORT_BTN);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL_DIALOG));

        // Wait report btn to be clickable
        Thread.sleep(2000);
        reportBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(MODAL_DIALOG));
        modalDialog = driver.findElement(MODAL_DIALOG);

        addReport = modalDialog.findElement(ADD_REPORT_BTN);
        assertThat(addReport.getAttribute("disabled")).isEqualTo("true");

        reporter = modalDialog.findElement(REPORTER_EMAIL_INPUT);
        reporter.sendKeys(REPORTER_EMAIL_TWICE);

        reportContent = modalDialog.findElement(REPORT_CONTENT_INPUT);
        reportContent.sendKeys(REPORT_CONTENT);

        wait.until(ExpectedConditions.visibilityOfElementLocated(ALREADY_REPORTED_MSG));

        assertThat(addReport.getAttribute("disabled")).isEqualTo("true");

        // close dialog
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ESCAPE);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL_DIALOG));
    }

    /**
     * Test reporting announcement without setting content
     * <p>
     * There is no precondition. First we navigate to announcement page and click
     * on add report button. After that we won't fill report content.
     * Expectation: Submission button should be disabled.
     */
    @Test
    public void reportWithoutContent() throws InterruptedException, AWTException {
        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        // Wait report btn to be clickable
        Thread.sleep(2000);

        wait.until(ExpectedConditions.presenceOfElementLocated(REPORT_BTN));
        WebElement reportBtn = driver.findElement(REPORT_BTN);

        reportBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(MODAL_DIALOG));
        WebElement modalDialog = driver.findElement(MODAL_DIALOG);

        WebElement addReport = modalDialog.findElement(ADD_REPORT_BTN);

        WebElement reporter = modalDialog.findElement(REPORTER_EMAIL_INPUT);
        reporter.sendKeys(REPORTER_EMAIL_WITHOUT_CONTENT);

        wait.until(disabledCondition(driver, ADD_REPORT_BTN));
        assertThat(addReport.getAttribute("disabled")).isEqualTo("true");

        // close dialog
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ESCAPE);

        wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL_DIALOG));
    }

    /**
     * Test reporting announcement as logged user
     * <p>
     * First we logged in as one of the users. Then we navigate to announcement page and
     * click on add report button. After that we fill report form properly and submit it.
     * Expectation: After submission report btn should be disabled.
     */
    @Test
    public void reportAnnouncementAsLoggedUser() throws InterruptedException {
        LoginUtil.login(EMAIL, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.presenceOfElementLocated(REPORT_BTN));
        WebElement reportBtn = driver.findElement(REPORT_BTN);
        reportBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(MODAL_DIALOG));
        WebElement modalDialog = driver.findElement(MODAL_DIALOG);

        WebElement addReport = modalDialog.findElement(ADD_REPORT_BTN);
        assertThat(addReport.getAttribute("disabled")).isEqualTo("true");

        WebElement reportContent = modalDialog.findElement(REPORT_CONTENT_INPUT);
        reportContent.sendKeys(REPORT_CONTENT);

        wait.until(enabledCondition(driver, ADD_REPORT_BTN));

        addReport.click();

        // Check if user can't report same add twice
        wait.until(ExpectedConditions.presenceOfElementLocated(REPORT_BTN));
        wait.until(disabledCondition(driver, REPORT_BTN));
        assertThat(reportBtn.isEnabled()).isFalse();

        wait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_REPORTED_MSG));
        final WebElement successNotification = driver.findElement(SUCCESS_REPORTED_MSG);
        successNotification.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(SUCCESS_REPORTED_MSG));

        logout(driver, wait);
    }

    /**
     * Test adding new comment for announcement
     * <p>
     * There is no precondition. First we navigate to announcement page.
     * Then we create new comment and submit it.
     * Expectation: New comment is displayed with today creation date.
     */
    @Test
    public void addCommentAsGuest() throws InterruptedException {
        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.presenceOfElementLocated(COMMENT));
        WebElement comment = driver.findElement(COMMENT);
        comment.sendKeys(COMMENT_TEXT);

        WebElement commentBtn = driver.findElement(ADD_COMMENT);
        commentBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(DATE_COMMENTED));
        WebElement date = driver.findElement(DATE_COMMENTED);
        assertThat(date.getText()).isEqualTo(String.format(DATE_COMMENT_DISPLAY, format.format(new Date())));
    }

    /**
     * Test adding new comment for announcement as logged user
     * <p>
     * First we need to log in. Then we navigate to announcement page.
     * Finally we create new comment and submit it.
     * Expectation: New comment is displayed with today creation date.
     */
    @Test
    public void addCommentAsLoggedUser() throws InterruptedException {
        LoginUtil.login(EMAIL, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_REPORTED);

        wait.until(ExpectedConditions.presenceOfElementLocated(COMMENT));
        WebElement comment = driver.findElement(COMMENT);
        comment.sendKeys(COMMENT_TEXT);

        WebElement commentBtn = driver.findElement(ADD_COMMENT);
        commentBtn.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(DATE_COMMENTED));
        WebElement date = driver.findElement(DATE_COMMENTED);
        assertThat(date.getText()).isEqualTo(String.format(DATE_COMMENT_DISPLAY, format.format(new Date())));

        logout(driver, wait);
    }

    /**
     * Test deleting previously added comment for announcement as logged user
     * <p>
     * First we need to log in. Then we navigate to announcement page. Finally we delete created comment.
     * Expectation: Deleted comment has not shown from announcement page.
     */
    @Test
    public void deleteAddedComment() throws InterruptedException {
        LoginUtil.login(EMAIL, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_COMMENTED);
        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_COMMENTED));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_COMMENTED);

        wait.until(ExpectedConditions.presenceOfElementLocated(COMMENT));
        WebElement comment = driver.findElement(COMMENT);
        comment.sendKeys(COMMENT_TEXT);

        WebElement commentBtn = driver.findElement(ADD_COMMENT);
        commentBtn.click();

        // Wait for comment to appear
        wait.until(ExpectedConditions.presenceOfElementLocated(DATE_COMMENTED));

        WebElement deleteBtn = driver.findElement(DELETE_COMMENT);
        deleteBtn.click();

        wait.until(ExpectedConditions.not(presenceOfAllElementsLocatedBy(DATE_COMMENTED)));

        logout(driver, wait);
    }

    /**
     * Test reporting verified announcement
     * <p>
     * First we need to log in. Then we navigate to page of verified
     * announcement. Finally we try to create report for that announcement.
     * Expectation: Report btn should be hidden.
     */
    @Test
    public void reportVerifiedAnnouncement() throws InterruptedException {
        LoginUtil.login(EMAIL, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT);
        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT);

        wait.until(ExpectedConditions.not(presenceOfAllElementsLocatedBy(REPORT_BTN)));

        logout(driver, wait);
    }

    /**
     * Test rating announcement
     * <p>
     * First we need to log in. Then we navigate to announcement page and rate it.
     * Expectation: Rated mark should be added to list of all rates.
     */
    @Test
    public void rateAnnouncement() throws InterruptedException {
        LoginUtil.login(EMAIL, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_RATE);
        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_RATE));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_RATE);

        wait.until(visibilityOfElementLocated(RATING_ANNOUNCEMENT_UPDATE));
        WebElement ratings = driver.findElement(RATING_ANNOUNCEMENT_UPDATE);

        // Set rating to 4
        ratings.findElements(By.xpath("ul/li")).get(3).click();

        // hover ratings
        Actions action = new Actions(driver);
        WebElement ratingList = driver.findElement(RATING_ANNOUNCEMENT_LIST);
        action.moveToElement(ratingList).perform();
        wait.until(visibilityOfElementLocated(RATING_ANNOUNCEMENT_LIST));

        assertThat(ratingList.findElements(NUM_OF_VOTES).get(1).getText()).isEqualTo("1");

        logout(driver, wait);
    }

    /**
     * Test rating own announcement
     * <p>
     * First we need to log in. Then we navigate to announcement page and rate it.
     * Expectation: List of all marks should be same as previous.
     */
    @Test
    @Ignore
    public void rateOwnAnnouncement() throws InterruptedException {
        LoginUtil.login(EMAIL_OWNER, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT);
        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT);

        wait.until(visibilityOfElementLocated(RATING_ANNOUNCEMENT_UPDATE));
        WebElement ratings = driver.findElement(RATING_ANNOUNCEMENT_UPDATE);

        // Set rating to 1
        ratings.findElements(By.xpath("ul/li")).get(0).click();

        // hover ratings
        Actions action = new Actions(driver);
        WebElement ratingList = driver.findElement(RATING_ANNOUNCEMENT_LIST);
        action.moveToElement(ratingList).perform();
        wait.until(visibilityOfElementLocated(RATING_ANNOUNCEMENT_LIST));

        assertThat(ratingList.findElements(NUM_OF_VOTES).get(4).getText()).isEqualTo("0");

        logout(driver, wait);
    }

    /**
     * Test rating announcer.
     * <p>
     * First we need to log in. Then we navigate to announcement page and rate it't announcer.
     * Expectation: List of all marks should be updated.
     */
    @Test
    public void rateAnnouncer() throws InterruptedException {
        LoginUtil.login(EMAIL, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_COMMENTED);
        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_COMMENTED));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + ANNOUNCEMENT_TO_BE_COMMENTED);

        wait.until(visibilityOfElementLocated(RATING_ANNOUNCER_UPDATE));
        WebElement ratings = driver.findElement(RATING_ANNOUNCER_UPDATE);

        // Set rating to 2
        ratings.findElements(By.xpath("ul/li")).get(1).click();

        // hover ratings
        Actions action = new Actions(driver);
        WebElement ratingList = driver.findElement(RATING_ANNOUNCER_LIST);
        action.moveToElement(ratingList).perform();
        wait.until(visibilityOfElementLocated(RATING_ANNOUNCER_LIST));

        assertThat(ratingList.findElements(NUM_OF_VOTES).get(3).getText()).isEqualTo("0");

        logout(driver, wait);
    }

    /**
     * Test rating yourself as announcer.
     * <p>
     * First we need to log in as announcer. Then announcer navigate to
     * announcement page and rate himself/herself as announcer.
     * Expectation: List of all marks should not be updated.
     */
    @Test
    @Ignore
    public void rateYourself() throws InterruptedException {
        LoginUtil.login(EMAIL_OWNER, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT);
        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT);

        wait.until(visibilityOfElementLocated(RATING_ANNOUNCER_UPDATE));
        WebElement ratings = driver.findElement(RATING_ANNOUNCER_UPDATE);

        // Set rating to 5
        ratings.findElements(By.xpath("ul/li")).get(4).click();

        // hover ratings
        Actions action = new Actions(driver);
        WebElement ratingList = driver.findElement(RATING_ANNOUNCER_LIST);
        action.moveToElement(ratingList).perform();
        wait.until(visibilityOfElementLocated(RATING_ANNOUNCER_LIST));

        assertThat(ratingList.findElements(NUM_OF_VOTES).get(0).getText()).isEqualTo("1");

        logout(driver, wait);
    }
}