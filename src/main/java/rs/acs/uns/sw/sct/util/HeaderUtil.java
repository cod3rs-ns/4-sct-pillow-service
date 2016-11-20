package rs.acs.uns.sw.sct.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    public static final String ANNOUNCEMENT = "announcement";
    public static final String COMMENT = "comment";
    public static final String COMPANY = "company";
    public static final String MARK = "mark";
    public static final String USER = "user";
    public static final String REAL_ESTATE = "real_estate";
    public static final String REPORT = "report";


    private HeaderUtil() {
    }

    /**
     * Creates Headers with specific alert entry.
     *
     * @param message alert message - header value
     * @param param   alert parameters - header value
     * @return HttpHeaders
     */
    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-awt-test-alert", message);
        headers.add("X-awt-test-params", param);
        return headers;
    }

    /**
     * Creates Headers with specific Entity creation alert.
     *
     * @param entityName name of the entity - header value
     * @param param      other parameters - header value
     * @return HttpHeaders
     */
    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("A new " + entityName + " is created with identifier " + param, param);
    }

    /**
     * Creates Headers with specific Entity update alert.
     *
     * @param entityName name of the entity - header value
     * @param param      other parameters - header value
     * @return HttpHeaders
     */
    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is updated with identifier " + param, param);
    }

    /**
     * Creates Headers with specific Entity deletion alert.
     *
     * @param entityName name of the entity - header value
     * @param param      other parameters - header value
     * @return HttpHeaders
     */
    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is deleted with identifier " + param, param);
    }


    /**
     * Creates Headers with specific failure alers
     *
     * @param entityName     name of the entity - header value
     * @param errorKey       error key
     * @param defaultMessage default message to be shown - header value
     * @return HttpHeader
     */
    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-sct-app-alert", defaultMessage);
        headers.add("X-sct-app-params", entityName);
        headers.add("X-sct-app-error-key", errorKey);
        return headers;
    }
}
