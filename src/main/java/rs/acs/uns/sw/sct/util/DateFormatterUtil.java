package rs.acs.uns.sw.sct.util;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

/**
 * Util class for date formatting.
 */
@Component
public class DateFormatterUtil {

    /**
     * Provides valid and only accepted date format.
     * @return SimpleDateFormat
     */
    @Bean(name = "dateFormatter")
    public SimpleDateFormat dateFormatter() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }
}
