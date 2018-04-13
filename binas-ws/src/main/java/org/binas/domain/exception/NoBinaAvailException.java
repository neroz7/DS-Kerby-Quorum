package org.binas.domain.exception;

/** Exception used to signal that no bina is available in a station. */
public class NoBinaAvailException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoBinaAvailException() {
	}

	public NoBinaAvailException(String message) {
		super(message);
	}
}
