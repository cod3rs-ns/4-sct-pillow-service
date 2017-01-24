package rs.acs.uns.sw.e2e.pages;

import org.openqa.selenium.By;

/**
 * Constants related to CompanyTest
 * @see rs.acs.uns.sw.e2e.tests.CompanyTest
 */
public class CompanyPage {

    private CompanyPage() {}

    // Elements important for testing flow

    public static final String COMPANIES_URL    = "http://localhost:8080/#/companies";
    public static final String COMPANY_URL      = "http://localhost:8080/#/company/";
    public static final String ADD_COMPANY_URL  = "http://localhost:8080/#/company-add";


    public static final By COMPANY_IDENTIFIER = By.cssSelector(".btn.btn-primary.btn-sm.pull-right");
    public static final By REQUEST_MEMBERSHIP_BUTTON = By.id("company-membership-request");
    public static final By REQUEST_SENT_ID = By.id("request-sent");

    // New company form
    public static final By FORM_COMPANY_NAME    = By.id("company-name");
    public static final By FORM_COMPANY_ADDRESS = By.id("company-address");
    public static final By FORM_COMPANY_PHONE   = By.id("company-phone");
    public static final By CHOOSE_LOGO_BUTTON   = By.id("choose-logo");
    public static final By FORM_COMPANY_USER    = By.id("company-user");
    public static final By ADD_COMPANY_BUTTON   = By.id("add-company-btn");
    public static final By REQUIRED_ADDRESS     = By.id("required-address");

    public static final String REQUEST_SENT_MESSAGE     = "Uspješno ste zatražili članstvo u kompaniji.";
    public static final String REQUIRED_ADDRESS_MESSAGE = "Adresa je obavezna.";

    // Fixed data for populating
    public static final String ADVERTISER_USERNAME_WITHOUT_COMPANY = "lbj";
    public static final String ADVERTISER_PASSWORD_WITHOUT_COMPANY = "123456";

    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "123456";

    public static final String COMPANY_NAME_VALUE    = "KMJ co.";
    public static final String COMPANY_ADDRESS_VALUE = "Klisa BB BB";
    public static final String COMPANY_PHONE_VALUE   = "+1 23 21323";
    public static final String COMPANY_LOGO_IMAGE    = "./src/test/resources/company-logo.jpg";
    public static final String COMPANY_USER_VALUE    = "David";
}
