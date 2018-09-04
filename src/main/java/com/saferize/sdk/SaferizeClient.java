package com.saferize.sdk;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saferize.sdk.Activity.ActivityType;
import com.saferize.sdk.internals.SaferizeConnection;
import com.saferize.sdk.internals.WebsocketClient;
import com.saferize.sdk.internals.WebsocketConnection;

public  class SaferizeClient implements WebsocketConnection {
	
	private SaferizeConnection connection;
	private WebsocketClient websocket;
	private Gson gson;
	private SaferizeSession session;
	
	private SaferizeCallback onPaused;
	private SaferizeCallback onResumed;
	private SaferizeCallback onTimeIsUp;
	private SaferizeCallback onError;
	private SaferizeCallback onDisconnect;
	private SaferizeCallback onConnect;
	
	public  SaferizeClient(Configuration configuration) throws AuthenticationException, WebsocketException {
		this.connection = new SaferizeConnection(configuration);
		this.websocket = new WebsocketClient(configuration);
		this.gson = new Gson();
	}
		
	
	public Approval signUp(String parentEmail, String userToken) throws SaferizeClientException {
		JsonObject root = new JsonObject();
		JsonObject user = new JsonObject();
		JsonObject parent = new JsonObject();
		root.add("user", user);
		root.add("parent", parent);
		
		user.addProperty("token", userToken);
		parent.addProperty("email", parentEmail);
		
		String resp = connection.post("/approval", root.toString());
		Approval approval = gson.fromJson(resp, Approval.class);
		return approval;
	}
	
	public SaferizeSession createSession(String userToken) throws SaferizeClientException {		
		String resp = connection.post("/session/app/" + userToken, "");
		session = gson.fromJson(resp, SaferizeSession.class);
		return session;
	}
	
	public void startWebsocketConnection() throws WebsocketException {
		websocket.subscribe(this);
		if (session == null) {
			throw new WebsocketException("Cannot start websocket connection before initiating a session");
		}
		websocket.connect(session);	
		
	}
	
	public void createProspect(String email) throws SaferizeClientException {
		JsonObject object = new JsonObject();
		object.addProperty("email", email);
		connection.post("/prospect", object.toString());
	}

	public void onPause(SaferizeCallback onPause) {
		this.onPaused = onPause;
	}
	
	
	public void onResume(SaferizeCallback onResume) {
		this.onResumed = onResume;
	}
	
	public void onTimeIsUp(SaferizeCallback onTimeIsUp) {
		this.onTimeIsUp = onTimeIsUp;
	}
	
	public void onError(SaferizeCallback onError) {
		this.onError = onError;
	}
	
	public void onDisconnect(SaferizeCallback onDisconnect) {
		this.onDisconnect = onDisconnect;
	}
	
	public void onConnect(SaferizeCallback onConnect) {
			this.onConnect = onConnect;	
	}

	@Override
	public void onConnect() {
		if (this.onConnect != null) onConnect.trigger(session);
	}

	

	@Override
	public void onMessage(String message) {
		JsonObject rootObject = gson.fromJson(message, JsonObject.class);
		String eventType = rootObject.get("eventType").getAsString();
		switch (eventType) {
			case "ApprovalStateChangedEvent": 
				ApprovalStateChangedEvent approvalEvent = gson.fromJson(message, ApprovalStateChangedEvent.class);
				session.setApproval(approvalEvent.getEntity());
				if (Approval.State.PAUSED == approvalEvent.getEntity().getCurrentState()) {
					if (onPaused != null) onPaused.trigger(session);
				} else {
					if (onResumed != null) onResumed.trigger(session);
				}
				break; 
			case "UsageTimerTimeIsUpEvent": 
				if (onTimeIsUp != null) onTimeIsUp.trigger(session);
				break;
		}

	} 


	@Override
	public void onDisconnect() {
		if (onDisconnect != null) {
			onDisconnect.trigger(session);
		}
		
	}


	@Override
	public void onError() {
		if (onError != null) {
			onError.trigger(session);
		}		
	}
	
	public void createActivity(ActivityType type, String text, String from) throws SaferizeClientException {
		Activity activity = new Activity(type, text, from);
		String json = gson.toJson(activity);
		connection.post(String.format("/session/%d/activity", session.getId()), json);
	}
	

}
