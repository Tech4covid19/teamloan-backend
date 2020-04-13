package pt.teamloan.ws;

import java.util.List;
import java.util.UUID;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;

import pt.teamloan.authserver.constants.RoleConstants;
import pt.teamloan.model.BusinessAreaEntity;
import pt.teamloan.service.BusinessAreaService;
import pt.teamloan.ws.response.GenericResponse;

@Path("/business-areas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Bulkhead
@Timed
@Timeout(value = 5000)
public class BusinessAreasResource {
	
	@Inject
	BusinessAreaService businessAreaService;
	
	@GET
	@PermitAll
	public List<BusinessAreaEntity> get(@Context SecurityContext ctx) {
		return businessAreaService.listAll();
	}
	
	@POST
	@RolesAllowed(RoleConstants.ADMIN)
	public GenericResponse post(@Context SecurityContext ctx, BusinessAreaEntity businessArea) {
		businessArea = businessAreaService.add(businessArea);
		return new GenericResponse(businessArea.getUuid());
	}
	
	@PATCH
	@Path("/{uuid}")
	@RolesAllowed(RoleConstants.ADMIN)
	public GenericResponse patch(@Context SecurityContext ctx, @PathParam("uuid") String uuid, BusinessAreaEntity businessArea) {
		if(businessArea.getUuid() == null) {
			businessArea.setUuid(UUID.fromString(uuid));
		}
		businessArea = businessAreaService.update(businessArea);
		return new GenericResponse(businessArea.getUuid());
	}
	
	@DELETE
	@RolesAllowed(RoleConstants.ADMIN)
	public GenericResponse delete(@Context SecurityContext ctx, BusinessAreaEntity businessArea) {
		businessArea = businessAreaService.update(businessArea);
		return new GenericResponse(businessArea.getUuid());
	}
}
