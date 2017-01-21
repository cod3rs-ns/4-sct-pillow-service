package rs.acs.uns.sw.e2e.util;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static rs.acs.uns.sw.e2e.pages.HomePage.HOME_URL;
import static rs.acs.uns.sw.e2e.pages.SigningPage.BUTTON_LOGIN;
import static rs.acs.uns.sw.e2e.pages.SigningPage.TEXTBOX_PASSWORD;
import static rs.acs.uns.sw.e2e.pages.SigningPage.TEXTBOX_USERNAME;

public class LoginUtil {

    public static void login(String username, String password, WebDriver driver, WebDriverWait wait) {
        // Input Username
        final WebElement inputUsername = driver.findElement(TEXTBOX_USERNAME);
        inputUsername.sendKeys(username);

        // Input Password
        final WebElement inputPassword = driver.findElement(TEXTBOX_PASSWORD);
        inputPassword.sendKeys(password);

        // Click on 'Uloguj se' button.
        final WebElement buttonSubmit = driver.findElement(BUTTON_LOGIN);
        buttonSubmit.click();

        wait.until(ExpectedConditions.urlToBe(HOME_URL));
    }
}
