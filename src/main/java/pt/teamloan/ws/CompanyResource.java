package pt.teamloan.ws;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;

import io.quarkus.smallrye.jwt.runtime.auth.QuarkusJwtCallerPrincipal;
import pt.teamloan.authserver.AuthServerException;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.service.CompanyService;
import pt.teamloan.ws.response.GenericResponse;

@Path("/company")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CompanyResource {
	private static final Logger LOGGER = Logger.getLogger(CompanyResource.class.getName());
	
	@Inject
	CompanyService companyService;

	@POST
	@Counted
	@Bulkhead(value = 2, waitingTaskQueue = 2)
	@Asynchronous
	@PermitAll
	public CompletionStage<Response> post(CompanyEntity company) throws AuthServerException {
		try {
			CompletionStage<Void> keycloakCompletionStage = companyService.register(company);
			return keycloakCompletionStage.thenApply(f -> Response.accepted().entity(new GenericResponse(company.getUuid())).build());
		} catch (ConstraintViolationException e) {
			LOGGER.log(Level.WARNING, "Company constraint validation!", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.BAD_REQUEST).entity(new GenericResponse(e)).build());
			return cf;
		} catch (EntityAlreadyExistsException e) {
			LOGGER.log(Level.WARNING, "Email or VAT already exists validation!", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.CONFLICT).entity(new GenericResponse(e)).build());
			return cf;
		}
	}
	
	@GET
	@Path("/{uuid}")
	@RolesAllowed({"END_USER"})
	public CompanyEntity getByUUID(@PathParam("uuid") String uuid, @Context SecurityContext ctx) {
		QuarkusJwtCallerPrincipal principal = (QuarkusJwtCallerPrincipal) ctx.getUserPrincipal();
		if(!principal.getClaim("uuid").equals(uuid)) {
			throw new ForbiddenException("The user can only access is own resources! It is trying to get info about another user resource.");
		}
		return companyService.getByUUID(uuid);
	}
}