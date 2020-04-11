package pt.teamloan.model;

import java.io.Serializable;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.*;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


/**
 * The persistent class for the district database table.
 * 
 */
@Entity
@Table(name="district")
@NamedQuery(name="DistrictEntity.findAll", query="SELECT d FROM DistrictEntity d")
@Where(clause = "fl_deleted = false")
public class DistrictEntity extends io.quarkus.hibernate.orm.panache.PanacheEntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="DISTRICT_ID_GENERATOR", sequenceName="DISTRICT_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DISTRICT_ID_GENERATOR")
	@JsonbTransient
	private Integer id;
	
	@Type(type = "pg-uuid")
	private UUID uuid;
	
	private String code;

	@Column(name="created_at")
	private Timestamp createdAt;

	@Column(name="fl_deleted")
	private boolean flDeleted;

	private String name;

	@Column(name="updated_at")
	private Timestamp updatedAt;

	public DistrictEntity() {
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
}