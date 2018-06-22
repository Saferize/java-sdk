package com.saferize.sdk.internals;



import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.saferize.sdk.Configuration;
import com.saferize.sdk.SaferizeSession;
import com.saferize.sdk.WebsocketException;


public class WebsocketClient extends Endpoint implements MessageHandler.Whole<String> {

	private Configuration configuration;
	
	private WebsocketConnection connection;
	private JWT jwt;
	
	public WebsocketClient(Configuration configuration) throws WebsocketException {
		try {
			this.configuration = configuration;	
			this.jwt = new JWT(configuration);
		} catch (JWTException e) {
			throw new WebsocketException(e);
		}
		
	}
	
	public void connect(SaferizeSession session) throws WebsocketException {
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
	
	 


	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.addMessageHandler(this);
		if (this.connection != null) {
			this.connection.onConnect();
		}
	}

	@Override
	public void onMessage(String message) {
		if (this.connection != null) {
			this.connection.onMessage(message);
		}
		
	}
	

}
