package rs.acs.uns.sw.e2e.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DragAndDropUtil {

    /**
     * Util function for dropping files from file system to browser.
     *
     * @param element   Element where drop is enabled
     * @param path      File we want to drop
     */
    public static void dropFile(final WebDriver driver, final WebDriverWait wait, final WebElement element, final String path) {

        final JavascriptExecutor jse = (JavascriptExecutor) driver;

        final String JS_DROP_FILE = "var target = arguments[0],\n" +
                "                offsetX = arguments[1],\n" +
                "                offsetY = arguments[2],\n" +
                "                document = target.ownerDocument || document,\n" +
                "                window = document.defaultView || window;\n" +
                "\n" +
                "        var input = document.createElement('INPUT');\n" +
                "        input.type = 'file';\n" +
                "        input.style.display = 'none';\n" +
                "        input.onchange = function () {\n" +
                "            target.scrollIntoView(true);\n" +
                "\n" +
                "            var rect = target.getBoundingClientRect(),\n" +
                "                    x = rect.left + (offsetX || (rect.width >> 1)),\n" +
                "                    y = rect.top + (offsetY || (rect.height >> 1)),\n" +
                "                    dataTransfer = { files: this.files };\n" +
                "\n" +
                "          ['dragenter', 'dragover', 'drop'].forEach(function (name) {\n" +
                "                var evt = document.createEvent('MouseEvent');\n" +
                "                evt.initMouseEvent(name, !0, !0, window, 0, 0, 0, x, y, !1, !1, !1, !1, 0, null);\n" +
                "                evt.dataTransfer = dataTransfer;\n" +
                "                target.dispatchEvent(evt);\n" +
                "            });\n" +
                "\n" +
                "            setTimeout(function () { document.body.removeChild(input); }, 25);\n" +
                "        };\n" +
                "        document.body.appendChild(input);\n" +
                "        return input;";

        final WebElement input = (WebElement) jse.executeScript(JS_DROP_FILE, element, 0, 0);
        input.sendKeys(path);
        wait.until(ExpectedConditions.stalenessOf(input));
    }
}
