package pt.teamloan.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class GenericResponse {

	private UUID uuid;
	private boolean success;
	private List<String> errors;

	public GenericResponse() {
		this.success = true;
		this.errors = new ArrayList();
	}
	
	public GenericResponse(UUID uuid) {
		this.uuid = uuid;
		this.success = true;
		this.errors = new ArrayList();
	}

	public GenericResponse(Throwable t) {
		this.success = false;
		this.errors = new ArrayList();
		this.errors.add(t.getLocalizedMessage());
	}

	public GenericResponse(ConstraintViolationException e) {
		this.success = false;
		this.errors = new ArrayList();
		if (e.getConstraintViolations() != null && !e.getConstraintViolations().isEmpty()) {
			for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
				this.errors.add(constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage());
			}
		} else {
			this.errors.add(e.getLocalizedMessage());
		}
	}

	public GenericResponse(boolean success, String error) {
		super();
		this.success = success;
		this.errors = new ArrayList();
		this.errors.add(error);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public boolean addError(String e) {
		return errors.add(e);
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

}
