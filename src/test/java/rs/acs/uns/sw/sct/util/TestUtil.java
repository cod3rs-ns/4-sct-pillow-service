package rs.acs.uns.sw.sct.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

    private static Random random = new Random();

    /**
     * MediaType for JSON UTF8
     */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    /**
     * Convert an object to JSON byte array.
     *
     * @param object the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);

        return mapper.writeValueAsBytes(object);
    }


    /**
     * Return random case insensitive substring.
     *
     * @param value string to be converted
     * @return the String
     */
    public static String getRandomCaseInsensitiveSubstring(String value) {
        StringBuilder builder = new StringBuilder();

        int startIndex = getRandomIntBetween(0, value.length() - 1);
        int endIndex = getRandomIntBetween(startIndex + 1, value.length());

        for (int i = startIndex; i < endIndex; i++) {
            boolean upper = random.nextBoolean();
            if(upper)
                builder.append(Character.toUpperCase(value.charAt(i)));
            else
                builder.append(Character.toLowerCase(value.charAt(i)));
        }

        return builder.toString();
    }

    public static int getRandomIntBetween(int start, int end) {
        return random.nextInt(end - start + 1) + start;
    }
}
