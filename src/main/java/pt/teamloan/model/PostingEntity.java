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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


/**
 * The persistent class for the posting database table.
 * 
 */
@Entity
@Table(name="posting")
public class PostingEntity extends PanacheEntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="POSTING_ID_GENERATOR", sequenceName="POSTING_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="POSTING_ID_GENERATOR")
	@JsonbTransient
	private Integer id;
	
	@Type(type="pg-uuid")
	private UUID uuid;

	private String city;

	private String district;

	private String email;

	private String intention;

	private String phone;

	private String status;

	private String title;

	@Column(name="zip_code")
	private String zipCode;

	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name="updated_at")
	private Timestamp updatedAt;

	@Column(name="updated_status_at")
	private Timestamp updatedStatusAt;

	//bi-directional many-to-one association to Company
	@ManyToOne
	@JoinColumn(name="id_company")
	private CompanyEntity company;

	//bi-directional many-to-one association to PostingJob
	@OneToMany(mappedBy="posting")
	private List<PostingJobEntity> postingJobs;

	public PostingEntity() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getDistrict() {
		return this.district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIntention() {
		return this.intention;
	}

	public void setIntention(String intention) {
		this.intention = intention;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Timestamp getUpdatedStatusAt() {
		return this.updatedStatusAt;
	}

	public void setUpdatedStatusAt(Timestamp updatedStatusAt) {
		this.updatedStatusAt = updatedStatusAt;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public CompanyEntity getCompany() {
		return this.company;
	}

	public void setCompany(CompanyEntity company) {
		this.company = company;
	}

	public List<PostingJobEntity> getPostingJobs() {
		return this.postingJobs;
	}

	public void setPostingJobs(List<PostingJobEntity> postingJobs) {
		this.postingJobs = postingJobs;
	}

	public PostingJobEntity addPostingJob(PostingJobEntity postingJob) {
		getPostingJobs().add(postingJob);
		postingJob.setPosting(this);

		return postingJob;
	}

	public PostingJobEntity removePostingJob(PostingJobEntity postingJob) {
		getPostingJobs().remove(postingJob);
		postingJob.setPosting(null);

		return postingJob;
	}

}