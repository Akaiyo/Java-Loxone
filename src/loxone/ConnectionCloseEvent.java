package loxone;

public class ConnectionCloseEvent {
	private int code;
	private String reason;
	private boolean remote;
	
	public ConnectionCloseEvent(int code, String reason, boolean remote) {
		this.code = code;
		this.reason = reason;
		this.remote = remote;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public boolean isRemote() {
		return remote;
	}
	public void setRemote(boolean remote) {
		this.remote = remote;
	}
	
	@Override
	public String toString() {
		return "ConnectionCloseEvent [code=" + code + ", reason=" + reason + ", remote=" + remote + "]";
	}
}
