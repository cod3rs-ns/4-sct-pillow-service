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
    public static final String ERROR_MSG_NOT_OWNER = "You do not have owner rights to perform action on this entity";
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
    public static final String ERROR_MSG_CANNOT_RATE_OWN_COMPANY_ANNOUNCEMENT = "You cannot rate your own or announcements by your own company";
    public static final String ERROR_MSG_CANNOT_POST_MULTIPLE_REPORTS = "You cannot post multiple reports for the same advertisement";
    public static final String ERROR_MSG_REPORT_ALREADY_RESOLVED = "You cannot modify status of resolved report";
    public static final String ERROR_MSG_PROVIDED_UNKNOWN_REPORT_STATUS = "You specified unknown report status";
    public static final String ERROR_MSG_EMAIL_ALREADY_IN_USE = "Email is already in use";
    public static final String ERROR_MSG_USERNAME_ALREADY_IN_USE = "Username is already in use";

    // ================================================ Error codes ================================================= //
    /**
     * Error code representing situation in which user cannot perform action because he is not
     * the <em>owner</em> of the entity.
     */
    public static final Integer ERROR_CODE_NOT_OWNER = 1001;

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
     * announcement that have already been verified.
     */
    public static final Integer ERROR_CODE_REPORT_VERIFIED_ANNOUNCEMENT = 1005;

    /**
     * Error code representing situation in which user tries to extend
     * the expiration date of announcement but fails because he didn't provide
     * new expiration date.
     */
    public static final Integer ERROR_CODE_NO_EXPIRATION_DATE = 1006;

    /**
     * Error code representing situation in which user tries update announcement
     * but doesn't provide date in valid format.
     */
    public static final Integer ERROR_CODE_INVALID_DATE_FORMAT = 1007;

    /**
     * Error code representing situation in which user tries to extend announcement
     * date, but provide new date that have already passed.
     */
    public static final Integer ERROR_CODE_PAST_DATE = 1008;

    /**
     * Error code representing situation in which user tries to verify announcement
     * which have already been verified.
     */
    public static final Integer ERROR_CODE_ALREADY_VERIFIED = 1009;

    /**
     * Error code representing situation in which user tries to perform company
     * based action, but he is not member of the same company, or any company at all.
     */
    public static final Integer ERROR_CODE_NOT_MEMBER_OF_COMPANY = 1010;

    /**
     * Error code representing situation in which user tries to request company membership
     * for more than once.
     */
    public static final Integer ERROR_CODE_ALREADY_REQUESTED_MEMBERSHIP = 1011;

    /**
     * Error code representing situation in which user tries to verify another user
     * company membership status, but that other user didn't request membership at all.
     */
    public static final Integer ERROR_CODE_USER_DID_NOT_REQUEST_MEMBERSHIP = 1012;

    /**
     * Error code representing situation in which user tries to verify another user
     * company membership status, but doesn't have permission to perform that action.
     */
    public static final Integer ERROR_CODE_NO_PERMISSION_TO_RESOLVE_MEMBERSHIP = 1013;

    /**
     * Error code representing situation in which user tries to rate its own, or announcements
     * from own company.
     */
    public static final Integer ERROR_CODE_CANNOT_RATE_OWN_COMPANY_ANNOUNCEMENT = 1014;

    /**
     * Error code representing situation in which user tries to post more than one report
     * for the same announcement.
     */
    public static final Integer ERROR_CODE_CANNOT_POST_MULTIPLE_REPORTS = 1015;

    /**
     * Error code representing situation in which user tries to resolve report which
     * has already been resolved by another user.
     */
    public static final Integer ERROR_CODE_REPORT_ALREADY_RESOLVED = 1016;

    /**
     * Error code representing situation in which user tries to assign new status to report,
     * but that status is not allowed by possible report types
     *
     * @see Constants.ReportStatus
     */
    public static final Integer ERROR_CODE_PROVIDED_UNKNOWN_REPORT_STATUS = 1017;

    /**
     * Error code representing situation when new user tries to register to our service by
     * providing email that has already been occupied by another user.
     */
    public static final Integer ERROR_CODE_EMAIL_ALREADY_IN_USE = 1018;

    /**
     * Error code representing situation when new user tries to register to our service by
     * providing username that has already been occupied by another user.
     */
    public static final Integer ERROR_CODE_USERNAME_ALREADY_IN_USE = 1019;

    private HeaderUtil() {
    }

    /**
     * Creates Headers with specific alert entry.
     *
     * @param message explains reason for alert
     * @param param   provides additional information about alert
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
    public static HttpHeaders failure(String entityName, Integer errorKey, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(SCT_HEADER_ALERT, defaultMessage);
        headers.add(SCT_HEADER_PARAMS, entityName);
        headers.add(SCT_HEADER_ERROR_KEY, errorKey.toString());
        return headers;
    }
}
