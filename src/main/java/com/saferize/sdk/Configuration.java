package com.saferize.sdk;

import java.net.URI;

public final class Configuration {

	private URI url;
	private String accessKey;
	private String privateKey;
	private URI websocketUrl;
	
	public Configuration(URI url, URI websocketUrl, String accessKey, String privateKey) {
		this.url = url;
		this.accessKey = accessKey;
		this.privateKey = privateKey;
		this.websocketUrl = websocketUrl;
	}
	
	public URI getUrl() {
		return url;
	}
	
	public String getAccessKey() {
		return accessKey;
	}
	
	public String getPrivateKey() {
		return privateKey;
	}
	
	public URI getWebsocketUrl() {
		return websocketUrl;
	}
	
	

}
