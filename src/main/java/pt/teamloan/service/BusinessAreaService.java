package pt.teamloan.service;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;

import io.quarkus.panache.common.Sort;
import pt.teamloan.model.BusinessAreaEntity;

@ApplicationScoped
public class BusinessAreaService {

	public List<BusinessAreaEntity> listAll() {
		return BusinessAreaEntity.listAll(Sort.ascending("name"));
	}
	
	@Transactional
	public BusinessAreaEntity add(@Valid BusinessAreaEntity businessArea) {
		businessArea.setUuid(UUID.randomUUID());
		businessArea.persist();
		return businessArea;
	}
	
	@Transactional
	public BusinessAreaEntity update(@Valid BusinessAreaEntity businessArea) {
		BusinessAreaEntity foundArea = BusinessAreaEntity.find("uuid", businessArea.getUuid()).firstResult();
		if(foundArea != null) {
			foundArea.setName(businessArea.getName());
			foundArea.persist();
			return foundArea;
		} else {
			throw new EntityNotFoundException("Unexisting business-area for uuid: " + businessArea.getUuid().toString());
		}
	}
	
	@Transactional
	public BusinessAreaEntity delete(String uuid) {
		BusinessAreaEntity foundArea = BusinessAreaEntity.find("uuid", uuid).firstResult();
		foundArea.setFlDeleted(true);
		return foundArea;
	}
}
