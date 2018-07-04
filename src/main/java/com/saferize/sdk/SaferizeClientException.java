package com.saferize.sdk;

public class SaferizeClientException  extends Exception {

	private static final long serialVersionUID = 1L;

	public SaferizeClientException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SaferizeClientException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SaferizeClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public SaferizeClientException(String message) {
		super(message);
	}

	public SaferizeClientException(Throwable cause) {
		super(cause);
	}
	
}
