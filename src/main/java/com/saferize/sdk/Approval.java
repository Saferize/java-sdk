package com.saferize.sdk;


public class Approval {
	
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
	
	public State getCurrentStateState() {
		return currentState;
	};
	
	

}
