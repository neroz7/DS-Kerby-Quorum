package org.binas.domain.exception;

/** Exception used to signal that the given email already exists. */
public class EmailExistsException extends Exception {
	private static final long serialVersionUID = 1L;

	public EmailExistsException() {
	}

	public EmailExistsException(String message) {
		super(message);
	}
}
