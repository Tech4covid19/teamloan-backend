package pt.teamloan.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


/**
 * The persistent class for the company database table.
 * 
 */
@Entity
@Table(name="company")
public class CompanyEntity extends PanacheEntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="COMPANY_ID_GENERATOR", sequenceName="COMPANY_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="COMPANY_ID_GENERATOR")
	private Integer id;

	@Type(type="pg-uuid")
	private UUID uuid;

	@NotBlank
	@Digits(integer = 9, fraction = 0)
	@Size(min = 9, max = 9)
	private String vat;
	
	@Email
	@NotBlank
	private String email;
	
	@NotBlank
	@Size(min = 8, max = 8)
	@Column(name="zip_code")
	@JsonbProperty("zip-code")
	private String zipCode;

	@NotBlank
	private String name;

	private String phone;

	private String responsible;

	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name="updated_at")
	private Timestamp updatedAt;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_business_area")
	private BusinessAreaEntity businessArea;

//	@OneToMany(mappedBy="company", fetch = FetchType.LAZY)
//	private List<PostingEntity> postings;

	public CompanyEntity() {
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

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getResponsible() {
		return this.responsible;
	}

	public void setResponsible(String responsible) {
		this.responsible = responsible;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getVat() {
		return this.vat;
	}

	public void setVat(String vat) {
		this.vat = vat;
	}

	public String getZip_code() {
		return this.zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public BusinessAreaEntity getBusinessArea() {
		return this.businessArea;
	}

	public void setBusinessArea(BusinessAreaEntity businessArea) {
		this.businessArea = businessArea;
	}

//	public List<PostingEntity> getPostings() {
//		return this.postings;
//	}
//
//	public void setPostings(List<PostingEntity> postings) {
//		this.postings = postings;
//	}

//	public PostingEntity addPosting(PostingEntity posting) {
//		getPostings().add(posting);
//		posting.setCompany(this);
//
//		return posting;
//	}
//
//	public PostingEntity removePosting(PostingEntity posting) {
//		getPostings().remove(posting);
//		posting.setCompany(null);
//
//		return posting;
//	}

}