package pt.teamloan.ws;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;

import pt.teamloan.authserver.constants.RoleConstants;
import pt.teamloan.model.JobEntity;
import pt.teamloan.service.JobsService;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Bulkhead
@Timed
@Timeout(value = 5000)
public class JobsResource {
	@Inject
	JobsService jobsService;
	
	@GET
	@RolesAllowed({ RoleConstants.END_USER, RoleConstants.ADMIN })
	public List<JobEntity> get(@Context SecurityContext ctx) {
		return jobsService.listAll();
	}
}
