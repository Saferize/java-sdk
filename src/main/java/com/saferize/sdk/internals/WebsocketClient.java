package com.saferize.sdk.internals;



import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.saferize.sdk.Configuration;
import com.saferize.sdk.SaferizeEvent;
import com.saferize.sdk.SaferizeSession;
import com.saferize.sdk.WebsocketException;


public class WebsocketClient extends Endpoint implements MessageHandler.Whole<String> {

	private Configuration configuration;
	
	private WebsocketConnection connection;
	private JWT jwt;	
	
	private Logger logger = LogManager.getLogger(WebsocketClient.class);	
	private Object connectionLock = new Object();
	private SaferizeSession saferizeSession;
	private boolean connected = false;
	
	public WebsocketClient(Configuration configuration) throws WebsocketException {		
		
		try {
			this.configuration = configuration;	
			this.jwt = new JWT(configuration);
		} catch (JWTException e) {
			throw new WebsocketException(e);
		}
		
	}
	
	public void connect(SaferizeSession session) throws WebsocketException {
		this.saferizeSession = session;
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		try {			
			ClientEndpointConfig.Configurator config = new ClientEndpointConfig.Configurator() {
				@Override
				public void beforeRequest(Map<String, List<String>> headers) {					
					headers.put("Authorization", Arrays.asList("Bearer " + jwt.generateJWT()));
				}
			};			
			ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().configurator(config).build();						
			container.connectToServer(this, cec,  new URI(configuration.getWebsocketUrl().toString() + "?id=" + session.getId()));
		} catch (DeploymentException | IOException | URISyntaxException  e) {
			throw new WebsocketException(e);
		}
	}
	



	
	public void subscribe(WebsocketConnection connection) {		
		this.connection = connection;
		
	}
	
	
	private void createWatchDog() {
		new Thread(() -> {
			while (! Thread.interrupted()) {
				try {
					synchronized (connectionLock) {
						if (connected) {
							connectionLock.wait();
						}
						Thread.sleep(5000);
						connect(saferizeSession);
					}
				} catch (InterruptedException | WebsocketException e) {
					logger.error(e);
				}	
			}						
		}).start();
	}
	 


	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.addMessageHandler(this);
		if (this.connection != null) {
			this.connection.onConnect();
		}
		connected = true;
		createWatchDog();
	}

	@Override
	public void onMessage(String message) {
		logger.info("Received: " + message);
		if (this.connection != null) {
			this.connection.onMessage(message);
		}		
	}
	
	@Override
	public void onError(Session session, Throwable thr) {
		logger.error(thr);
		super.onError(session, thr);
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		super.onClose(session, closeReason);
		logger.info("Connection closed: " + closeReason.getReasonPhrase());
		this.connection.onDisconnect();
		synchronized (connectionLock) {
			connected = false;
			connectionLock.notifyAll();	
		}		
	}
	
}
