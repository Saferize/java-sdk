package com.saferize.sdk;

public class WebsocketException extends Exception {

	private static final long serialVersionUID = 1L;

	public WebsocketException() {
	}

	public WebsocketException(String message) {
		super(message);
	}

	public WebsocketException(Throwable cause) {
		super(cause);
	}

	public WebsocketException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebsocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
