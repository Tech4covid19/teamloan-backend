package pt.teamloan.authserver;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;
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

	private static final Logger LOGGER = Logger.getLogger(KeycloakServiceImpl.class.getName());
	
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
		UserRepresentation userRepresentation = buildUserRepresentation(authServerUser);
		
		// Get realm resources
		RealmResource realmResource = keycloak.realm(realm);
		UsersResource usersResource = realmResource.users();
		
		// Create user
		String subjectUuid = executeCreateUserRequest(userRepresentation, usersResource);
		LOGGER.log(Level.DEBUG, "Created user " + subjectUuid + " on authorization server. Adding END_USER role...");
		
		// Assign END_USER role
		addRoleToUser(usersResource, subjectUuid, RoleConstants.END_USER);
		LOGGER.log(Level.DEBUG, "Successfully added END_USER role to user " + subjectUuid);
		
		return new AuthServerResponse(subjectUuid);
	}
	
	@Override
	@Retry(maxRetries = 3, delay = 500, delayUnit = ChronoUnit.MILLIS)
	public AuthServerResponse updateEmailToVerified(String subjectUuid) throws AuthServerException {
		UserResource userResource = keycloak.realm(realm).users().get(subjectUuid);
		UserRepresentation userRepresentation = userResource.toRepresentation();
		userRepresentation.setEmailVerified(true);
		userResource.update(userRepresentation);
		return new AuthServerResponse(userRepresentation.getId());
	}

	@Retry(maxRetries = 3, delay = 500, delayUnit = ChronoUnit.MILLIS)
	protected String executeCreateUserRequest(UserRepresentation userRepresentation, UsersResource usersResource) {
		Response response = usersResource.create(userRepresentation);
		String userId = CreatedResponseUtil.getCreatedId(response);
		return userId;
	}

	@Retry(maxRetries = 3, delay = 500, delayUnit = ChronoUnit.MILLIS, abortOn = AuthServerException.class)
	protected void addRoleToUser(UsersResource usersResource, String userId, String roleName) throws AuthServerException {
		List<RoleRepresentation> availableRoles = usersResource.get(userId).roles().realmLevel().listAvailable();
		if(availableRoles != null && !availableRoles.isEmpty()) {
			Optional<RoleRepresentation> endUserRoleOptional = availableRoles.stream().filter(r -> roleName.equals(r.getName())).findAny();
			if(endUserRoleOptional.isEmpty()) {
				throw new AuthServerException(AuthServerErrorMessage.MISSING_END_USER_ROLE, roleName, realm, userId);
			} else {
				usersResource.get(userId).roles().realmLevel().add(Arrays.asList(endUserRoleOptional.get()));
			}
		} else {
			LOGGER.log(Level.DEBUG, "No available roles to assign to user " + userId);
			throw new AuthServerException(AuthServerErrorMessage.MISSING_END_USER_ROLE, roleName, realm, userId);
		}
	}

	private UserRepresentation buildUserRepresentation(AuthServerUser authServerUser) {
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setEnabled(true);
		userRepresentation.setUsername(authServerUser.getUsername());
		userRepresentation.setEmail(authServerUser.getEmail());
		userRepresentation.setAttributes(Collections.singletonMap(ATTRIBUTE_UUID, Arrays.asList(authServerUser.getUuid())));
		userRepresentation.setRealmRoles(Arrays.asList(RoleConstants.END_USER));

		CredentialRepresentation passwordCred = new CredentialRepresentation();
		passwordCred.setTemporary(false);
		passwordCred.setType(CredentialRepresentation.PASSWORD);
		passwordCred.setValue(authServerUser.getPassword());
		userRepresentation.setCredentials(Arrays.asList(passwordCred));
		return userRepresentation;
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
