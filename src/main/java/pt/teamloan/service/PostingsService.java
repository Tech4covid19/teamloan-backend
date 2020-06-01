package pt.teamloan.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.jboss.logmanager.Level;

import com.google.common.base.Strings;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import pt.teamloan.exception.TeamLoanException;
import pt.teamloan.model.BusinessAreaEntity;
import pt.teamloan.model.CompanyEntity;
import pt.teamloan.model.DistrictEntity;
import pt.teamloan.model.JobEntity;
import pt.teamloan.model.MunicipalityEntity;
import pt.teamloan.model.PostingEntity;
import pt.teamloan.model.PostingJobEntity;
import pt.teamloan.model.enums.CloseReason;
import pt.teamloan.model.enums.Intent;
import pt.teamloan.model.enums.PostingStatus;
import pt.teamloan.utils.UUIDMapper;

@ApplicationScoped
public class PostingsService {
	private static final Logger LOGGER = Logger.getLogger(PostingsService.class.getName());

	private static final String LIST_QUERY_WITH_PARAMETERS = "FROM PostingEntity p " + "JOIN FETCH p.district d "
			+ "JOIN FETCH p.municipality m " + "JOIN FETCH p.postingJobs pj " + "JOIN FETCH pj.job pjj "
			+ "JOIN FETCH p.company c " + "JOIN FETCH c.businessArea ca "
			+ "WHERE (p.intent = :intent OR :intent IS NULL) " 
			+ "AND p.status=:postingStatus"
			+ "AND (c.id = :companyId OR :companyId IS NULL) "
			+ "AND (d.id = :districtId OR :districtId IS NULL) "
			+ "AND (m.id = :municipalityId OR :municipalityId IS NULL) "
			+ "AND (ca.id = :businessAreaId OR :businessAreaId IS NULL) "
			+ "AND (:jobId IS NULL OR EXISTS(SELECT 1 FROM PostingJobEntity pj_aux WHERE pj_aux.job.id = :jobId AND pj_aux.posting.id = p.id))";

	@Inject
	UUIDMapper uuidMapper;

