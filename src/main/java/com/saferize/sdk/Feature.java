package com.saferize.sdk;

public class Feature {

	private long id;
	private String name;
	private boolean implemented;
	private boolean parentPrivilege;
	
	public Feature() {
	}

	protected long getId() {
		return id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected boolean isImplemented() {
		return implemented;
	}

	protected void setImplemented(boolean implemented) {
		this.implemented = implemented;
	}

	protected boolean isParentPrivilege() {
		return parentPrivilege;
	}

	protected void setParentPrivilege(boolean parentPrivilege) {
		this.parentPrivilege = parentPrivilege;
	}

	
	
}
