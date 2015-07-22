package loxone.request;

class StatusRequest extends Request{

	@Override
	protected String getFormatString() {
		return "jdev/sps/status";
	}

}
