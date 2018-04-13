package org.binas.domain.exception;

/** The Email is checked and does not meet the conditions */
public class InvalidEmailException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidEmailException() {
	}

	public InvalidEmailException(String message) {
		super(message);
	}
}
