package org.binas.domain.exception;

/** Exception used to signal that no slots are are currently available in a station. */
public class InvalidEmailException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidEmailException() {
	}

	public InvalidEmailException(String message) {
		super(message);
	}
}
