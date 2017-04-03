package org.bol.game.portal.exception;

public class GamePortalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GamePortalException() {
	}

	public GamePortalException(String message) {
		super(message);
	}

	public GamePortalException(Throwable cause) {
		super(cause);
	}

	public GamePortalException(String message, Throwable cause) {
		super(message, cause);
	}

	public GamePortalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
