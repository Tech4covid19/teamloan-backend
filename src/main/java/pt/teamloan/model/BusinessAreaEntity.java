package pt.teamloan.model;

import java.io.Serializable;
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
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


/**
 * The persistent class for the business_area database table.
 * 
 */
@Entity
@Table(name="business_area")
@Where(clause = "fl_deleted = false")
public class BusinessAreaEntity extends PanacheEntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="BUSINESS_AREA_ID_GENERATOR", sequenceName="BUSINESS_AREA_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="BUSINESS_AREA_ID_GENERATOR")
	@JsonbTransient
	private Integer id;

	@Type(type="pg-uuid")
	private UUID uuid;

	@NotBlank
	private String name;

	@JsonbTransient
	@Column(name = "fl_deleted")
	private boolean flDeleted = false;

	public BusinessAreaEntity() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean isFlDeleted() {
		return flDeleted;
	}

	public void setFlDeleted(boolean flDeleted) {
		this.flDeleted = flDeleted;
	}
}