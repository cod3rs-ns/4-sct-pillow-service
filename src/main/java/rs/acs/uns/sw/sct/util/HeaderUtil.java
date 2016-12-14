package rs.acs.uns.sw.sct.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    // ========================== Header names ========================== //
    private static final String SCT_HEADER_ALERT = "X-sct-alert";
    private static final String SCT_HEADER_PARAMS = "X-sct-params";
    private static final String SCT_HEADER_ERROR_KEY = "X-sct-error-key";

    // ====================================== Common error messages ======================================= //
    public static final String ERROR_MSG_UPDATE_DENIED = "You do not have permit to update this entity.";

    // ============================================== Error codes ============================================== //
    /**
     * Code which receives user who cannot perform action because he is not the the <em>owner</em> of the entity
     */
    public static final Integer ERROR_CODE_NOT_OWNER = 1001;

    private HeaderUtil() {
    }

    /**
     * Creates Headers with specific alert entry.
     *
     * @param message explains reason for alert
     * @param param  provides additional information about alert
     * @return HttpHeaders
     */
    private static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SCT_HEADER_ALERT, message);
        headers.add(SCT_HEADER_PARAMS, param);
        return headers;
    }

    /**
     * Creates Headers with specific Entity creation alert.
     *
     * @param entityName name of the entity which instance is created and flushed to database
     * @param param      other parameters which provides additional info,
     *                   such as identifier assigned to the <em>newly created entity</em>
     * @return HttpHeaders
     */
    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("A new " + entityName + " is created with identifier " + param, param);
    }

    /**
     * Creates Headers with specific Entity update alert.
     *
     * @param entityName name of the entity which is updated
     * @param param      other parameters which provides additional info,
     *                   such as identifier of the <em>newly updated entity</em>
     * @return HttpHeaders
     */
    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is updated with identifier " + param, param);
    }

    /**
     * Creates Headers with specific Entity deletion alert.
     *
     * @param entityName name of the entity which is deleted
     * @param param      other parameters which provides additional info,
     *                   such as identifier of the <em>deleted entity</em>
     * @return HttpHeaders
     */
    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("A " + entityName + " is deleted with identifier " + param, param);
    }


    /**
     * Creates Headers with specific failure alerts. This method is used to better explain reason for
     * unsuccessful method invocation.
     *
     * @param entityName     name of the entity on which action should have been performed
     * @param errorKey       key from the above <em>ERROR CODES</em>
     * @param defaultMessage default message to be shown - value from the above <em>ERROR MESSAGES</em>
     * @return HttpHeader
     */
    public static HttpHeaders createFailureAlert(String entityName, Integer errorKey, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SCT_HEADER_ALERT, defaultMessage);
        headers.add(SCT_HEADER_PARAMS, entityName);
        headers.add(SCT_HEADER_ERROR_KEY, errorKey.toString());
        return headers;
    }
}
