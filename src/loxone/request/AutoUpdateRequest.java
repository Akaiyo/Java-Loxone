package loxone.request;

class AutoUpdateRequest extends Request{

	@Override
	protected String getFormatString() {
		return "jdev/sps/enablestatusupdate";
	}

}
