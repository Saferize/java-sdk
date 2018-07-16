package com.saferize.sdk;

public class SaferizeSession {
    
	public enum Status {
        ACTIVE, EXPIRED
    };
	private Status status; 
	private long id;
	private Approval approval;
	
	public SaferizeSession() {
	}

	
	public Status getStatus() {
		return status;
	}
	
	public long getId() {
		return id;
	}
	
	public Approval getApproval() {
		return approval;
	}
	
	protected void setApproval(Approval approval) {
		this.approval = approval;
	}
}
