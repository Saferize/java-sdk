package com.saferize.sdk;

public class ApprovalStateChangedEvent extends SaferizeEvent  {

	public Approval entity;
	
	public ApprovalStateChangedEvent() {
		// TODO Auto-generated constructor stub
	}

	
	public Approval getEntity() {
		return entity;
	}
}
