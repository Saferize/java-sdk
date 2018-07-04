package com.saferize.sdk;

public class TimeIsUpException extends SaferizeClientException {


	private static final long serialVersionUID = 1L;

	public TimeIsUpException() {
		super();
	}

	public TimeIsUpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TimeIsUpException(String message, Throwable cause) {
		super(message, cause);
	}

	public TimeIsUpException(String message) {
		super(message);
	}

	public TimeIsUpException(Throwable cause) {
		super(cause);
	}

}
