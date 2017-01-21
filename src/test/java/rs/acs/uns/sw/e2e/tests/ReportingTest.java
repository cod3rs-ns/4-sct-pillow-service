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

import javax.swing.text.DateFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static rs.acs.uns.sw.e2e.pages.ReportingPage.*;
import static rs.acs.uns.sw.e2e.pages.SigningPage.SIGNING_URL;
import static rs.acs.uns.sw.e2e.util.Constants.WEBDRIVER_TIMEOUT;

@ActiveProfiles("test")
@SpringBootTest(classes = SctServiceApplication.class)
public class ReportingTest {

    private static WebDriver driver;

    // Wait in webdriver until some condition is not satisfied
    private static WebDriverWait wait;

    @BeforeClass
    public static void instanceDriver() {
        ChromeOptions options = ConfigUtil.chromeOptions();
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
        LoginUtil.login(REPORTER_USERNAME, REPORTER_PASSWORD, driver, wait);
    }

    @Test
    public void acceptReport() {
        driver.navigate().to(REPORTING_URL);
        wait.until(ExpectedConditions.urlToBe(REPORTING_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(REPORTING_URL);

        int activeReportsNum = driver.findElement(ACTIVE_REPORTS_CONTAINER).findElements(By.xpath("*")).size();
        assertThat(activeReportsNum).isEqualTo(NUM_OF_PENDING_REPORTS);

        WebElement report = driver.findElement(By.id(PENDING_REPORT_ID));
        WebElement acceptBtn = report.findElement((By.id(String.format(ACCEPT_REPORT_BTN_ID, PENDING_REPORT_ID))));
        acceptBtn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(PENDING_REPORT_ID)));

        activeReportsNum = driver.findElement(ACTIVE_REPORTS_CONTAINER).findElements(By.xpath("*")).size();
        assertThat(activeReportsNum).isEqualTo(NUM_OF_PENDING_REPORTS - 1);

        WebElement acceptRadio = driver.findElement(ACCEPTED_REPORTS_SEARCH);
        acceptRadio.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(PENDING_REPORT_ID)));
    }

    @Test
    public void rejectReport() {
        driver.navigate().to(REPORTING_URL);
        wait.until(ExpectedConditions.urlToBe(REPORTING_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(REPORTING_URL);

        WebElement report = driver.findElement(By.id(REPORT_TO_BE_REJECTED));
        WebElement rejectBtn = report.findElement((By.id(String.format(REJECT_BTN_ID, REPORT_TO_BE_REJECTED))));
        rejectBtn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(REPORT_TO_BE_REJECTED)));

        WebElement rejectRadio = driver.findElement(REJECTED_REPORTS_SEARCH);
        rejectRadio.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(REPORT_TO_BE_REJECTED)));
    }

    @Test
    public void searchPendingReports() {
        driver.navigate().to(REPORTING_URL);
        wait.until(ExpectedConditions.urlToBe(REPORTING_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(REPORTING_URL);

        WebElement reporterQuery = driver.findElement(REPORTER_QUERY);
        reporterQuery.sendKeys(EMAIL_PENDING);

        WebElement searchBtn = driver.findElement(SEARCH_BTN);
        searchBtn.click();

        List<WebElement> pendingReports = driver.findElement(ACTIVE_REPORTS_CONTAINER).findElements(By.xpath("*"));
        for (WebElement report : pendingReports) {
            assertThat(report.findElement(REPORTER_EMAIL_DISPLAY).getText()).isEqualTo(EMAIL_PENDING);
        }
    }

    @Test
    public void searchRejectedReports() throws InterruptedException {
        driver.navigate().to(REPORTING_URL);
        wait.until(ExpectedConditions.urlToBe(REPORTING_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(REPORTING_URL);

        WebElement reporterQuery = driver.findElement(REPORTER_QUERY);
        WebElement searchBtn = driver.findElement(SEARCH_BTN);

        reporterQuery.sendKeys(EMAIL_REJECTED);

        searchBtn.click();

        List<WebElement> pendingReports = driver.findElement(ACTIVE_REPORTS_CONTAINER).findElements(By.xpath("*"));
        Thread.sleep(3000);
        for (WebElement report : pendingReports) {
            assertThat(report.findElement(REPORTER_EMAIL_DISPLAY).getText()).isEqualTo(EMAIL_REJECTED);
        }
    }

    @Test
    public void searchAcceptedReports() throws InterruptedException {
        driver.navigate().to(REPORTING_URL);
        wait.until(ExpectedConditions.urlToBe(REPORTING_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(REPORTING_URL);

        WebElement reporterQuery = driver.findElement(REPORTER_QUERY);
        WebElement searchBtn = driver.findElement(SEARCH_BTN);

        reporterQuery.sendKeys(EMAIL_ACCEPTED);
        searchBtn.click();

        WebElement activeContainer = driver.findElement(ACTIVE_REPORTS_CONTAINER);
        List<WebElement> pendingReports = activeContainer.findElements(By.xpath("*"));
        Thread.sleep(3000);
        for (WebElement report : pendingReports) {
            assertThat(report.findElement(REPORTER_EMAIL_DISPLAY).getText()).isEqualTo(EMAIL_ACCEPTED);
        }
    }

    @Test
    public void sortByDate() throws ParseException {
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy");

        driver.navigate().to(REPORTING_URL);
        wait.until(ExpectedConditions.urlToBe(REPORTING_URL));
        assertThat(driver.getCurrentUrl()).isEqualTo(REPORTING_URL);

        WebElement sortBtn = driver.findElement(SORT_BY_DATE);
        sortBtn.click();

        WebElement activeContainer = driver.findElement(ACTIVE_REPORTS_CONTAINER);
        List<WebElement> pendingReports = activeContainer.findElements(By.xpath("*"));
        for (int i = 1; i < pendingReports.size(); i++) {
            Date d1 = dt.parse(pendingReports.get(i - 1).findElement(REPORTER_DATE_DISPLAY).getText());
            Date d2 = dt.parse(pendingReports.get(i).findElement(REPORTER_DATE_DISPLAY).getText());
            assertThat(d1).isBeforeOrEqualsTo(d2);
        }
    }
}
