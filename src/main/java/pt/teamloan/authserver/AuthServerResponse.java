package pt.teamloan.authserver;
/**
 * Generic response format for all the client responses
 * 
 * @author nuno.alves
 *
 */
public class AuthServerResponse {

    String id;
    /**
     * Constructor for response with id
     * 
     * @param id created/updated resource id
     */
    public AuthServerResponse(String id) {
        super();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
