package com.saferize.sdk.internals;

class JWTException extends Exception {


	private static final long serialVersionUID = 1L;

	public JWTException() {
		super();
	}

	public JWTException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public JWTException(String message, Throwable cause) {
		super(message, cause);
	}

	public JWTException(String message) {
		super(message);
	}

	public JWTException(Throwable cause) {
		super(cause);
	}
	
}
