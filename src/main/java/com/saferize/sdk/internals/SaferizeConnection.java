package com.saferize.sdk.internals;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.saferize.sdk.AuthenticationException;
import com.saferize.sdk.Configuration;
import com.saferize.sdk.ConnectionException;

public class SaferizeConnection {

	private Configuration configuration;
	private JWT jwt;
	public static final String ACCEPT_HEADER = "application/vnd.saferize.com+json;version=1";
	
	
	public SaferizeConnection(Configuration configuration) throws AuthenticationException {
		this.configuration = configuration;		
		try {
			this.jwt = new JWT(configuration);
		} catch (JWTException e) {
			throw new AuthenticationException(e);
		}
	}

	public String get(String path) throws ConnectionException {
		String token = jwt.generateJWT();
		try {
			HttpResponse<String> resp = Unirest.post(configuration.getUrl() + path)
					.header("Authorization", "Bearer " + token)
					.header("Accept", ACCEPT_HEADER).asString();
			if (resp.getStatus() >= 400) {
				throw new ConnectionException(String.format("Invalid Status Received from Server. Status: %d, Text:%s", resp.getStatus(), resp.getBody()));
			}			
			return resp.getBody();
		} catch (UnirestException e) {
			throw new  ConnectionException(e);
		}	
	}
	
	public  String post(String path, String body) throws ConnectionException {
		String token = jwt.generateJWT();
		try {
			HttpResponse<String> resp = Unirest.post(configuration.getUrl() + path)
					.header("Authorization", "Bearer " + token)
					.header("Accept", ACCEPT_HEADER).body(body).asString();
			if (resp.getStatus() >= 400) {
				throw new ConnectionException(String.format("Invalid Status Received from Server. Status: %d, Text:%s", resp.getStatus(), resp.getBody()));
			}
			return resp.getBody();
		} catch (UnirestException e) {
			throw new  ConnectionException(e);
		}		
	}
	

}
