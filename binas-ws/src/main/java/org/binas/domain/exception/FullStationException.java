package org.binas.domain.exception;

/** Exception used to signal that no slots are are currently available in a station. */
public class FullStationException extends Exception {
	private static final long serialVersionUID = 1L;

	public FullStationException() {
	}

	public FullStationException(String message) {
		super(message);
	}
}
