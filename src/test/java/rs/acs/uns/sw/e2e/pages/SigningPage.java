package rs.acs.uns.sw.e2e.pages;

import org.openqa.selenium.By;

/**
 * Constants from client's Signing Page and data for populating
 */
public class SigningPage {

    public static final String SIGNING_URL = "http://localhost:8080/#/login";

    public static final By BUTTON_LOGIN = By.id("login-button");
    public static final By TEXTBOX_USERNAME = By.id("form-username");
    public static final By TEXTBOX_PASSWORD = By.id("form-password");

    public static final By LOGGED_USER_NAVBAR = By.cssSelector(".nav.navbar-nav.navbar-right.ng-scope > li > a");
    public static final By WRONG_LOGIN_DIALOG = By.cssSelector(".alert.alert-danger");


    public static final String WRONG_LOGIN_DIALOG_MESSAGE = "Uneseno korisničko ime i/ili lozinka su pogrešni. Pokušajte ponovo.";

    // Fixed data for populating textboxes on page
    public static final String ADMIN_USERNAME = "isco";
    public static final String ADMIN_PASSWORD = "123456";

    public static final String WRONG_USERNAME = "Sergio Ramos <3";
    public static final String WRONG_PASSWORD = "Until the final whistle";
}
