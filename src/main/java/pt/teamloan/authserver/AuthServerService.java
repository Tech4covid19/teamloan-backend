package pt.teamloan.authserver;

/**
 * Interface with all the needed methods for the authorization server client
 * 
 * @author nuno.alves
 *
 */
public interface AuthServerService {

    /**
     * Creates an user on a given realm.
     * 
     * @param realm The realm on which to create the user
     * @param user
     *        The user details with its roles for each client if needed
     * @return response with created user id
     * @throws AuthServerException AuthServerException
     */
    public AuthServerResponse createUser(AuthServerUser user) throws AuthServerException;

    /**
     * Marks the user as having the email verified.
     * 
     * @param subjectUuid The user to update
     * @return response with updated user id
     * @throws AuthServerException AuthServerException
     */
	public AuthServerResponse updateEmailToVerified(String subjectUuid) throws AuthServerException;

    /**
     * Changes the user password
     * 
     * @param realm realm
     * @param userId userId
     * @param password new password
     * @throws AuthServerException AuthServerException
     */
    public void resetPassword(String userId, String password) throws AuthServerException;;
}
