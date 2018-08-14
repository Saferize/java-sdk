package com.saferize.sdk.internals;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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

	
	private String read(InputStream stream) throws IOException {
		StringBuilder response = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}				
		}				

		return response.toString();	
	}
	
	private String connect(String method, String path, String body) throws SaferizeClientException {
		String token = jwt.generateJWT();
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(configuration.getUrl() + path).openConnection();
			try {
				connection.addRequestProperty("Authorization", "Bearer " + token);
				connection.addRequestProperty("Accept", ACCEPT_HEADER);
				connection.setRequestMethod(method);			
				connection.setDoOutput(true);
				boolean hasBody = body != null && !body.isEmpty();
				
				if (hasBody) {
					connection.setDoInput(true);	
				}
				
				connection.connect();
				
				if (hasBody) {
					try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
						writer.writeBytes(body);
						writer.flush();
					}				
				}
				
				return read(connection.getInputStream());				
			
			} catch (IOException e) {							
				if (connection.getResponseCode() >= 400) {					
					handleException(connection.getResponseCode(), read(connection.getErrorStream()));
					return "";
				}
				else throw e;
			}
			
		} catch (IOException e) {
			throw new  ConnectionException(e);
		}		
	}
	
	public String get(String path) throws SaferizeClientException {
		return connect("GET", path, null);
	}
	
	public String post(String path, String body) throws SaferizeClientException {
		return connect("POST", path, body);
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
