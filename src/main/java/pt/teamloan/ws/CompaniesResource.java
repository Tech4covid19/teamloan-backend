package pt.teamloan.ws;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
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
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;

import io.quarkus.smallrye.jwt.runtime.auth.QuarkusJwtCallerPrincipal;
import pt.teamloan.authserver.AuthServerException;
import pt.teamloan.authserver.constants.RoleConstants;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.service.CompanyService;
import pt.teamloan.ws.request.ForgotPasswordRequest;
import pt.teamloan.ws.request.ResetPasswordRequest;
import pt.teamloan.ws.response.GenericResponse;

@Path("/company")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Bulkhead
@Timed
@Timeout(value = 5000)
public class CompaniesResource {
	private static final Logger LOGGER = Logger.getLogger(CompaniesResource.class.getName());

	@Inject
	CompanyService companyService;

	@POST
	@Asynchronous
	@PermitAll
	@Bulkhead(value = 5, waitingTaskQueue = 5)
	@Timeout(value = 10000)
	public CompletionStage<Response> post(CompanyEntity company) throws AuthServerException {
		try {
			CompletionStage<Void> registerCompanyCompletionStage = companyService.register(company);
			return registerCompanyCompletionStage
					.thenApply(f -> Response.accepted().entity(new GenericResponse(company.getUuid())).build());
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
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Generic exception", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericResponse(e)).build());
			return cf;
		}
	}

	@GET
	@Path("/{uuid}")
	@RolesAllowed({ RoleConstants.END_USER, RoleConstants.ADMIN })
	public CompanyEntity getByUUID(@PathParam("uuid") String uuid, @Context SecurityContext ctx) {
		QuarkusJwtCallerPrincipal principal = (QuarkusJwtCallerPrincipal) ctx.getUserPrincipal();
		if (!principal.getClaim("uuid").equals(uuid)) {
			throw new ForbiddenException(
					"The user can only access is own resources! It is trying to get info about another user resource.");
		}
		return companyService.getByUUID(uuid);
	}

	@Path("/activation/{activationKey}")
	@POST
	@PermitAll
	@Bulkhead(value = 2, waitingTaskQueue = 2)
	public Response activation(@PathParam("activationKey") String activationKey) throws AuthServerException {
		try {
			CompanyEntity activatedCompany = companyService.activate(activationKey);
			return Response.ok(new GenericResponse(activatedCompany.getUuid())).build();
		} catch (NoResultException e) {
			LOGGER.log(Level.ERROR, "AuthServer exception", e);
			return Response.status(Status.NOT_FOUND).entity(new GenericResponse(e)).build();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Generic exception", e);
			return Response.serverError().entity(new GenericResponse(e)).build();
		}
	}

	@Path("/forgot-password")
	@POST
	@PermitAll
	@Bulkhead(value = 2, waitingTaskQueue = 2)
	public Response forgotPassword(ForgotPasswordRequest request) throws AuthServerException {
		try {
			companyService.forgotPassword(request.getEmail());
			return Response.accepted(new GenericResponse()).build();
		} catch (ConstraintViolationException e) {
			LOGGER.log(Level.WARNING, "Company constraint validation!", e);
			return Response.status(Status.BAD_REQUEST).entity(new GenericResponse(e)).build();
		} catch (NoResultException e) {
			LOGGER.log(Level.WARN, "No company for forgot password provided email: " + request.getEmail(), e);
			return Response.status(Status.NOT_FOUND).entity(new GenericResponse(e)).build();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Generic exception", e);
			return Response.serverError().entity(new GenericResponse(e)).build();
		}
	}

	@Path("/reset-password/{resetPasswordKey}")
	@POST
	@PermitAll
	@Bulkhead(value = 2, waitingTaskQueue = 2)
	public Response resetPassword(@PathParam("resetPasswordKey") String resetPasswordKey,
			ResetPasswordRequest resetPasswordRequest) throws AuthServerException {
		try {
			CompanyEntity resetCompany = companyService.resetPassword(resetPasswordKey, resetPasswordRequest.getPassword());
			return Response.ok(new GenericResponse(resetCompany.getUuid())).build();
		} catch (NoResultException e) {
			LOGGER.log(Level.ERROR, "Invalid reset password key.", e);
			return Response.status(Status.NOT_FOUND).entity(new GenericResponse(e)).build();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Generic exception", e);
			return Response.serverError().entity(new GenericResponse(e)).build();
		}
	}
}