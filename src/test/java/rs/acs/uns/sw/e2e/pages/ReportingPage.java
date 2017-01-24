package rs.acs.uns.sw.e2e.pages;


import org.openqa.selenium.By;

public class ReportingPage {

    public static final String REPORTING_URL = "http://localhost:8080/#/reports";

    public static final By ACTIVE_REPORTS_CONTAINER = By.id("pendingReports");
    public static String ACCEPT_REPORT_BTN_ID = "accept-report-%s";
    public static String REJECT_BTN_ID = "reject-report-%s";
    public static final By ACCEPTED_REPORTS_SEARCH = By.id("accepted-search");
    public static final By REJECTED_REPORTS_SEARCH = By.id("rejected-search");
    public static final By REPORTER_QUERY = By.id("reporter-query");
    public static final By SEARCH_BTN = By.id("search-btn");
    public static final By REPORTER_EMAIL_DISPLAY = By.id("reporter-email");
    public static final By SORT_BY_DATE = By.id("date-sort");
    public static final By REPORTER_DATE_DISPLAY = By.id("report-date");

    public static final String REPORTER_USERNAME = "wiggins";
    public static final String REPORTER_PASSWORD = "123456";

    public static final String REPORT_TO_BE_REJECTED = "4";
    public static final String PENDING_REPORT_ID = "3";

    public static final String EMAIL_PENDING = "user53@mail.com";
    public static final String EMAIL_ACCEPTED = "user6@mail.com";
    public static final String EMAIL_REJECTED = "user50@mail.com";
}
