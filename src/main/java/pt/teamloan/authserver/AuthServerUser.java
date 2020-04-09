package pt.teamloan.authserver;

/**
 * POJO representing an authorization server user
 * 
 * @author nuno.alves
 *
 */
public class AuthServerUser {

	private String uuid;
	private String username;
	private String email;
	private String password;

	/**
	 * Construtor with all the params for an AuthServerUser
	 * 
	 */
	public AuthServerUser(String uuid, String username, String email, String password) {
		super();
		this.uuid = uuid;
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}