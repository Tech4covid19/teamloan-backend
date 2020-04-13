package pt.teamloan.ws;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import pt.teamloan.model.DistrictEntity;
import pt.teamloan.model.MunicipalityEntity;
import pt.teamloan.service.AddressesService;

@Path("/addresses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
@Bulkhead
@Timed
@Timeout(value = 5000)
public class AddressesResource {

	@Inject
	AddressesService addressesService;

	@GET
	@Path("/districts")
	@RolesAllowed({ RoleConstants.END_USER, RoleConstants.ADMIN })
	public List<DistrictEntity> getDistricts(@Context SecurityContext ctx) {
		return addressesService.listDistrics();
	}

	@GET
	@Path("/districts/{uuid}/municipalities")
	@RolesAllowed({ RoleConstants.END_USER, RoleConstants.ADMIN })
	public List<MunicipalityEntity> getMunicipalities(@Context SecurityContext ctx,
			@PathParam("uuid") String districtUUID) {
		return addressesService.listMunicipalities(districtUUID);
	}
}
