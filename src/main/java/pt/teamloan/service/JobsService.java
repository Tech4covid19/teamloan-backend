package pt.teamloan.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.panache.common.Sort;
import pt.teamloan.model.JobEntity;

@ApplicationScoped
public class JobsService {

	public List<JobEntity> listAll() {
		return JobEntity.listAll(Sort.ascending("name"));
	}
}
