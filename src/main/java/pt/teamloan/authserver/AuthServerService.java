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
     * @param realm The realm on which to creØßate the user
     * @param user
     *        The user details with its roles for each client if needed
     * @return response with created user id
     * @throws AuthServerException AuthServerException
     */
    public AuthServerResponse createUser(AuthServerUser user) throws AuthServerException;

}
