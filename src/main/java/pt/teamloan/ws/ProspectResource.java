package pt.teamloan.ws;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

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
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;

import pt.teamloan.db.ProspectEntity;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.service.ProspectService;

@Path("/prospect")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProspectResource {
	private static final Logger LOGGER = Logger.getLogger(ProspectResource.class.getName());
	
	@Inject
	ProspectService prospectService;

	@POST
	@Counted
	@Bulkhead(value = 2, waitingTaskQueue = 2)
	@Asynchronous
	public CompletionStage<Response> post(ProspectEntity prospectEntity) {
		try {
			LOGGER.info("POST /prospect with email: " + prospectEntity.email);
			CompletionStage<Void> sendMailCompletionStage = prospectService.registerProspect(prospectEntity);
			LOGGER.info("Successfully sent prospect email to: " + prospectEntity.email);
			return sendMailCompletionStage.thenApply(f -> Response.accepted().entity(new GenericResponse()).build());
		} catch (ConstraintViolationException e) {
			LOGGER.log(Level.ERROR, "Prospect constraint validation!", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.BAD_REQUEST).entity(new GenericResponse(e)).build());
			return cf;
		} catch (EntityAlreadyExistsException e) {
			LOGGER.log(Level.ERROR, "Email already exists validation!", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.CONFLICT).entity(new GenericResponse(e)).build());
			return cf;
		}
	}
}