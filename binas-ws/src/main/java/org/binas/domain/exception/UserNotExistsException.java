package org.binas.domain.exception;

/** Exception used to signal that a user does not exist. */
public class UserNotExistsException extends Exception {
	private static final long serialVersionUID = 1L;

	public UserNotExistsException() {
	}

	public UserNotExistsException(String message) {
		super(message);
	}
}
