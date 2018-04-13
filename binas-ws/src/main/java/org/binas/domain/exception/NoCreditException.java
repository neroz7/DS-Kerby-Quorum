package org.binas.domain.exception;

/** Exception used to signal that no slots are are currently available in a station. */
public class NoCreditException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoCreditException() {
	}

	public NoCreditException(String message) {
		super(message);
	}
}
