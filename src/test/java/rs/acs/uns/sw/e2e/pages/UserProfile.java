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

    public static final String X_PATH_ERROR_MESSAGE_P = "//p[contains(text(), \"%s\")]";

    // Fixed data for populating inputs for registration
    public static final String VERIFIER_USERNAME = "isco";
    public static final String VERIFIER_PASSWORD = "123456";

    public static final String UPDATED_FIRST_NAME = "Antony";
    public static final String UPDATED_LAST_NAME = "Davies";
    public static final String UPDATED_PHONE = "44-5-852";
    public static final String UPDATED_PASSWORD = "654321";
    public static final String USERNAME_FOR_PASS_CHANGING = "bjelica";
}
