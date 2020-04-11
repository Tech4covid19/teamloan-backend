package pt.teamloan.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.panache.common.Sort;
import pt.teamloan.model.DistrictEntity;
import pt.teamloan.model.MunicipalityEntity;

@ApplicationScoped
public class AddressesService {

	public List<DistrictEntity> listDistrics() {
		return DistrictEntity.listAll(Sort.ascending("name"));
	}
	
	public List<MunicipalityEntity> listMunicipalities(String districtUUID) {
		return MunicipalityEntity.find("district.uuid", Sort.ascending("name"), UUID.fromString(districtUUID)).list();
	}
}
