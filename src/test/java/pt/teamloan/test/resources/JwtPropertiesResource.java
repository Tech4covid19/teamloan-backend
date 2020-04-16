package pt.teamloan.test.resources;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class JwtPropertiesResource implements QuarkusTestResourceLifecycleManager {

	@Override
	public Map<String, String> start() {
		Map<String, String> props = new HashMap<>();
		props.put("mp.jwt.verify.publickey.location", "jwt/testPublicKey.pem");
		props.put("mp.jwt.verify.issuer", "https://test.teamloan.pt");
		return props;
	}

	@Override
	public void stop() {
		
	}

}
