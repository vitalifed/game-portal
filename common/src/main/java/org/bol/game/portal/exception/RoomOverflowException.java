package org.bol.game.portal.exception;

public class RoomOverflowException extends GamePortalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoomOverflowException() {
		super();
	}

	public RoomOverflowException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RoomOverflowException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoomOverflowException(String message) {
		super(message);
	}

	public RoomOverflowException(Throwable cause) {
		super(cause);
	}

}
