package com.saferize.sdk;

import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saferize.sdk.internals.SaferizeConnection;
import com.saferize.sdk.internals.WebsocketClient;
import com.saferize.sdk.internals.WebsocketConnection;

public final class SaferizeClient implements WebsocketConnection {
	
	private SaferizeConnection connection;
	private WebsocketClient websocket;
	private Gson gson;
	private SaferizeSession session;
	
	private Consumer<SaferizeSession> onPaused;
	private Consumer<SaferizeSession> onResumed;
	private Consumer<SaferizeSession> onTimeIsUp;
	private Consumer<SaferizeSession> onError;
	private Consumer<SaferizeSession> onDisconnect;
	private Consumer<SaferizeSession> onConnect;
	
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

	public void onPause(Consumer<SaferizeSession> onPause) {
		this.onPaused = onPause;
	}
	
	
	public void onResume(Consumer<SaferizeSession> onResume) {
		this.onResumed = onResume;
	}
	
	public void onTimeIsUp(Consumer<SaferizeSession> onTimeIsUp) {
		this.onTimeIsUp = onTimeIsUp;
	}
	
	public void onError(Consumer<SaferizeSession> onError) {
		this.onError = onError;
	}
	
	public void onDisconnect(Consumer<SaferizeSession> onDisconnect) {
		this.onDisconnect = onDisconnect;
	}
	
	public void onConnect(Consumer<SaferizeSession> onConnect) {
			this.onConnect = onConnect;	
	}

	@Override
	public void onConnect() {
		if (this.onConnect != null) onConnect.accept(session);
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
					if (onPaused != null) onPaused.accept(session);
				} else {
					if (onResumed != null) onResumed.accept(session);
				}
				break;	
			case "UsageTimerTimeIsUpEvent": 
				if (onTimeIsUp != null) onTimeIsUp.accept(session);
				break;
		}

	}


	@Override
	public void onDisconnect() {
		if (onDisconnect != null) {
			onDisconnect.accept(session);
		}
		
	}


	@Override
	public void onError() {
		if (onError != null) {
			onError.accept(session);
		}
		
	}
	

}
