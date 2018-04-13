package org.binas.domain.exception;

/** Exception used to signal that no Binas are currently available in a station. */
public class BadInitException extends Exception {
	private static final long serialVersionUID = 1L;

	public BadInitException() {
	}

	public BadInitException(String message) {
		super(message);
	}
}
