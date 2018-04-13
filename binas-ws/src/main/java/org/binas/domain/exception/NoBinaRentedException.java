package org.binas.domain.exception;

/** Exception used to signal that no bina is rented. */
public class NoBinaRentedException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoBinaRentedException() {
	}

	public NoBinaRentedException(String message) {
		super(message);
	}
}
