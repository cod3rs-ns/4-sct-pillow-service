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

    // Registration fields
    public static final By FORM_REGISTER = By.id("registration-form");
    public static final By BUTTON_REGISTER = By.id("register-button");
    public static final By INPUT_USERNAME = By.id("sign-up-username");
    public static final By INPUT_PASSWORD = By.id("sign-up-password");
    public static final By INPUT_RE_PASSWORD = By.id("sign-up-re-password");
    public static final By INPUT_FIRST_NAME = By.id("sign-up-first-name");
    public static final By INPUT_LAST_NAME = By.id("sign-up-last-name");
    public static final By INPUT_TYPE = By.id("sign-up-type");
    public static final By INPUT_EMAIL = By.id("sign-up-email");
    public static final By INPUT_PHONE = By.id("sign-up-phone");
    public static final By EMAIL_SUCCESS_MESSAGE = By.id("email-sent");
    public static final By EMAIL_EXISTS = By.id("email-already-exists");


    public static final By SUCCESS_REGISTRATION_MSG = By.id("success-registration");
    public static final String WRONG_LOGIN_DIALOG_MESSAGE = "Uneseno korisničko ime i/ili lozinka su pogrešni. Pokušajte ponovo.";

    // Fixed data for populating textboxes on page
    public static final String ADMIN_USERNAME = "b";
    public static final String ADMIN_PASSWORD = "123456";

    public static final String WRONG_USERNAME = "Sergio Ramos <3";
    public static final String WRONG_PASSWORD = "Until the final whistle";

    // Fixed data for populating inputs for registration
    public static final String REGISTRATION_USERNAME = "jordan23m";
    public static final String REGISTRATION_PASSWORD = "jordan23";
    public static final String REGISTRATION_FIRST_NAME = "michael";
    public static final String REGISTRATION_LAST_NAME = "jordan";
    public static final String REGISTRATION_TYPE = "verifikator";
    public static final String REGISTRATION_EMAIL = "michael@jordan23.com";
    public static final String REGISTRATION_PHONE = "99-66-88";

    public static final String NEW_USERNAME = "michael";
    public static final String NEW_EMAIL = "jordan@jordan.com";

    public static final String USERNAME_IN_USE = "isco";
    public static final String EMAIL_IN_USE = "isco@gmail.com";
    public static final String WRONG_EMAIL = "email";
    public static final String WEAK_PASSWORD = "123";
    public static final String RETYPED_WRONG_PASSWORD = "jordan32";

    public static final String X_PATH_ERROR_MESSAGE_SPAN = "//span[contains(text(), \"%s\")]";

    public static final class ValidationMessages {
        public static final String USERNAME_EXISTS = "Korisničko ime je već zauzeto.";
        public static final String WRONG_EMAIL = "Nepravilno formirana email adresa.";
        public static final String WEAK_PASSWORD = "Šifra mora sadržati bar 6 karaktera.";
        public static final String PASSWORD_DOES_NOT_MATCH = "Šifra se ne poklapa.";
        public static final String USERNAME_REQUIRED = "Korisničko ime je obavezno.";
        public static final String PASSWORD_REQUIRED = "Šifra je obavezna.";
        public static final String FIRST_NAME_REQUIRED = "Ime je obavezno.";
        public static final String LAST_NAME_REQUIRED = "Prezime je obavezno.";
        public static final String PHONE_REQUIRED = "Telefon je obavezan.";

        private ValidationMessages() {
        }
    }
}
