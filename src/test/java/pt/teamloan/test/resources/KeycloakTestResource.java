//package pt.teamloan.test.resources;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.wait.strategy.Wait;
//
//import io.mobime.foundation.logs.utils.LoggingUtils;
//import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
//
//public class KeycloakTestResource implements QuarkusTestResourceLifecycleManager {
//
//    protected static final String AUTH_PATH = "/auth";
//    protected static final String KEYCLOAK_USER_VAR = "KEYCLOAK_USER";
//    protected static final String PROXY_ADDRESS_FORWARDING = "true";
//    protected static final String PROXY_ADDRESS_FORWARDING_VAR = "PROXY_ADDRESS_FORWARDING";
//    protected static final String KEYCLOAK_LOGLEVEL = "DEBUG";
//    protected static final String KEYCLOAK_LOGLEVEL_VAR = "KEYCLOAK_LOGLEVEL";
//    protected static final String KEYCLOAK_PASSWORD = "admin";
//    protected static final String KEYCLOAK_USER = "admin";
//    protected static final String KEYCLOAK_PASSWORD_VAR = "KEYCLOAK_PASSWORD";
//    protected static final String JBOSS_KEYCLOAK_IMAGE = "jboss/keycloak:3.4.3.Final";
//
//    private static GenericContainer authServer;
//
//    @Override
//    public Map<String, String> start() {
//        try {
//            authServer = new GenericContainer(JBOSS_KEYCLOAK_IMAGE) //
//                    .withExposedPorts(8080) //
//                    .withEnv(KEYCLOAK_USER_VAR, KEYCLOAK_USER) //
//                    .withEnv(KEYCLOAK_PASSWORD_VAR, KEYCLOAK_PASSWORD) //
//                    .withEnv(KEYCLOAK_LOGLEVEL_VAR, KEYCLOAK_LOGLEVEL) //
//                    .withEnv(PROXY_ADDRESS_FORWARDING_VAR, PROXY_ADDRESS_FORWARDING) //
//                    .waitingFor(Wait.forListeningPort()) //
//                    .waitingFor(Wait.forHttp(AUTH_PATH)) //
//                    .withStartupTimeout(Duration.ofMinutes(3));
//
//            authServer.start();
//            return new HashMap<>();
//        } catch (Exception e) {
//            LoggingUtils.writeException(getClass(), e);
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void stop() {
//        authServer.close();
//        authServer = null;
//    }
//
//    public static GenericContainer getAuthServer() {
//        return authServer;
//    }
//}
