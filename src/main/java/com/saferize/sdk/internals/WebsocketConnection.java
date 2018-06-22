package com.saferize.sdk.internals;

public interface WebsocketConnection {
	public void onConnect();
	public void onMessage(String message);
	public void onDisconnect();
}
