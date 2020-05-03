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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import pt.teamloan.model.enums.Intent;
import pt.teamloan.model.interfaces.UUIDMappeable;

/**
 * The persistent class for the company database table.
 * 
 */
@Entity
@Table(name = "company")
@Where(clause = "fl_deleted = false")
public class CompanyEntity extends PanacheEntityBase implements Serializable, UUIDMappeable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "COMPANY_ID_GENERATOR", sequenceName = "COMPANY_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COMPANY_ID_GENERATOR")
	@JsonbTransient
	private Integer id;

	@Type(type = "pg-uuid")
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
	@Column(name = "zip_code")
	@JsonbProperty("zip-code")
	private String zipCode;

	@NotBlank
	private String name;

	private String phone;

	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@JsonbProperty("business-area")
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "id_business_area")
	private BusinessAreaEntity businessArea;

	@Transient
	private String password;

	@JsonbTransient
	@Column(name = "fl_deleted")
	private boolean flDeleted = false;

	@Enumerated(EnumType.STRING)
	private Intent intent;

	@JsonbTransient
	@Column(name = "auth_subject_uuid")
	private String authSubjectUuid;

	@JsonbTransient
	@Column(name = "activation_key")
	private String activationKey;

	@JsonbTransient
	@Column(name = "dt_activation_key_expires_at")
	private Timestamp dtActivationKeyExpiresAt;

	@Column(name = "fl_email_verified")
	private boolean emailVerified = false;

	@JsonbTransient
	@Column(name = "reset_pass_key")
	private String resetPasswordKey;

	@JsonbTransient
	@Column(name = "dt_reset_pass_key_expires_at")
	private Timestamp dtResetPasswordKeyExpiresAt;

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

	@JsonbTransient
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

	public String getVat() {
		return this.vat;
	}

	public void setVat(String vat) {
		this.vat = vat;
	}

	public String getZipCode() {
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

	@JsonbTransient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isFlDeleted() {
		return flDeleted;
	}

	public void setFlDeleted(boolean flDeleted) {
		this.flDeleted = flDeleted;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public String getAuthSubjectUuid() {
		return authSubjectUuid;
	}

	public void setAuthSubjectUuid(String authSubjectUuid) {
		this.authSubjectUuid = authSubjectUuid;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public Timestamp getDtActivationKeyExpiresAt() {
		return dtActivationKeyExpiresAt;
	}

	public void setDtActivationKeyExpiresAt(Timestamp dtActivationKeyExpiresAt) {
		this.dtActivationKeyExpiresAt = dtActivationKeyExpiresAt;
	}

	public boolean isEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getResetPasswordKey() {
		return resetPasswordKey;
	}

	public void setResetPasswordKey(String resetPasswordKey) {
		this.resetPasswordKey = resetPasswordKey;
	}

	public Timestamp getDtResetPasswordKeyExpiresAt() {
		return dtResetPasswordKeyExpiresAt;
	}

	public void setDtResetPasswordKeyExpiresAt(Timestamp dtResetPasswordKeyExpiresAt) {
		this.dtResetPasswordKeyExpiresAt = dtResetPasswordKeyExpiresAt;
	}

}