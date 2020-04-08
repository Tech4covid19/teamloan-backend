package pt.teamloan.service;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;

import pt.teamloan.exception.EntityAlreadyExistsException;
import pt.teamloan.model.CompanyEntity;

@ApplicationScoped
public class CompanyService {
	
	@Transactional
	public CompletionStage<Void> register(@Valid CompanyEntity company) throws EntityAlreadyExistsException {
		if(CompanyEntity.count("email", company.getEmail()) > 0) {
			throw new EntityAlreadyExistsException("Email already exists");
		}
		if(CompanyEntity.count("vat", company.getVat()) > 0) {
			throw new EntityAlreadyExistsException("VAT already exists");
		}
		company.setUuid(UUID.randomUUID());
		company.persist();
		// create on keycloak
		return CompletableFuture.completedFuture(null);
	}
	
	@Transactional
	public CompanyEntity getByUUID(String uuid) {
		return CompanyEntity.find("uuid", UUID.fromString(uuid)).firstResult();
	}
}
