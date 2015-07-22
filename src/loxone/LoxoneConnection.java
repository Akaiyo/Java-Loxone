package loxone;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import loxone.request.*;

public class LoxoneConnection {
	
	private Logger logger = LoggerFactory.getLogger(LoxoneConnection.class);
	
	private final ObjectMapper jsonMapper = new ObjectMapper();
	
	private final URI uri;
	private final String user;
	private final String password;
	
	private WebSocketClient client;	
	
	private UUIDStorage uuidStorage;
	
	private Map<UUID,List<StatusUpdateListener>> listenersMap;
	private ErrorHandler errorHandler;
	private ConnectionCloseHandler closeHandler;
	
	public LoxoneConnection(URI uri,String user,String password){
		this.uri = uri;
		this.user = user;
		this.password = password;
		this.uuidStorage = new UUIDStorage();
		this.listenersMap = new HashMap<>();
	}

	
	public void connect(){
		
		Map<String,String> httpHeaders = constructHTTPHeaders();
		
		this.client = new WebSocketClient(this.uri, new Draft_17(), httpHeaders) {
	
			@Override
			public void onOpen(ServerHandshake sh) {
				logger.debug("LoxoneConnection established");
				
				//shortID -> UUID 
				sendRequest(Request.GetLoxApp);
				
				//Autoupdates over websocket
				sendRequest(Request.AutoUpdate);
			}
			
			@Override
			public void onMessage(String s) {
				logger.debug("Incoming message from Loxone server");
				//logger.debug(s);
				handleMessage(s);
			}
			
			@Override
			public void onError(Exception e) {
				logger.error("Error in connection to Loxone server",e);
				if(errorHandler != null)
					errorHandler.onError(e);
			}
			
			@Override
			public void onClose(int code, String reason, boolean remote) {				
				if(closeHandler != null)
					closeHandler.onConnectionClose(
							new ConnectionCloseEvent(code, reason, remote));
			}
		};
		
		try {
			client.connectBlocking();
		} catch (InterruptedException e) {
			logger.error("Blocking call of connectBlocking() was interrupted",e);
		}
	}
	
	public void disconnect(){
		this.client.close();
	}
	
	public void sendRequest(Request request){
		client.send(request.getRequest());
	}
	
	public void sendRequest(Request reguest,Object... args){
		client.send(reguest.getRequest(args));
	}
	
	public void addStatusUpdateListener(StatusUpdateListener listener){
		addStatusUpdateListener(listener,null);
	}
	
	public void addStatusUpdateListener(StatusUpdateListener listener,UUID uuid){
		List<StatusUpdateListener> listeners = listenersMap.get(uuid);
		
		if(listeners == null){
			listeners = new LinkedList<>();
			listenersMap.put(uuid, listeners);
		}
		
		listeners.add(listener);
	}
	
	public void removeStatusUpdateListener(StatusUpdateListener listener){
		removeStatusUpdateListener(listener,null);
	}
	
	public void removeStatusUpdateListener(StatusUpdateListener listener,UUID uuid){
		Iterator<List<StatusUpdateListener>> it = listenersMap.values().iterator();
		
		while(it.hasNext()){
			List<StatusUpdateListener> listeners = it.next();
			listeners.remove(listener);
			if(listeners.isEmpty())
				it.remove();
		}
	}
	
	public void setErrorHandler(ErrorHandler errorHandler){
		this.errorHandler = errorHandler;
	}
	
	public void removeErrorHandler(){
		this.errorHandler = null;
	}
	
	public void setConnectionCloseHandler(ConnectionCloseHandler closeHandler){
		this.closeHandler = closeHandler;
	}
	
	public void removeConnectionCloseHandler(){
		this.closeHandler = null;
	}
	
	private void callStatusUpdateListeners(StatusUpdate update){
		callListOfListeners(listenersMap.get(null),update);
		callListOfListeners(listenersMap.get(update.getUUID()),update);
	}
	
	private void callListOfListeners(List<StatusUpdateListener> listeners,StatusUpdate update){
		if(listeners != null){
			listeners.forEach(listener -> listener.onStatusUpdate(update));
		}
	}
		
	private Map<String,String> constructHTTPHeaders(){
		Map<String,String> httpHeaders = new HashMap<>();
		
		//BasicAuth
		String authString = user + ":" + password;
		String authStringInBase64 = Base64.encode(authString.getBytes());
		httpHeaders.put("Authorization", "Basic " + authStringInBase64);
		
		return httpHeaders;
	}
		
	private void handleMessage(String message){
		if(message == null)
			return;

		
		if(message.startsWith("{\"s\"")){
			handleSplitMessages(message);
		}else{
			handleSingleMessage(message);
		}
		
	}
		
	private void handleSplitMessages(String message){
		String[] lines = message.split("\\r?\\n");
		
		for(String line : lines){
			try{
				handleLine(line);
			}catch(Exception e){
				logger.error("Was not able to parse Loxone message line",e);
			}
		}
	}
	
	private void handleSingleMessage(String message){
		try {
			handleLine(message);
		} catch (IOException e) {
			logger.error("Was not able to parse Loxone message",e);
		}
	}
	
	private void handleLine(String line) throws JsonProcessingException, IOException{
		if(line == null || line.isEmpty())
			return;
		
		JsonNode lineNode  = jsonMapper.readTree(line);
		
		if(lineNode.has("s")){	//Statusupdate
			handleStatusUpdate(lineNode.get("s"));
		}else if(lineNode.has("LL")){ //RequestResponse
			handleRequestResponse(lineNode.get("LL"));
		}else if(lineNode.has("UUIDs")){
			handleUUIDs(lineNode.get("UUIDs"));
		}else{
			logger.debug(line);
		}
	}
	
	
	
	
	private void handleStatusUpdate(JsonNode statusNode){
		int shortID = statusNode.get("n").asInt();
		double value = statusNode.get("v").asDouble();
		
		if(uuidStorage.contains(shortID)){
			UUID uuid = uuidStorage.get(shortID);
			
			StatusUpdate update = new StatusUpdate(uuid, shortID, value);
			
		//	logger.debug(update.toString());
						
			callStatusUpdateListeners(update);
			
			
			
		}else{
			logger.warn("Was not able to resolve uuid from statusupdate. Ignoring it...");
		}
		
		
		
		
	}
	
	private void handleRequestResponse(JsonNode responseNode){
		int responseCode =  responseNode.get("Code").asInt();
		String request = responseNode.get("control").asText();
		String value = responseNode.get("value").asText();
				
		logger.debug("RequestResponse:");
		logger.debug("Code: " + responseCode);
		logger.debug("Request: " + request);
		logger.debug("Value: " + value);
	}
	
	private void handleUUIDs(JsonNode uuidsNode){
		//logger.debug("LoxLive:");
		//logger.debug(uuidsNode.toString());
		
		uuidStorage.drop();
		
		JsonNode uuidNode = uuidsNode.get("UUID");
		uuidNode.elements().forEachRemaining(this :: addUUID);
		
		uuidStorage.forEach((n,uuid) -> logger.debug(n + " -> " + uuid));
	}
	
	
	private void addUUID(JsonNode node){
		int n = node.get("n").asInt();
		String loxoneUUID = node.get("UUID").asText();
		String formatedLoxoneUUID = loxoneUUID.substring(0, 23) + "-" + loxoneUUID.substring(24);	
		UUID uuid = UUID.fromString(formatedLoxoneUUID);
		uuidStorage.add(n, uuid);
	}
	
	
	
	
}
