package pt.teamloan.authserver;

/**
 * POJO representing an authorization server user
 * 
 * @author nuno.alves
 *
 */
public class AuthServerUser {

	private String subjectUuid;
	private String username;
	private String email;
	private String password;
	private String uuid;

	/**
	 * Construtor with all the params for an AuthServerUser
	 * 
	 */
	public AuthServerUser(String v, String username, String email, String password, String uuid) {
		super();
		this.subjectUuid = subjectUuid;
		this.username = username;
		this.email = email;
		this.password = password;
		this.uuid = uuid;
	}

	public String getSub() {
		return subjectUuid;
	}

	public void setSub(String subjectUuid) {
		this.subjectUuid = subjectUuid;
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