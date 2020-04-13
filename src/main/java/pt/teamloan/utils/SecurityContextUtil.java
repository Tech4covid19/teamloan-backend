package pt.teamloan.utils;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.SecurityContext;

import io.quarkus.smallrye.jwt.runtime.auth.QuarkusJwtCallerPrincipal;

@ApplicationScoped
public class SecurityContextUtil {
	
	private static final String COMPANY_UUID_CLAIM = "uuid";

	public String getCompanyUuid(SecurityContext ctx) {
		QuarkusJwtCallerPrincipal principal = (QuarkusJwtCallerPrincipal) ctx.getUserPrincipal();
		return principal.getClaim(COMPANY_UUID_CLAIM);
	}
}
