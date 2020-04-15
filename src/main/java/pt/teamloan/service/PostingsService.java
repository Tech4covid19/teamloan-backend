package pt.teamloan.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.Valid;

import com.google.common.base.Strings;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import pt.teamloan.exception.GenericException;
import pt.teamloan.model.BusinessAreaEntity;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.model.DistrictEntity;
import pt.teamloan.model.JobEntity;
import pt.teamloan.model.MunicipalityEntity;
import pt.teamloan.model.PostingEntity;
import pt.teamloan.model.PostingJobEntity;
import pt.teamloan.model.enums.Intent;
import pt.teamloan.model.enums.PostingStatus;
import pt.teamloan.utils.UUIDMapper;

@ApplicationScoped
public class PostingsService {

	private static final String LIST_QUERY_WITH_PARAMETERS = "FROM PostingEntity p JOIN FETCH p.district d JOIN FETCH p.municipality m JOIN FETCH p.postingJobs pj JOIN FETCH pj.job pjj JOIN FETCH p.company c JOIN FETCH c.businessArea ca WHERE p.intent = :intent AND (d.id = :districtId OR :districtId IS NULL) AND (m.id = :municipalityId OR :municipalityId IS NULL) AND (pjj.id = :jobId OR :jobId IS NULL) AND (ca.id = :businessAreaId OR :businessAreaId IS NULL)";

	@Inject
	UUIDMapper uuidMapper;

	public List<PostingEntity> findPaged(Page page, Intent intent, String businessAreaUuid, String districtUuid,
			String municipalityUuid, String jobUuid) throws GenericException {

		Parameters parameters = Parameters.with("intent", intent);
		
		// Optional district filter (for now)
		if (!Strings.isNullOrEmpty(municipalityUuid)) {
			Integer districtId = uuidMapper.mapToId(districtUuid, DistrictEntity.class);
			parameters.and("districtId", districtId);
		} else {
			parameters.and("districtId", null);
		}

		// Optional municipality filter
		if (!Strings.isNullOrEmpty(municipalityUuid)) {
			Integer municipalityId = uuidMapper.mapToId(municipalityUuid, MunicipalityEntity.class);
			parameters.and("municipalityId", municipalityId);
		} else {
			parameters.and("municipalityId", null);
		}

		// Optional job filter
		if (!Strings.isNullOrEmpty(jobUuid)) {
			Integer jobId = uuidMapper.mapToId(jobUuid, JobEntity.class);
			parameters.and("jobId", jobId);
		} else {
			parameters.and("jobId", null);
		}

		// Optional business-area filter
		if (!Strings.isNullOrEmpty(businessAreaUuid)) {
			Integer businessAreaId = uuidMapper.mapToId(businessAreaUuid, BusinessAreaEntity.class);
			parameters.and("businessAreaId", businessAreaId);
		} else {
			parameters.and("businessAreaId", null);
		}
		
		return PostingEntity.find(LIST_QUERY_WITH_PARAMETERS, Sort.descending("p.createdAt"), parameters).page(page)
				.list();
	}

	public List<PostingEntity> listAllForCompany(String companyUuid) {
		return PostingEntity.find("company.uuid", Sort.descending("createdAt"), UUID.fromString(companyUuid)).list();
	}

	public PostingEntity findCompanyPosting(String companyUuid, String postingUuid) {
		Parameters parameters = Parameters.with("companyUuid", UUID.fromString(companyUuid)).and("postingUuid",
				UUID.fromString(postingUuid));
		PostingEntity foundPosting = PostingEntity
				.find("company.uuid = :companyUuid AND uuid = :postingUuid", parameters).singleResult();
		return foundPosting;
	}

	@Transactional
	public PostingEntity create(String companyUuid, @Valid PostingEntity postingEntity) throws GenericException {
		mapPostingCompanyId(companyUuid, postingEntity);
		mapDistrictId(postingEntity);
		mapMunicipalityId(postingEntity);

		postingEntity.setUuid(UUID.randomUUID());
		postingEntity.setStatus(PostingStatus.ACTIVE);
		postingEntity.setUpdatedStatusAt(Timestamp.from(Instant.now()));
		for (PostingJobEntity postingJob : postingEntity.getPostingJobs()) {
			postingJob.setUuid(UUID.randomUUID());
			postingJob.setStatus(PostingStatus.ACTIVE);
			postingJob.setUpdatedStatusAt(Timestamp.from(Instant.now()));
			mapJobId(postingJob);
		}
		postingEntity.persist();
		return postingEntity;
	}

	@Transactional
	public PostingEntity update(String companyUuid, String postingUuid, @Valid PostingEntity postingEntity) {
		PostingEntity foundPosting = findCompanyPosting(companyUuid, postingUuid);
		if (foundPosting != null) {
			// TODO: merge properties that were sent on the request

			// TODO: update statusLastUpdate date if status changed

			// foundPosting.persist();
			return foundPosting;
		} else {
			throw new EntityNotFoundException("Unexisting posting for uuid: " + foundPosting.getUuid().toString());
		}
	}

	@Transactional
	public PostingEntity delete(String companyUuid, String postingUuid) {
		PostingEntity foundPosting = findCompanyPosting(companyUuid, postingUuid);
		foundPosting.setFlDeleted(true);
		foundPosting.persist();
		return foundPosting;
	}

	/**
	 * This works only with cascadeType = REFRESH. Because only the reference id
	 * will be updated
	 * 
	 * @param companyUuid   companyUuid
	 * @param postingEntity postingEntity
	 * @throws GenericException GenericException
	 */
	private void mapPostingCompanyId(String companyUuid, PostingEntity postingEntity) throws GenericException {
		CompanyEntity companyEntity = new CompanyEntity();
		Integer companyId = uuidMapper.mapToId(companyUuid, CompanyEntity.class);
		companyEntity.setId(companyId);
		postingEntity.setCompany(companyEntity);
	}

	private void mapDistrictId(PostingEntity postingEntity) throws GenericException {
		if (postingEntity.getDistrict() != null && postingEntity.getDistrict().getUuid() != null) {
			UUID uuid = postingEntity.getDistrict().getUuid();
			postingEntity.setDistrict(null);
			Integer id = uuidMapper.mapToId(uuid, DistrictEntity.class);
			DistrictEntity districtEntity = new DistrictEntity();
			districtEntity.setId(id);
			postingEntity.setDistrict(districtEntity);
		}
	}

	private void mapMunicipalityId(PostingEntity postingEntity) throws GenericException {
		if (postingEntity.getMunicipality() != null && postingEntity.getMunicipality().getUuid() != null) {
			UUID uuid = postingEntity.getMunicipality().getUuid();
			postingEntity.setMunicipality(null);
			Integer id = uuidMapper.mapToId(uuid, MunicipalityEntity.class);
			MunicipalityEntity municipalityEntity = new MunicipalityEntity();
			municipalityEntity.setId(id);
			postingEntity.setMunicipality(municipalityEntity);
		}
	}

	private void mapJobId(PostingJobEntity postingJob) throws GenericException {
		if (postingJob.getJob() != null && postingJob.getJob().getUuid() != null) {
			UUID uuid = postingJob.getJob().getUuid();
			postingJob.setJob(null);
			Integer id = uuidMapper.mapToId(uuid, JobEntity.class);
			JobEntity jobEntity = new JobEntity();
			jobEntity.setId(id);
			postingJob.setJob(jobEntity);
		}
	}

	public static void main(String[] args) {
		System.out.println(UUID.randomUUID());
	}
}
