package com.saferize.sdk.internals;



import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.saferize.sdk.Configuration;
import com.saferize.sdk.SaferizeSession;
import com.saferize.sdk.WebsocketException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class WebsocketClient extends WebSocketListener  {

	private Configuration configuration;
	
	private WebsocketConnection connection;
	private JWT jwt;	
	
	//private Logger logger = LogManager.getLogger(WebsocketClient.class);	
	private Object connectionLock = new Object();
	private SaferizeSession saferizeSession;
	private Boolean connected = null;
	private Thread watchDogThread = null;
	
	
	public WebsocketClient(Configuration configuration) throws WebsocketException {		
		try {
			this.configuration = configuration;	
			this.jwt = new JWT(configuration);
		} catch (JWTException e) {
			throw new WebsocketException(e);
		}
	}
	
	/**
	 * @param session
	 * @throws WebsocketException
	 */
	public void connect(SaferizeSession session) throws WebsocketException {
		this.saferizeSession = session;
		OkHttpClient client = new OkHttpClient();
		Request request =  new Request.Builder()
				.url(configuration.getWebsocketUrl().toString() + "?id=" + session.getId())
				.addHeader("Authorization", "Bearer " + jwt.generateJWT())
				.build();
		WebSocket webSocket = client.newWebSocket(request, this);
	}
	



	
	public void subscribe(WebsocketConnection connection) {		
		this.connection = connection;
		
	}
	
	
	private void createWatchDog() {
		if (watchDogThread != null) {
			return;
		}
		
		watchDogThread = new Thread(() -> {
			while (! Thread.interrupted()) {
				try {
					synchronized (connectionLock) {
						if (connected) {
							connectionLock.wait();
						}
						Thread.sleep(5000);
						if (!connected) {
							connect(saferizeSession);
						}
					}
				} catch (InterruptedException | WebsocketException e) {
					//logger.error(e);
				}	
			}						
		});
		watchDogThread.start();
	}
	 


	@Override
	public void onOpen(WebSocket webSocket, Response response) {
		if (this.connection != null) {
			this.connection.onConnect();
		}
		connected = true;
		createWatchDog();
	}


	@Override
	public void onMessage(WebSocket webSocket, String text) {
		if (this.connection != null) {
			this.connection.onMessage(text);
		}		
	}
	
	@Override
	public void onFailure(WebSocket webSocket, Throwable t, Response response) {
		super.onFailure(webSocket, t, response);
		if (this.connection != null) {
			this.connection.onError();
			onClosed(webSocket, -1, "failed");
			
		}
	}

	@Override
	public void onClosed(WebSocket webSocket, int code, String reason) {
		//logger.debug("Connection closed: " + closeReason.getReasonPhrase());
		if (this.connection != null) {
			this.connection.onDisconnect();
		}
		synchronized (connectionLock) {
			connected = false;
			connectionLock.notifyAll();	
		}		
	}
	
}
