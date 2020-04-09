package pt.teamloan.authserver;

/**
 * Exception thrown by the authorization server client lib
 * 
 * @author nuno.alves
 *
 */
public class AuthServerException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Contructor with AuthServerErrorMessage
     * 
     * @param errorMessage error
     */
    public AuthServerException(AuthServerErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }

    /**
     * Contructor with AuthServerErrorMessage and message params
     * 
     * @param errorMessage error
     * @param params params for formatting
     */
    public AuthServerException(AuthServerErrorMessage errorMessage, Object... params) {
        super(errorMessage.getMessage(params));
    }

    /**
     * Contructor with AuthServerErrorMessage and exception cause
     * 
     * @param errorMessage error
     * @param t exception cause
     */
    public AuthServerException(AuthServerErrorMessage errorMessage, Throwable t) {
        super(errorMessage.getMessage(), t);
    }

    /**
     * Contructor with AuthServerErrorMessage, exception cause and
     * 
     * @param errorMessage errorMessage
     * @param t Exception cause
     * @param params params for message formatting
     */
    public AuthServerException(AuthServerErrorMessage errorMessage, Throwable t, Object... params) {
        super(errorMessage.getMessage(params), t);
    }
}
