package com.saferize.sdk;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.util.Date;

import org.junit.Test;

import com.saferize.sdk.internals.WebsocketConnection;


public class TestSaferizeClient {

	private static final String privateKey = "-----BEGIN RSA PRIVATE KEY-----\n" + 
			"MIIEpAIBAAKCAQEA06TiLHrxKaJp5E50cMEjqB4hd7wGGt/NwhuqAKM4F1881AlK\n" + 
			"GmOqECxpNU/qLaIQcye5DXPzWusLqALB7ATLvlLz/c/62Vg8jUKfH17TGIbms0al\n" + 
			"zsdTQb9I6pQLZQbxpuuiurGcfqsRZHWICI5/0wBBcmfnMKHZj6MVdrZTfifQHEJz\n" + 
			"LEk6uQ4Du1iBFkoSQQVW/yOaoMPpyAf5AgbdyX3REf2n2Ijs3uS6Uhpc5AZFhMiz\n" + 
			"ChPd3oR6+2VsyMbRvCK/A9uqrynw8s2AEWgLlW8PpFwUvvDw1fX+d+1Gsp+fuJud\n" + 
			"cPpJKELFKwJyeSdrf6g2eGZFZjpSDJk9NzA2/QIDAQABAoIBAC3Vqom5glri6o3g\n" + 
			"E8WLfl5dUCAvHx9Y0qWz+ggzUOV24aSF8n9ukBj6lTpPeUayr19Q/fmU3+ITvy1+\n" + 
			"k2K60yj/rAmOriO9wTdSc0WG8q6AIJw4s5XpgvVdKLxsnV8ettzQcSh/aIXiJF0e\n" + 
			"OvynZ7VZe9L7/4x/sK8zwWU5LTHHppW2JJQ6JStNTLIsbV5Bc+vFcnbVWI+k5PfA\n" + 
			"NBUUICo/xDy45GY9i3acHQwq/tYXHItrzwgnlm5PG49kekQMOBcXWMZa+CuXZbpF\n" + 
			"jz8lEQ5pXlN52eFvF12VJC3D+kBrH5ZGeYHVzWIuPGyQmgt49kB9QmiI1MOm+WJS\n" + 
			"CWi1d/0CgYEA818bKc9aTsGOmc728MO0aEi7d1SbGJyG46KuEopIa+j4poFJTvLU\n" + 
			"2wvaQEhNpgVeukIypxW3TuP8qeZszA/k3GisxDMVWrYpD1izWanM6EGhbdMPevUh\n" + 
			"1PTlQce671XSbD49fTDcoLFu09XE1jqQU/EU6myvG320irWVE3fNIPMCgYEA3qBR\n" + 
			"LnHu8184a4CJCYBUcFHDh4gxsDpJh9tXjvcCeeowdJk70kjIL7Di6NpX6ABLZjPO\n" + 
			"sWQgRKG5cGTtxpKQLQA2QdnQ2saZAAS2yuPW8fzESyubiuJyugizolHcKO2PbI7f\n" + 
			"7LHn9TGy3q+tksqvFhZ5tE2+rgVi92vgpynzxE8CgYBulZGPLvP3A0Zbp0pX3mVU\n" + 
			"WXAtadlLlpxIRTxZmlIMDoElj2uTHw4PNlSGjxQRUzFW6wt/FoQDqd6+CMD4/GPe\n" + 
			"rwWJ5ThXzpvbqE9ed6RvCJtkftny4f3seRbPDAVqCRIjMyjXgONPdTBJu0HEojnA\n" + 
			"mrQJTTdIA5eMy7Ogc/hWdwKBgQCQrEQ1V4r+EyoaCkyDpSa6Wxgi0mnf6PDx3aOX\n" + 
			"34N3cK4Oh6ntbKjS/TNoOMQZm19kSlSOyM+DakmU9bHjcklJRTL9NixYj+jLr0SO\n" + 
			"suNzHFz/sJYC+keuB4uc92+IFWE4Hdz891wS5jokJqw4kYiYZQCwIDnC4vM+cJds\n" + 
			"aoHkVwKBgQC/ObQzO2UxOzxI0/IbgKRm4a30uGUq/DAcrr7LbC4lQ0JpEnLhMK8r\n" + 
			"26yCi9PhALxuYdTalkzT1nQbWxW8kDZuUYpYK3BDWJj2GxJfdCqNHkrnWZQb9A2x\n" + 
			"Hq6/JLerN9xKQwFq+mqYDe7NPMCa2jin5Wc/EGQWzVzBt+dIEZE63w==\n" + 
			"-----END RSA PRIVATE KEY-----";
	
	
	private static final String SERVER_URL = "http://api.dev.saferize";
	//private static final String SERVER_URL = "http://localhost:8080";
	private static final String WEBSOCKET_URL = "ws://websocket.dev.saferize/usage";
	
	public TestSaferizeClient() {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	@Test
	public void testSignup() throws Exception {
		Configuration config = new  Configuration(new URI(SERVER_URL), new URI(WEBSOCKET_URL), "6b405c76-4b36-4821-9ab5-3a4a127b2af1", privateKey);
		SaferizeClient client = new SaferizeClient(config);
		Approval approval = client.signUp("renato+mine" + new Date().getTime()+ "@saferize.com", "userToken" + new Date().getTime());
		assertThat(approval.getStatus(), is (Approval.Status.PENDING));		
	}
	
	@Test
	public void testCreateSession() throws Exception {
		Configuration config = new  Configuration(new URI(SERVER_URL), new URI(WEBSOCKET_URL),  "6b405c76-4b36-4821-9ab5-3a4a127b2af1", privateKey);
		SaferizeClient client = new SaferizeClient(config);
		String userToken = "userToken" + new Date().getTime();
		Approval approval = client.signUp("renato+mine" + new Date().getTime() + "@saferize.com", userToken);
		assertThat(approval.getStatus(), is (Approval.Status.PENDING));		
		
		SaferizeSession session = client.createSession(userToken);
		assertThat(session.getStatus(), is (SaferizeSession.Status.ACTIVE));		
	}
	
	boolean received = false;
	
	
	@Test	
	public void testWebsocketClient() throws Exception {
		Configuration config = new  Configuration(new URI(SERVER_URL), new URI(WEBSOCKET_URL), "6b405c76-4b36-4821-9ab5-3a4a127b2af1", privateKey);
		SaferizeClient client = new SaferizeClient(config);
		String userToken = "userToken" + new Date().getTime();
		Approval approval = client.signUp("renato+mine" + new Date().getTime() + "@saferize.com", userToken);
		assertThat(approval.getStatus(), is (Approval.Status.PENDING));		
		
		SaferizeSession session = client.createSession(userToken);
		assertThat(session.getStatus(), is (SaferizeSession.Status.ACTIVE));		
		
		client.startWebsocketConnection();
		
	}

}