	public List<PostingEntity> findPaged(Page page, Intent intent, String companyUuid, String businessAreaUuid,
			String districtUuid, String municipalityUuid, String jobUuid) throws TeamLoanException {

		Parameters parameters = Parameters.with("intent", intent);

		// Optional district filter (for now)
		if (!Strings.isNullOrEmpty(districtUuid)) {
			Integer districtId = uuidMapper.mapToId(districtUuid, DistrictEntity.class);
			parameters.and("districtId", districtId);
		} else {
			parameters.and("districtId", null);
		}

		// Optional company filter
		if (!Strings.isNullOrEmpty(companyUuid)) {
			Integer companyId = uuidMapper.mapToId(companyUuid, CompanyEntity.class);
			parameters.and("companyId", companyId);
		} else {
			parameters.and("companyId", null);
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

		parameters.and("postingStatus", PostingStatus.ACTIVE);

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
	public PostingEntity create(String companyUuid, @Valid PostingEntity postingEntity) throws TeamLoanException {
		mapPostingCompanyId(companyUuid, postingEntity);
		mapDistrictId(postingEntity, postingEntity.getDistrict());
		mapMunicipalityId(postingEntity, postingEntity.getMunicipality());

		setPostingJobProperties(postingEntity);

		postingEntity.setUuid(UUID.randomUUID());
		postingEntity.setStatus(PostingStatus.ACTIVE);
		postingEntity.setUpdatedStatusAt(Timestamp.from(Instant.now()));

		postingEntity.persist();
		return postingEntity;
	}

	@Transactional
	public PostingEntity update(String companyUuid, String postingUuid, @Valid PostingEntity postingEntity)
			throws TeamLoanException {
		PostingEntity foundPosting = findCompanyPosting(companyUuid, postingUuid);
		if (foundPosting != null) {
			foundPosting
					.setEmail(postingEntity.getEmail() != null ? postingEntity.getEmail() : foundPosting.getEmail());
			foundPosting.setIntent(
					postingEntity.getIntent() != null ? postingEntity.getIntent() : foundPosting.getIntent());
			foundPosting
					.setNotes(postingEntity.getNotes() != null ? postingEntity.getNotes() : foundPosting.getNotes());
			foundPosting
					.setPhone(postingEntity.getPhone() != null ? postingEntity.getPhone() : foundPosting.getPhone());
			foundPosting
					.setTitle(postingEntity.getTitle() != null ? postingEntity.getTitle() : foundPosting.getTitle());
			foundPosting.setZipCode(
					postingEntity.getZipCode() != null ? postingEntity.getZipCode() : foundPosting.getZipCode());
			mapDistrictId(foundPosting, postingEntity.getDistrict());
			mapMunicipalityId(foundPosting, postingEntity.getMunicipality());

			if (postingEntity.getPostingJobs() != null && !postingEntity.getPostingJobs().isEmpty()) {
				for (PostingJobEntity currentPostingJob : foundPosting.getPostingJobs()) {
					currentPostingJob.setFlDeleted(true);
				}
				setPostingJobProperties(postingEntity);
				foundPosting.setPostingJobs(postingEntity.getPostingJobs());
			}
			
			foundPosting
				.setCloseReasonDetails(postingEntity.getCloseReasonDetails() != null ? postingEntity.getCloseReasonDetails() : foundPosting.getCloseReasonDetails());

			CloseReason closeReason = postingEntity.getClosePostingReason();	
			if( closeReason != null){
				foundPosting.setCloseReason(closeReason);
				setPostingStatus(foundPosting, mapPostingStatus(closeReason));
			}

			if (postingEntity.getStatus() != null) {
				setPostingStatus(foundPosting, postingEntity.getStatus());
			}
			
			foundPosting.persist();
			return foundPosting;
		} else {
			TeamLoanException tlException = new TeamLoanException(
					"Unexisting posting for company uuid: '{0}' and posting uuid: '{1}'", companyUuid, postingUuid);
			LOGGER.log(Level.ERROR, tlException.getMessage());
			throw tlException;
		}
	}

	@Transactional
	public PostingEntity delete(String companyUuid, String postingUuid) {
		PostingEntity foundPosting = findCompanyPosting(companyUuid, postingUuid);
		if (foundPosting.getPostingJobs() != null && !foundPosting.getPostingJobs().isEmpty()) {
			for (PostingJobEntity posting : foundPosting.getPostingJobs()) {
				posting.setFlDeleted(true);
			}
		}
		foundPosting.setFlDeleted(true);
		foundPosting.persist();
		return foundPosting;
	}

	public PostingEntity findPostingByUuid(String postingUuid) {
		return PostingEntity.find("uuid", UUID.fromString(postingUuid)).singleResult();
	}

	/**
	 * This works only with cascadeType = REFRESH. Because only the reference id
	 * will be updated
	 * 
	 * @param companyUuid   companyUuid
	 * @param postingEntity postingEntity
	 * @throws TeamLoanException GenericException
	 */
	private void mapPostingCompanyId(String companyUuid, PostingEntity postingEntity) throws TeamLoanException {
		CompanyEntity companyEntity = new CompanyEntity();
		Integer companyId = uuidMapper.mapToId(companyUuid, CompanyEntity.class);
		companyEntity.setId(companyId);
		postingEntity.setCompany(companyEntity);
	}

	private void mapDistrictId(PostingEntity postingEntity, DistrictEntity districtEntity) throws TeamLoanException {
		if (districtEntity != null && districtEntity.getUuid() != null) {
			UUID uuid = districtEntity.getUuid();
			postingEntity.setDistrict(null);
			Integer id = uuidMapper.mapToId(uuid, DistrictEntity.class);
			DistrictEntity mappedDistrictEntity = new DistrictEntity();
			mappedDistrictEntity.setId(id);
			postingEntity.setDistrict(mappedDistrictEntity);
		}
	}

	private void mapMunicipalityId(PostingEntity postingEntity, MunicipalityEntity municipalityEntity)
			throws TeamLoanException {
		if (postingEntity.getMunicipality() != null && postingEntity.getMunicipality().getUuid() != null) {
			UUID uuid = postingEntity.getMunicipality().getUuid();
			postingEntity.setMunicipality(null);
			Integer id = uuidMapper.mapToId(uuid, MunicipalityEntity.class);
			MunicipalityEntity mappedMunicipalityEntity = new MunicipalityEntity();
			mappedMunicipalityEntity.setId(id);
			postingEntity.setMunicipality(mappedMunicipalityEntity);
		}
	}

	private void mapJobId(PostingJobEntity postingJob) throws TeamLoanException {
		if (postingJob.getJob() != null && postingJob.getJob().getUuid() != null) {
			UUID uuid = postingJob.getJob().getUuid();
			postingJob.setJob(null);
			Integer id = uuidMapper.mapToId(uuid, JobEntity.class);
			JobEntity jobEntity = new JobEntity();
			jobEntity.setId(id);
			postingJob.setJob(jobEntity);
		}
	}

	private void setPostingStatus(PostingEntity postingEntity, PostingStatus status) throws TeamLoanException{
		postingEntity.setStatus(status);
		postingEntity.setUpdatedStatusAt(Timestamp.from(Instant.now()));
	}

	private PostingStatus mapPostingStatus(CloseReason closeReason){
		if(CloseReason.MATCH.equals(closeReason)){
			return PostingStatus.MATCHED;
		}
		return PostingStatus.CANCELED;
	}

	private void setPostingJobProperties(PostingEntity postingEntity) throws TeamLoanException {
		for (PostingJobEntity postingJob : postingEntity.getPostingJobs()) {
			postingJob.setUuid(UUID.randomUUID());
			postingJob.setStatus(PostingStatus.ACTIVE);
			postingJob.setUpdatedStatusAt(Timestamp.from(Instant.now()));
			mapJobId(postingJob);
		}
	}
}
