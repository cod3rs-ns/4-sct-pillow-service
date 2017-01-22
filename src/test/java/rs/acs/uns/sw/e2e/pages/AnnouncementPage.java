package rs.acs.uns.sw.e2e.pages;


import org.openqa.selenium.By;
import sun.security.provider.PolicySpiFile;

public class AnnouncementPage {

    public static final String ANNOUNCEMENT_PAGE_URL = "http://localhost:8080/#/announcement/";

    public static final By REPORT_BTN = By.id("report-btn");
    public static final By REPORT_CONTENT_INPUT = By.id("report-content");
    public static final By REPORTER_EMAIL_INPUT = By.id("reporter");
    public static final By ADD_REPORT_BTN = By.id("add-report-btn");
    public static final By MODAL_DIALOG = By.className("modal-dialog");
    public static final By ALREADY_REPORTED_MSG = By.id("already-reported-msg");
    public static final By SUCCESS_REPORTED_MSG = By.id("success-reported");
    public static final By COMMENT = By.id("comment");
    public static final By ADD_COMMENT = By.id("add-comment");
    public static final By DATE_COMMENTED = By.id("date-commented");
    public static final By DELETE_COMMENT = By.id("delete-comment");
    public static final By RATING_ANNOUNCEMENT_UPDATE = By.id("announcement-rate-update");
    public static final By RATING_ANNOUNCEMENT_LIST = By.id("list-rate-ann");
    public static final By NUM_OF_VOTES = By.className("vote-no");

    public static final String EMAIL = "russ";
    public static final String EMAIL_OWNER = "isco";
    public static final String PASSWORD = "123456";

    public static final String ANNOUNCEMENT_TO_BE_REPORTED = "4";
    public static final String REPORT_CONTENT = "Wrong announcement";
    public static final String REPORTER_EMAIL = "reporter@reporter.com";
    public static final String REPORTER_EMAIL_TWICE = "twice@twice.com";
    public static final String REPORTER_EMAIL_WITHOUT_CONTENT = "content@content.com";
    public static final String COMMENT_TEXT = "New comment";
    public static final String DATE_COMMENT_DISPLAY = "Objavljen: %s";
    public static final String ANNOUNCEMENT_TO_BE_COMMENTED = "3";
    public static final String VERIFIED_ANNOUNCEMENT = "2";
    public static final String ANNOUNCEMENT_TO_BE_RATE = "1";

}
