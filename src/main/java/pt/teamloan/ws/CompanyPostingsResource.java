package pt.teamloan.ws;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logmanager.Level;
import org.jboss.logmanager.Logger;

import pt.teamloan.authserver.constants.RoleConstants;
import pt.teamloan.model.PostingEntity;
import pt.teamloan.service.PostingsService;
import pt.teamloan.utils.SecurityContextUtil;
import pt.teamloan.ws.response.GenericResponse;

@Path("/company/{companyUuid}/postings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Bulkhead
@Timed
@Timeout(value = 50000)
public class CompanyPostingsResource {
	private static final Logger LOGGER = Logger.getLogger(CompanyPostingsResource.class.getName());
	
	@Inject
	SecurityContextUtil securityContextUtil;
	
	@Inject
	PostingsService postingsService;
	
	@GET
	@RolesAllowed({RoleConstants.END_USER, RoleConstants.ADMIN})
	public List<PostingEntity> get(@PathParam("companyUuid") String companyUuid, @Context SecurityContext ctx) {
		validatePermissionForCompanyUuid(companyUuid, ctx);
		return postingsService.listAllForCompany(companyUuid);
	}
	
	@GET
	@Path("/{postingUuid}")
	@RolesAllowed({RoleConstants.END_USER, RoleConstants.ADMIN})
	public PostingEntity getByUuid(@PathParam("companyUuid") String companyUuid, @PathParam("postingUuid") String postingUuid, @Context SecurityContext ctx) {
		validatePermissionForCompanyUuid(companyUuid, ctx);
		return postingsService.findCompanyPosting(companyUuid, postingUuid);
	}
	
	@POST
	@RolesAllowed({RoleConstants.END_USER, RoleConstants.ADMIN})
	public Response post(@PathParam("companyUuid") String companyUuid, PostingEntity postingEntity, @Context SecurityContext ctx) {
		try {
			validatePermissionForCompanyUuid(companyUuid, ctx);
			postingEntity = postingsService.create(companyUuid, postingEntity);
			return Response.status(Status.CREATED).entity(new GenericResponse(postingEntity.getUuid())).build();
		} catch (ConstraintViolationException e) {
			LOGGER.log(Level.WARNING, "Posting constraint validation!", e);
			return Response.status(Status.BAD_REQUEST).entity(new GenericResponse(e)).build();
		} catch (ForbiddenException e) {
			LOGGER.log(Level.WARNING, "Permissions validation!", e);
			return Response.status(Status.FORBIDDEN).entity(new GenericResponse(e)).build();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Unexpected error.", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericResponse(e)).build();
		}
	}
	
	@Path("/{postingUuid}")
	@PATCH
	@RolesAllowed({RoleConstants.END_USER, RoleConstants.ADMIN})
	public Response patch(@PathParam("companyUuid") String companyUuid, @PathParam("postingUuid") String postingUuid, PostingEntity postingEntity, @Context SecurityContext ctx) {
		try {
			validatePermissionForCompanyUuid(companyUuid, ctx);
			postingEntity = postingsService.update(companyUuid, postingUuid, postingEntity);
			return Response.status(Status.CREATED).entity(new GenericResponse(postingEntity.getUuid())).build();
		} catch (ConstraintViolationException e) {
			LOGGER.log(Level.WARNING, "Posting constraint validation!", e);
			return Response.status(Status.BAD_REQUEST).entity(new GenericResponse(e)).build();
		} catch (ForbiddenException e) {
			LOGGER.log(Level.WARNING, "Permissions validation!", e);
			return Response.status(Status.FORBIDDEN).entity(new GenericResponse(e)).build();
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Unexpected error.", e);
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(new GenericResponse(e)).build();
		}
	}

	private void validatePermissionForCompanyUuid(String companyUuid, SecurityContext ctx) {
		String companyUuidFromSecurityCtx = securityContextUtil.getCompanyUuid(ctx);
		if(!companyUuidFromSecurityCtx.equals(companyUuid)) {
			throw new ForbiddenException("The user can only access is own resources! It is trying to do something on another user resource.");
		}
	}

}