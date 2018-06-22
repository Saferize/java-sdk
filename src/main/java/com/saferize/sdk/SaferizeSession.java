package com.saferize.sdk;

public class SaferizeSession {
    
	public enum Status {
        ACTIVE, EXPIRED
    };
	private Status status; 
	private long id;
	
	public SaferizeSession() {
	}

	
	public Status getStatus() {
		return status;
	}
	
	public long getId() {
		return id;
	}
}
