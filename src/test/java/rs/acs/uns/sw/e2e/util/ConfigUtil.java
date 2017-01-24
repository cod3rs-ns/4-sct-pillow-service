package rs.acs.uns.sw.e2e.util;

import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

import static rs.acs.uns.sw.e2e.util.Constants.*;

public class ConfigUtil {

    /**
     * Util function for setting options for chrome driver
     *
     * @return options
     */
    public static ChromeOptions chromeOptions() {
        File file = new File(WEB_DRIVER_PATH);
        System.setProperty(WEB_DRIVER_NAME, file.getAbsolutePath());

        final String os = System.getProperty("os.name");

        final ChromeOptions options = new ChromeOptions();
        options.addArguments(("Mac OS X".equals(os)) ? MAXIMIZE_OSX : MAXIMIZE_WIN);
        return options;
    }
}
