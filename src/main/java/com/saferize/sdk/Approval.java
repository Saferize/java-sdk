package com.saferize.sdk;

import java.io.Serializable;

public class Approval implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public enum Status {
		PENDING,
		NOTIFIED,
		APPROVED,
		REJECTED,
	}
	

	public enum State {
	    ACTIVE,
	    PAUSED,
	}
	
	private Status status;
	private State currentState;
	
	public Approval() {
		// TODO Auto-generated constructor stub
	}
	
	public Status getStatus() {
		return status;
	}
	
	public State getCurrentState() {
		return currentState;
	};
	
	

}
