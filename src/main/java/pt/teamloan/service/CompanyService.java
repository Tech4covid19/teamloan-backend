package pt.teamloan.service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import pt.teamloan.authserver.AuthServerException;
import pt.teamloan.authserver.AuthServerService;
import pt.teamloan.authserver.AuthServerUser;
import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.CompanyEntity;

@ApplicationScoped
public class CompanyService {
	
	@Inject
	AuthServerService authServerService;
	
	@Transactional
	public CompletionStage<Void> register(@Valid CompanyEntity company) throws EntityAlreadyExistsException, AuthServerException {
		if(CompanyEntity.count("email", company.getEmail()) > 0) {
			throw new EntityAlreadyExistsException("Email already exists");
		}
		if(CompanyEntity.count("vat", company.getVat()) > 0) {
			throw new EntityAlreadyExistsException("VAT already exists");
		}
		company.setUuid(UUID.randomUUID());
		company.persist();
		
		// create on keycloak
		authServerService.createUser(new AuthServerUser(company.getUuid().toString(), company.getVat(), company.getEmail(), company.getPassword()));
		
		return CompletableFuture.completedFuture(null);
	}
	
	@Transactional
	public CompanyEntity getByUUID(String uuid) {
		return CompanyEntity.find("uuid", UUID.fromString(uuid)).firstResult();
	}
}
