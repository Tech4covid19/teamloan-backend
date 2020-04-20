package pt.teamloan.ws;

import java.util.List;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logmanager.Logger;

import io.quarkus.panache.common.Page;
import pt.teamloan.authserver.constants.RoleConstants;
import pt.teamloan.exception.TeamLoanException;
import pt.teamloan.model.PostingEntity;
import pt.teamloan.model.enums.Intent;
import pt.teamloan.service.PostingsService;
import pt.teamloan.utils.SecurityContextUtil;

@Path("/postings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Bulkhead
@Timed
@Timeout(value = 5000)
public class PostingsResource {
	private static final Logger LOGGER = Logger.getLogger(PostingsResource.class.getName());

	@Inject
	SecurityContextUtil securityContextUtil;

	@Inject
	PostingsService postingsService;

	@GET
	@RolesAllowed({ RoleConstants.END_USER, RoleConstants.ADMIN })
	public List<PostingEntity> get(@QueryParam("page-number") Integer pageNumber,
			@QueryParam("page-size") Integer pageSize, @QueryParam("intent") Intent intent,
			@QueryParam("business-area") String businessAreaUuid, @QueryParam("district") String districtUuid,
			@QueryParam("municipality") String municipalityUuid, @QueryParam("job") String jobUuid,
			@Context SecurityContext ctx) throws TeamLoanException {
		
		Integer effectivePageIndex = pageNumber == null ? 0 : pageNumber - 1;
		Integer effectivePageSize = pageSize == null ? 10 : pageSize;
		Page page = Page.of(effectivePageIndex, effectivePageSize);
		
		return postingsService.findPaged(page, intent, businessAreaUuid, districtUuid, municipalityUuid, jobUuid);
	}
	
	@Path("/{uuid}")
	@GET
	@RolesAllowed({ RoleConstants.END_USER, RoleConstants.ADMIN })
	public PostingEntity getByUUID(@PathParam("uuid") String postingUuid,
			@Context SecurityContext ctx) throws TeamLoanException {
		return postingsService.findPostingByUuid(postingUuid);
	}
}