package pt.teamloan.ws;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;

import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.model.ProspectEntity;
import pt.teamloan.service.CompanyService;

@Path("/company")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource {
	private static final Logger LOGGER = Logger.getLogger(CompanyResource.class.getName());
	
	@Inject
	CompanyService companyService;

	@POST
	@Counted
	@Bulkhead(value = 2, waitingTaskQueue = 2)
	@Asynchronous
	public CompletionStage<Response> post(CompanyEntity company) {
		try {
			LOGGER.info("POST /company: " + company.toString());
			CompletionStage<Void> keycloakCompletionStage = companyService.register(company);
			LOGGER.info("Successfully registered company: " + company.getUuid());
			return keycloakCompletionStage.thenApply(f -> Response.accepted().entity(new GenericResponse(company.getUuid())).build());
		} catch (ConstraintViolationException e) {
			LOGGER.log(Level.ERROR, "Company constraint validation!", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.BAD_REQUEST).entity(new GenericResponse(e)).build());
			return cf;
		} catch (EntityAlreadyExistsException e) {
			LOGGER.log(Level.ERROR, "Email or VAT already exists validation!", e);
			CompletableFuture<Response> cf = new CompletableFuture<Response>();
			cf.complete(Response.status(Status.CONFLICT).entity(new GenericResponse(e)).build());
			return cf;
		}
	}
	
	@GET
	@Path("/{uuid}")
	public CompanyEntity getByUUID(@PathParam("uuid") String uuid) {
		return companyService.getByUUID(uuid);
	}
}