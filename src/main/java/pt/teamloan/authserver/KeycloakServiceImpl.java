package pt.teamloan.authserver;

import java.util.Arrays;
import java.util.Collections;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import pt.teamloan.authserver.constants.RoleConstants;

/**
 * Implementation of AuthServerService specifically for keycloak authorization
 * server
 * 
 * @author nuno.alves
 *
 */
@ApplicationScoped
public class KeycloakServiceImpl implements AuthServerService {

	private static final String CLIENT_ID_TEAMLOAN_WEB_APP = "TEAMLOAN_WEB_APP";

	private static final String ATTRIBUTE_UUID = "uuid";

	private static final String ACTION_VERIFY_EMAIL = "VERIFY_EMAIL";

	@ConfigProperty(name = "keycloak.realm")
	String realm;

	@ConfigProperty(name = "keycloak.backend.client_id")
	String backendClientId;

	@ConfigProperty(name = "keycloak.backend.client_secret")
	String backendClientSecret;

	@ConfigProperty(name = "keycloak.auth-server-url")
	String authServerUrl;

	private Keycloak keycloak;

	@Override
	public AuthServerResponse createUser(AuthServerUser authServerUser) throws AuthServerException {
		lazyLoadKeycloakAdminClient();
		// Define user
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEnabled(true);
		userRepresentation.setUsername(authServerUser.getUsername());
		userRepresentation.setEmail(authServerUser.getEmail());
		userRepresentation.setAttributes(Collections.singletonMap(ATTRIBUTE_UUID, Arrays.asList(authServerUser.getUuid())));
		userRepresentation.setRealmRoles(Arrays.asList(RoleConstants.END_USER));
		userRepresentation.setRequiredActions(Arrays.asList(ACTION_VERIFY_EMAIL));

		CredentialRepresentation passwordCred = new CredentialRepresentation();
		passwordCred.setTemporary(false);
		passwordCred.setType(CredentialRepresentation.PASSWORD);
		passwordCred.setValue(authServerUser.getPassword());
		userRepresentation.setCredentials(Arrays.asList(passwordCred));
		
		// Get realm
		RealmResource realmResource = keycloak.realm(realm);
		UsersResource usersResource = realmResource.users();

		Response response = usersResource.create(userRepresentation);
		System.out.printf("Response: %s %s%n", response.getStatus(), response.getStatusInfo());
		System.out.println(response.getLocation());
		String userId = CreatedResponseUtil.getCreatedId(response);
		usersResource.get(userId).executeActionsEmail(CLIENT_ID_TEAMLOAN_WEB_APP, "https://frontend.teamloan.pt", 259200, Arrays.asList(ACTION_VERIFY_EMAIL));
		return new AuthServerResponse(userId);
	}

	private Keycloak lazyLoadKeycloakAdminClient() {
		if (this.keycloak == null) {
			this.keycloak = KeycloakBuilder.builder() //
					.serverUrl(authServerUrl) //
					.realm(realm) //
					.grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
					.clientId(backendClientId) //
					.clientSecret(backendClientSecret) //
					.build();
		}
		return this.keycloak;
	}
}
