package com.saferize.sdk;


public class Approval {
	
	public enum Status {
		PENDING,
		NOTIFIED,
		APPROVED,
		REJECTED,
	}
	
	private Status status;
	
	public Approval() {
		// TODO Auto-generated constructor stub
	}
	
	public Status getStatus() {
		return status;
	}
	
	

}
