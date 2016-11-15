package rs.acs.uns.sw.awt_test.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utility class for testing REST controllers.
 */
public class DateUtil {

    /**
     * Convert a LocalDate to java.util.Date
     *
     * @param localDate the LocalDate to convert
     * @return the java.util.Date
     */
    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Convert a java.util.Date to LocalDate
     *
     * @param date the java.util.Date to convert
     * @return the LocalDate
     */
    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

}