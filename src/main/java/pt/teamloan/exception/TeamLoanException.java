package pt.teamloan.exception;

import java.text.MessageFormat;

public class TeamLoanException extends Exception {
	
	public TeamLoanException(String message) {
		super(message);
	}
	
	public TeamLoanException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public TeamLoanException(String message, Throwable cause, Object... parameters) {
		super(MessageFormat.format(message, parameters), cause);
	}
}
