package pt.teamloan.service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import pt.teamloan.authserver.AuthServerService;
import pt.teamloan.authserver.AuthServerUser;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.exception.GenericException;
import pt.teamloan.model.BusinessAreaEntity;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.utils.UUIDMapper;

@ApplicationScoped
public class CompanyService {
	
	@Inject
	AuthServerService authServerService;
	
	@Inject
	UUIDMapper uuidMapper;
	
	@Transactional
	public CompletionStage<Void> register(@Valid CompanyEntity company) throws Exception {
		if(CompanyEntity.count("email", company.getEmail()) > 0) {
			throw new EntityAlreadyExistsException("Email already exists");
		}
		if(CompanyEntity.count("vat", company.getVat()) > 0) {
			throw new EntityAlreadyExistsException("VAT already exists");
		}
		company.setUuid(UUID.randomUUID());
		setBusinessAreaId(company);
		company.persist();
		
		// create on keycloak
		authServerService.createUser(new AuthServerUser(company.getUuid().toString(), company.getVat(), company.getEmail(), company.getPassword()));
		
		return CompletableFuture.completedFuture(null);
	}

	private void setBusinessAreaId(CompanyEntity company) throws GenericException {
		if(company.getBusinessArea() != null && company.getBusinessArea().getUuid() != null) {
			UUID uuid = company.getBusinessArea().getUuid();
			company.setBusinessArea(null);
			Integer id = uuidMapper.mapToId(uuid, BusinessAreaEntity.class);
			BusinessAreaEntity businessArea = new BusinessAreaEntity();
			businessArea.setId(id);
			company.setBusinessArea(businessArea);
		}
	}
	
	public CompanyEntity getByUUID(String uuid) {
		return CompanyEntity.find("uuid", UUID.fromString(uuid)).firstResult();
	}
}
