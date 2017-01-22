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
import static rs.acs.uns.sw.e2e.pages.UserProfile.LOGOUT_LINK;
import static rs.acs.uns.sw.e2e.pages.UserProfile.USER_MENU;
import static rs.acs.uns.sw.e2e.util.ConditionUtil.disabledCondition;
import static rs.acs.uns.sw.e2e.util.ConditionUtil.enabledCondition;
import static rs.acs.uns.sw.e2e.util.Constants.WEBDRIVER_TIMEOUT;
import static rs.acs.uns.sw.e2e.util.LoginUtil.logout;

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
public class AnnouncementTest {

    private static WebDriver driver;

    // Wait in webdriver until some condition is not satisfied
    private static WebDriverWait wait;

    @BeforeClass
    public static void instanceDriver() {
        ChromeOptions options = ConfigUtil.chromeOptions();
        options.addArguments("incognito");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, WEBDRIVER_TIMEOUT);
    }

    @AfterClass
    public static void closeDriver() {
        //driver.close();
    }

    @Before
    public void openBrowser() {
        driver.get(SIGNING_URL);
    }

    @Test
    public void reportAnnouncementAsGuest() throws InterruptedException {
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

        WebElement reporter = modalDialog.findElement(REPORTER_EMAIL_INPUT);
        reporter.sendKeys(REPORTER_EMAIL);

        WebElement reportContent = modalDialog.findElement(REPORT_CONTENT_INPUT);
        reportContent.sendKeys(REPORT_CONTENT);

        wait.until(enabledCondition(driver, ADD_REPORT_BTN));
        addReport.click();
    }

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

        final WebElement linkUserMenu = driver.findElement(USER_MENU);
        linkUserMenu.click();

        wait.until(visibilityOfElementLocated(LOGOUT_LINK));
        final WebElement logoutLink = driver.findElement(LOGOUT_LINK);

        logoutLink.click();
    }

    @Test
    public void addCommentAsGuest() throws InterruptedException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy.");
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


    @Test
    public void addCommentAsLoggedUser() throws InterruptedException {
        LoginUtil.login(EMAIL, PASSWORD, driver, wait);

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy.");
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

    @Test
    public void reportVerifiedAnnouncement() throws InterruptedException {
        LoginUtil.login(EMAIL, PASSWORD, driver, wait);

        driver.get(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT);
        wait.until(ExpectedConditions.urlToBe(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT));
        assertThat(driver.getCurrentUrl()).isEqualTo(ANNOUNCEMENT_PAGE_URL + VERIFIED_ANNOUNCEMENT);

        wait.until(ExpectedConditions.not(presenceOfAllElementsLocatedBy(REPORT_BTN)));

        logout(driver, wait);
    }

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
    }

    @Test
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
    }

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
    }

    @Test
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
    }
}
