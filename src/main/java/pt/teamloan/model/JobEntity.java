package pt.teamloan.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import pt.teamloan.model.interfaces.UUIDMappeable;


/**
 * The persistent class for the job database table.
 * 
 */
@Entity
@Table(name="job")
@Where(clause = "fl_deleted = false")
public class JobEntity extends PanacheEntityBase implements Serializable, UUIDMappeable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="JOB_ID_GENERATOR", sequenceName="JOB_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JOB_ID_GENERATOR")
	@JsonbTransient
	private Integer id;

	@Type(type="pg-uuid")
	private UUID uuid;

	@JsonbTransient
	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;

	@JsonbTransient
	@UpdateTimestamp
	@Column(name="updated_at")
	private Timestamp updatedAt;

	private String name;
	
	//bi-directional many-to-one association to PostingJob
	@OneToMany(mappedBy="job")
	@JsonbTransient
	private List<PostingJobEntity> postingJobs;

	@JsonbTransient
	@Column(name = "fl_deleted")
	private boolean flDeleted = false;

	public JobEntity() {
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

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	@JsonbTransient
	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public List<PostingJobEntity> getPostingJobs() {
		return this.postingJobs;
	}

	public void setPostingJobs(List<PostingJobEntity> postingJobs) {
		this.postingJobs = postingJobs;
	}

	public PostingJobEntity addPostingJob(PostingJobEntity postingJob) {
		getPostingJobs().add(postingJob);
		postingJob.setJob(this);

		return postingJob;
	}

	public PostingJobEntity removePostingJob(PostingJobEntity postingJob) {
		getPostingJobs().remove(postingJob);
		postingJob.setJob(null);

		return postingJob;
	}
	public boolean isFlDeleted() {
		return flDeleted;
	}

	public void setFlDeleted(boolean flDeleted) {
		this.flDeleted = flDeleted;
	}
}