package rs.acs.uns.sw.e2e.util;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;
import static rs.acs.uns.sw.e2e.pages.HomePage.HOME_URL;
import static rs.acs.uns.sw.e2e.pages.SigningPage.*;
import static rs.acs.uns.sw.e2e.pages.UserProfile.LOGOUT_LINK;
import static rs.acs.uns.sw.e2e.pages.UserProfile.USER_MENU;

public class LoginUtil {

    public static void login(String username, String password, WebDriver driver, WebDriverWait wait) {

        driver.navigate().to(SIGNING_URL);
        wait.until(ExpectedConditions.urlToBe(SIGNING_URL));

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


    public static void logout(WebDriver driver, WebDriverWait wait) {
        final WebElement linkUserMenu = driver.findElement(USER_MENU);
        linkUserMenu.click();

        wait.until(visibilityOfElementLocated(LOGOUT_LINK));
        final WebElement logoutLink = driver.findElement(LOGOUT_LINK);

        logoutLink.click();
    }
}
