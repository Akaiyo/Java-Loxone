package loxone.request;

class GetLoxAppRequest extends Request{

	@Override
	protected String getFormatString() {
		return "jdev/sps/getloxapp";
	}
	
}
