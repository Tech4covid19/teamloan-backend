package pt.teamloan.ws;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

public class GenericResponse {

	private boolean success;
	private List<String> errors;

	public GenericResponse() {
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

}
