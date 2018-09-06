package com.saferize.sdk;

import java.io.Serializable;
import java.util.List;

public class SaferizeSession implements Serializable {
    
	private static final long serialVersionUID = 1L;

	public enum Status {
        ACTIVE, EXPIRED
    };
	private Status status; 
	private long id;
	private Approval approval;
	private List<Feature> features;
	
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
	
	public List<Feature> getFeatures() {
		return features;
	}


	public boolean isFeatureEnabled(String name) {
		if (features == null) {
			return true;
		}
		
		return features.stream().anyMatch(feature -> name.equals(feature.getName()) && feature.isParentPrivilege());
	}
}
