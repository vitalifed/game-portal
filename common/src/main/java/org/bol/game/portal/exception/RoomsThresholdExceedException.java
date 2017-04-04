package org.bol.game.portal.exception;

/**
 * Thrown when an amount of rooms in repository exceeds defined value.
 * 
 * @author VF85400
 *
 */
public class RoomsThresholdExceedException extends GamePortalException {

	private static final long serialVersionUID = 1L;

	public RoomsThresholdExceedException() {
		super();
	}

	public RoomsThresholdExceedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RoomsThresholdExceedException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoomsThresholdExceedException(String message) {
		super(message);
	}

	public RoomsThresholdExceedException(Throwable cause) {
		super(cause);
	}

}
