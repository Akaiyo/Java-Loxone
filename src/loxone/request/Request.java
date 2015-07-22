package loxone.request;

public abstract class Request {
	
	public static final Request AutoUpdate = new AutoUpdateRequest();
	public static final Request Status = new StatusRequest();
	public static final Request State = new StateRequest();
	public static final Request EnumDev = new EnumDevRequest();
	public static final Request EnumIn = new EnumInRequest();
	public static final Request EnumOut = new EnumOutRequest();
	public static final Request GetLoxApp = new GetLoxAppRequest();
	public static final Request IO = new IORequest();
	
	protected abstract String getFormatString();
	
	public String getRequest(Object... args){
		String formatString = getFormatString();
		String request = String.format(formatString, args);
		return request;
	}
}
