package rs.acs.uns.sw.sct.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
public final class HeaderUtil {

    // ========================== Header names ========================== //
    public static final String SCT_HEADER_ALERT = "X-SCT-Alert";
    public static final String SCT_HEADER_PARAMS = "X-SCT-Params";
    public static final String SCT_HEADER_ERROR_KEY = "X-SCT-Error-Key";

    // ===========================================  Common error messages =========================================== //
    public static final String ERROR_MSG_NOT_OWNER = "You do not owner rights on this entity";
    public static final String ERROR_MSG_ID_EXISTS = "Entity with provided ID already exists";
    public static final String ERROR_MSG_CUSTOM_ID = "A new entity cannot have custom ID";
    public static final String ERROR_MSG_NON_EXISTING_ENTITY = "There is no entity with the ID you specified";
    public static final String ERROR_MSG_REPORT_VERIFIED_ANNOUNCEMENT = "You cannot report verified announcement";
    public static final String ERROR_MSG_NO_EXPIRATION_DATE = "Object must contain expirationDate attribute";
    public static final String ERROR_MSG_INVALID_DATE_FORMAT = "Date must be in format dd/MM/yyyy";
    public static final String ERROR_MSG_PAST_DATE = "Modified date must be after today";
    public static final String ERROR_MSG_ALREADY_VERIFIED = "Entity is already verified";
    public static final String ERROR_MSG_NOT_MEMBER_OF_COMPANY = "You are not member of company which you are trying to update";
    public static final String ERROR_MSG_ALREADY_REQUESTED_MEMBERSHIP = "Already requested company membership. Set request param confirmed=True to overwrite previous request";
    public static final String ERROR_MSG_USER_DID_NOT_REQUEST_MEMBERSHIP = "User with this ID did not request membership";
    public static final String ERROR_MSG_NO_PERMISSION_TO_RESOLVE_MEMBERSHIP = "You do not have permission to resolve membership status";
    public static final String ERROR_MSG_CANNOT_RATE_OWN_COMPANY_ANNOUNCEMENT = "You cannot rate your own company's announcement";
    public static final String ERROR_MSG_CANNOT_POST_MULTIPLE_REPORTS = "You cannot post multiple reports for the same advertisement";
    public static final String ERROR_MSG_REPORT_ALREADY_RESOLVED = "Cannot modify status of resolved report";
    public static final String ERROR_MSG_PROVIDED_UNKNOWN_REPORT_STATUS = "You have specified unknown report status.";
    // ================================================ Error codes ================================================= //
    /**
     * Error code representing situation in which user cannot perform action because he is not
     * the <em>owner</em> of the entity.
     */
    public static final Integer ERROR_CODE_NOT_OWNER = 1001;

    /**
     * Error code representing situation in which user cannot perform creation of entity
     * because entity with same ID exists.
     */
    public static final Integer ERROR_CODE_ID_EXISTS = 1002;

    /**
     * Error code representing situation in which user cannot perform creation of entity
     * because he provided custom entity ID.
     */
    public static final Integer ERROR_CODE_CUSTOM_ID = 1003;

    /**
     * Error code representing situation in which user tries to create report for announcement
     * which does not exists.
     */
    public static final Integer ERROR_CODE_NON_EXISTING_ENTITY = 1004;

    /**
     * Error code representing situation in which user tries to report
     * announcement that is already been verified.
     */
    public static final Integer ERROR_CODE_REPORT_VERIFIED_ANNOUNCEMENT = 1005;

    public static final Integer ERROR_CODE_NO_EXPIRATION_DATE = 1006;

    public static final Integer ERROR_CODE_INVALID_DATE_FORMAT = 1007;

    public static final Integer ERROR_CODE_PAST_DATE = 1008;

    public static final Integer ERROR_CODE_ALREADY_VERIFIED = 1009;

    public static final Integer ERROR_CODE_NOT_MEMBER_OF_COMPANY = 1010;

    public static final Integer ERROR_CODE_ALREADY_REQUESTED_MEMBERSHIP = 1011;

    public static final Integer ERROR_CODE_USER_DID_NOT_REQUEST_MEMBERSHIP = 1012;

    public static final Integer ERROR_CODE_NO_PERMISSION_TO_RESOLVE_MEMBERSHIP = 1013;

    public static final Integer ERROR_CODE_CANNOT_RATE_OWN_COMPANY_ANNOUNCEMENT = 1014;

    public static final Integer ERROR_CODE_CANNOT_POST_MULTIPLE_REPORTS = 1015;

    public static final Integer ERROR_CODE_REPORT_ALREADY_RESOLVED = 1016;

    public static final Integer ERROR_CODE_PROVIDED_UNKNOWN_REPORT_STATUS = 1017;

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
