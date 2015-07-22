package loxone;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class UUIDStorage {
	private final Map<Integer,UUID> uuids;
	
	public UUIDStorage(){
		this.uuids = new HashMap<>();
	}
	
	
	public UUID get(int shortID){
		return uuids.get(shortID);
	}
	
	public void add(int shortID,UUID uuid){
		uuids.put(shortID , uuid);
	}
	
	public boolean ocntains(UUID uuid){
		return uuids.containsValue(uuid);
	}
	
	public boolean contains(int shortID){
		return uuids.containsKey(shortID);
	}
	
	public void drop(){
		uuids.clear();
	}
	
	public void forEach(BiConsumer<? super Integer,? super UUID> consumer){
		uuids.forEach(consumer);
	}
}
