package rs.acs.uns.sw.e2e.pages;


import org.openqa.selenium.By;

public class UserProfile {
    public static final String USER_PROFILE_URL = "http://localhost:8080/#/profile/";


    public static final By EDIT_BUTTON = By.id("edit-profile");
    public static final By CHANGE_PASSWORD_BUTTON = By.id("change-pass");
    public static final By EDIT_FIRST_NAME = By.id("form-first-name");
    public static final By EDIT_LAST_NAME = By.id("form-last-name");
    public static final By EDIT_PASSWORD = By.id("edit-password");
    public static final By EDIT_RE_PASSWORD = By.id("edit-re-password");
    public static final By USER_MENU = By.id("user-menu");
    public static final By LOGOUT_LINK = By.id("logout-link");
    public static final By EDIT_PHONE = By.id("form-phone");
    public static final By SAVE_EDIT_BUTTON = By.id("save-edit-button");
    public static final By CANCEL_EDIT_BUTTON = By.id("cancel-edit-button");
    public static final By DISPLAYED_NAME = By.id("user-name");
    public static final By DISPLAYED_PHONE = By.id("user-phone");
    public static final By FORM_EDIT_PERSONAL_DATA = By.id("form-edit-personal-data");
    public static final By SAVE_PASS_BUTTON = By.id("save-pass-button");
    public static final By CHANGE_PROFILE_PICTURE_BUTTON = By.id("change-image");
    public static final By CHANGE_PASSWORD_SUCCESS_MESSAGE = By.id("pass-changed");
    public static final By EXTENDED_DATE = By.id("extending-date");
    public static final By EXTENDED_DATE_BTN = By.id("extend-expiration-date-btn");
    public static final By EXTENDED_DATE_SUCCESS_MSG = By.id("exp-date-message");
    public static final By EXTENDED_DATE_ERROR_MSG = By.id("exp-date-error-msg");
    public static final By MEMBERSHIP_REQUESTS_TAB = By.id("company-membership-requests");
    public static final By ACCEPT_REQUEST_BTN = By.id("accept-request");
    public static final By DENY_REQUEST_BTN = By.id("deny-request");
    public static final By USER_COMPANY = By.id("user-company");

    public static final String X_PATH_ERROR_MESSAGE_P = "//p[contains(text(), \"%s\")]";
    public static final String X_PATH_ANNOUNCEMENT_FRAGMENT = "//a[contains(@href, \"%s\")]/../..";
    public static final String X_PATH_USER_MEM_REQ_FRAGMENT = "//a[contains(@href, \"%s\")]/..";

    public static final String ANNOUNCEMENT_LINK = "#/announcement/";

    // Fixed data for populating inputs for registration
    public static final String VERIFIER_USERNAME = "isco";
    public static final String DEFAULT_PASSWORD = "123456";

    public static final String UPDATED_FIRST_NAME = "Antony";
    public static final String UPDATED_LAST_NAME = "Davies";
    public static final String UPDATED_PHONE = "44-5-852";
    public static final String UPDATED_PASSWORD = "654321";
    public static final String USERNAME_FOR_PASS_CHANGING = "bjelica";
    public static final String ANNOUNCEMENT_ID = "4";
    public static final String ANNOUNCEMENT_ID_EXP_BEFORE_PREV = "5";

    public static final String USERNAME_FOR_EXTENDING_DATE = "test_advertiser_company_member";
    public static final String UPDATED_EXTENDED_DATE = "20/10/2018";
    public static final String DATE_EXPIRATION_SUCCESS_MESSAGE_CONTENT = "Datum isteka oglasa produžen do: %s";
    public static final String WRONG_EXTENDED_DATE = "20/10/2010";
    public static final String DATE_EXPIRATION_ERROR_MESSAGE_CONTENT = "GREŠKA! Modified date must be after today";
    public static final String USER_MEMBERSHIP_LINK_ACCEPT = "#/profile/test_advertiser_pending_membership/?page=1&sort=id%2Casc";
    public static final String USER_MEMBERSHIP_LINK_DENY = "#/profile/sr4/?page=1&sort=id%2Casc";
    public static final String COMPANY_MEMBERS_LINK = "#/company/4/members";
    public static final String EXTENDED_DATE_BEFORE_PREV = "20/10/2017";

    public static final String URL_PREFIX = "http://localhost:8080/";
}
