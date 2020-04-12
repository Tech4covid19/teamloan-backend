package pt.teamloan.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;


/**
 * The persistent class for the municipality database table.
 * 
 */
@Entity
@Table(name="municipality")
@NamedQuery(name="MunicipalityEntity.findAll", query="SELECT m FROM MunicipalityEntity m")
@Where(clause = "fl_deleted = false")
public class MunicipalityEntity extends io.quarkus.hibernate.orm.panache.PanacheEntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="MUNICIPALITY_ID_GENERATOR", sequenceName="MUNICIPALITY_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MUNICIPALITY_ID_GENERATOR")
	@JsonbTransient
	private Integer id;

	@Type(type = "pg-uuid")
	private UUID uuid;
	
	private String code;

	@JsonbTransient
	@Column(name="created_at")
	private Timestamp createdAt;

	@JsonbTransient
	@Column(name="fl_deleted")
	private boolean flDeleted;

	private String name;

	@JsonbTransient
	@Column(name="updated_at")
	private Timestamp updatedAt;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_district")
	@Fetch(FetchMode.JOIN)
	@JsonbTransient
	private DistrictEntity district;
	
	public MunicipalityEntity() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Timestamp getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public boolean getFlDeleted() {
		return this.flDeleted;
	}

	public void setFlDeleted(boolean flDeleted) {
		this.flDeleted = flDeleted;
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

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public DistrictEntity getDistrict() {
		return this.district;
	}

	public void setDistrict(DistrictEntity district) {
		this.district = district;
	}

}