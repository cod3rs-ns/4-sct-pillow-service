package rs.acs.uns.sw.awt_test.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility class for testing REST controllers.
 */
public class DateUtil {

    private static SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");


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

    /**
     * Create java.util.Date from year, month, date
     *
     * @param date
     * @return the Date
     */
    public static Timestamp date(String date) {
        Timestamp retVal = null;

        try {
            Date d = fmt.parse(date);
            long time = d.getTime();
            retVal = new Timestamp(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return retVal;
    }
}