package com.saferize.sdk;

public class Activity {

	private String activityText;
	private String activityFrom;
	
	private ActivityType activityType;
	
	public enum ActivityType {
		CHAT, COMMENT, ACHIEVEMENT, OTHER
	}
	
	public Activity(ActivityType type, String text, String from) {
		activityFrom = from;
		activityText = text;
		activityType = type;
	}
	
	public String getActivityFrom() {
		return activityFrom;
	}
	
	public String getActivityText() {
		return activityText;
	}
	
	public ActivityType getActivityType() {
		return activityType;
	}
	

}
