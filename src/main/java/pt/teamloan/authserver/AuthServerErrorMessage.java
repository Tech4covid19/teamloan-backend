package pt.teamloan.authserver;

import java.text.MessageFormat;

/**
 * Error messages for the authorization server client
 * 
 * @author nuno.alves
 *
 */
public enum AuthServerErrorMessage {
    ERROR_READING_CONFIGS("An error has ocurred obtaining the admin client configs. {0}"), //
    ERROR_AUTH_SERVER_INVOCATION("An error has ocurred invoking authorization server: Status code {0} {1}"), //
    MISSING_REQUIRED_PARAMETER("Missing required parameter: \"{0}\"");
    private String message;

    private AuthServerErrorMessage(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }

    /**
     * Get formatted message
     * 
     * @param parameters replace params
     * @return formatted message
     */
    public String getMessage(Object... parameters) {
        return MessageFormat.format(this.message, parameters);
    }

}
