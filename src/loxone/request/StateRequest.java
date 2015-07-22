package loxone.request;

class StateRequest extends Request{

	@Override
	protected String getFormatString() {
		return "jdev/sps/state";
	}

}