package com.saferize.sdk.internals;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.saferize.sdk.ApprovalNotFoundException;
import com.saferize.sdk.AuthenticationException;
import com.saferize.sdk.Configuration;
import com.saferize.sdk.ConnectionException;
import com.saferize.sdk.SaferizeClientException;
import com.saferize.sdk.TimeIsUpException;

public class SaferizeConnection {

	private Configuration configuration;
	private JWT jwt;
	public static final String ACCEPT_HEADER = "application/vnd.saferize.com+json;version=1";
	private static Gson gson = new Gson();
	
	private Map<String, Function<String, ? extends SaferizeClientException>> errorMapping = new HashMap<>();
	
	
	public SaferizeConnection(Configuration configuration) throws AuthenticationException {
		
		this.configuration = configuration;		
		
		try {
			this.jwt = new JWT(configuration);
		} catch (JWTException e) {
			throw new AuthenticationException(e);
		}
		
		errorMapping.put("com.saferize.core.entities.appusagesession.UsageTimerTimeIsUpException", TimeIsUpException::new);
		errorMapping.put("com.saferize.core.entities.approval.ApprovalNotFoundException", ApprovalNotFoundException::new);
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
	
	public  String post(String path, String body) throws SaferizeClientException {
		String token = jwt.generateJWT();
		try {
			HttpResponse<String> resp = Unirest.post(configuration.getUrl() + path)
					.header("Authorization", "Bearer " + token)
					.header("Accept", ACCEPT_HEADER).body(body).asString();
			if (resp.getStatus() >= 400) {
				handleException(resp.getStatus(), resp.getBody());
				
			}
			return resp.getBody();
		} catch (UnirestException e) {
			throw new  ConnectionException(e);
		}		
	}
	
   private void handleException(int status, String body) throws SaferizeClientException {
	   if (body == null || body.isEmpty()) {
		   throw new ConnectionException(String.format("Invalid Status Received from Server. Status: %d, Text:%s", status, body));   
	   } 
	   try {
		   JsonObject errorObj = gson.fromJson(body, JsonObject.class);
		   if (!errorObj.has("exceptionType")) {
			   throw new ConnectionException(String.format("Invalid Status Received from Server. Status: %d, Text:%s", status, body));
		   }
		   String exceptionType = errorObj.get("exceptionType").getAsString();
		   String message = errorObj.get("message").getAsString();
		   if (errorMapping.containsKey(exceptionType)) {
			   throw errorMapping.get(exceptionType).apply(message);   
		   } else {
			   throw new ConnectionException(String.format("Invalid Status Received from Server. Status: %d, Text:%s", status, body));
		   }
		   		   
	   } catch (JsonSyntaxException e) {
		   throw new ConnectionException(String.format("Invalid Status Received from Server. Status: %d, Text:%s", status, body));
	   }
	   
   }
	
	
}
