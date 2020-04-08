package pt.teamloan.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


/**
 * The persistent class for the business_area database table.
 * 
 */
@Entity
@Table(name="business_area")
public class BusinessAreaEntity extends PanacheEntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="BUSINESS_AREA_ID_GENERATOR", sequenceName="BUSINESS_AREA_ID_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="BUSINESS_AREA_ID_GENERATOR")
	private Integer id;

	@Type(type="pg-uuid")
	private UUID uuid;

	private String name;

	//bi-directional many-to-one association to Company
	@OneToMany(mappedBy="businessArea")
	private List<CompanyEntity> companies;

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

	public List<CompanyEntity> getCompanies() {
		return this.companies;
	}

	public void setCompanies(List<CompanyEntity> companies) {
		this.companies = companies;
	}

	public CompanyEntity addCompany(CompanyEntity company) {
		getCompanies().add(company);
		company.setBusinessArea(this);

		return company;
	}

	public CompanyEntity removeCompany(CompanyEntity company) {
		getCompanies().remove(company);
		company.setBusinessArea(null);

		return company;
	}

}