package pt.teamloan.ws;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;

import pt.teamloan.authserver.constants.RoleConstants;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.ProspectEntity;
import pt.teamloan.service.ProspectService;
import pt.teamloan.ws.response.GenericResponse;

@Path("/prospect")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Bulkhead
@Timed
@Timeout(value = 10000)
public class ProspectsResource {
	private static final Logger LOGGER = Logger.getLogger(ProspectsResource.class.getName());
	
	@Inject
	ProspectService prospectService;

	@POST
	@Bulkhead(value = 2, waitingTaskQueue = 2)
	@Asynchronous
	@PermitAll
	public CompletionStage<Response> post(ProspectEntity prospectEntity) {
		try {
			CompletionStage<Void> sendMailCompletionStage = prospectService.registerProspect(prospectEntity);
			return sendMailCompletionStage.thenApply(f -> Response.accepted().entity(new GenericResponse()).build());
		} catch (ConstraintViolationException e) {
			LOGGER.log(Level.WARNING, "Prospect constraint validation!", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.BAD_REQUEST).entity(new GenericResponse(e)).build());
			return cf;
		} catch (EntityAlreadyExistsException e) {
			LOGGER.log(Level.WARNING, "Email already exists validation!", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.CONFLICT).entity(new GenericResponse(e)).build());
			return cf;
		}
	}
	
	@Path("/inform")
	@POST
	@Bulkhead(value = 1, waitingTaskQueue = 1)
	@RolesAllowed(RoleConstants.ADMIN)
	public Response inform() {
		try {
			CompletionStage<Void> sendMailCompletionStage = prospectService.sendInformationEmails();
			sendMailCompletionStage.exceptionally(f -> {
				LOGGER.log(Level.ERROR, "Error informing prospects by email!", f);
				return null;
			});
			return Response.accepted().entity(new GenericResponse()).build();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Unexpected error!", e);
			return Response.serverError().entity(new GenericResponse(e)).build();
		}
	}

	@Path("/reinform")
	@POST
	@Bulkhead(value = 1, waitingTaskQueue = 1)
	@RolesAllowed(RoleConstants.ADMIN)
	public Response reinform() {
		try {
			CompletionStage<Void> sendMailCompletionStage = prospectService.sendReinformEmails();
			sendMailCompletionStage.exceptionally(f -> {
				LOGGER.log(Level.ERROR, "Error REinforming prospects by email!", f);
				return null;
			});
			return Response.accepted().entity(new GenericResponse()).build();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Unexpected error!", e);
			return Response.serverError().entity(new GenericResponse(e)).build();
		}
	}
}