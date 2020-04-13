package pt.teamloan.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import pt.teamloan.model.enums.PostingStatus;
import pt.teamloan.model.interfaces.UUIDMappeable;

/**
 * The persistent class for the posting_job database table.
 * 
 */
@Entity
@Table(name = "posting_job")
@Where(clause = "fl_deleted = false")
public class PostingJobEntity extends PanacheEntityBase implements Serializable, UUIDMappeable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "POSTING_JOB_ID_GENERATOR", sequenceName = "POSTING_JOB_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POSTING_JOB_ID_GENERATOR")
	@JsonbTransient
	private Integer id;

	@Type(type = "pg-uuid")
	private UUID uuid;

	@Column(name = "other_job")
	@JsonbProperty("other-job")
	@Size(min = 2, max = 100)
	private String otherJob;

	@Column(name = "number_of_people")
	@JsonbProperty("number-of-people")
	@NotNull
	@Min(1)
	@Max(999)
	private Integer numberOfPeople;

	@Enumerated(EnumType.STRING)
	private PostingStatus status = PostingStatus.ACTIVE;

	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@Column(name = "updated_status_at")
	private Timestamp updatedStatusAt;

	// bi-directional many-to-one association to Job
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "id_job")
	private JobEntity job;

	// bi-directional many-to-one association to Posting
	@ManyToOne
	@JoinColumn(name = "id_posting")
	@JsonbTransient
	private PostingEntity posting;

	@JsonbTransient
	@Column(name = "fl_deleted")
	private boolean flDeleted = false;

	public PostingJobEntity() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	@JsonbTransient
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getOtherJob() {
		return this.otherJob;
	}

	public void setOtherJob(String otherJob) {
		this.otherJob = otherJob;
	}

	public PostingStatus getStatus() {
		return status;
	}

	public void setStatus(PostingStatus status) {
		this.status = status;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	@JsonbTransient
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Timestamp getUpdatedStatusAt() {
		return this.updatedStatusAt;
	}

	@JsonbTransient
	public void setUpdatedStatusAt(Timestamp updatedStatusAt) {
		this.updatedStatusAt = updatedStatusAt;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public JobEntity getJob() {
		return this.job;
	}

	public void setJob(JobEntity job) {
		this.job = job;
	}

	public PostingEntity getPosting() {
		return this.posting;
	}

	public void setPosting(PostingEntity posting) {
		this.posting = posting;
	}

	public boolean isFlDeleted() {
		return flDeleted;
	}

	public void setFlDeleted(boolean flDeleted) {
		this.flDeleted = flDeleted;
	}

	public Integer getNumberOfPeople() {
		return numberOfPeople;
	}

	public void setNumberOfPeople(Integer numberOfPeople) {
		this.numberOfPeople = numberOfPeople;
	}

}