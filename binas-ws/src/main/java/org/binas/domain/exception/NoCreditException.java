package org.binas.domain.exception;

/** Exception used to signal that there is no credit with the user. */
public class NoCreditException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoCreditException() {
	}

	public NoCreditException(String message) {
		super(message);
	}
}
