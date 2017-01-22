package rs.acs.uns.sw.e2e.util;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import javax.annotation.Nullable;

public class ConditionUtil {

    public static ExpectedCondition<Boolean> disabledCondition(WebDriver driver, By element) {
        return new ExpectedCondition<Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable WebDriver input) {
                String enabled = driver.findElement(element).getAttribute("disabled");
                if (enabled == null) {
                    return false;
                }
                else
                    return true;

            }
        };
    }

    public static ExpectedCondition<Boolean> enabledCondition(WebDriver driver, By element) {
        return new ExpectedCondition<Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable WebDriver input) {
                String enabled = driver.findElement(element).getAttribute("disabled");
                if (enabled == null)
                    return true;
                else
                    return false;
            }
        };
    }
}
