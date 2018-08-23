package com.saferize.sdk;

public class ApprovalNotFoundException extends SaferizeClientException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApprovalNotFoundException() {
	}

	public ApprovalNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ApprovalNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApprovalNotFoundException(String message) {
		super(message);
	}

	public ApprovalNotFoundException(Throwable cause) {
		super(cause);
	}

}
