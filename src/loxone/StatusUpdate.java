package loxone;

import java.util.UUID;

public class StatusUpdate {
	private UUID uuid;
	private int shortID;
	private double value;
	
	
	public StatusUpdate(UUID uuid,int shortID,double value){
		this.uuid = uuid;
		this.shortID = shortID;
		this.value = value;
	}
	
	public UUID getUUID() {
		return uuid;
	}


	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}


	public int getShortID() {
		return shortID;
	}


	public void setShortID(int shortID) {
		this.shortID = shortID;
	}


	public double getValue() {
		return value;
	}


	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "StatusUpdate [uuid=" + uuid + ", shortID=" + shortID + ", value=" + value + "]";
	}
}
