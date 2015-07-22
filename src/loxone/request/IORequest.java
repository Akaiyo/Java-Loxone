package loxone.request;

class IORequest extends Request{

	@Override
	protected String getFormatString() {
		return "jdev/sps/io/%s/%s";
	}
	
}
