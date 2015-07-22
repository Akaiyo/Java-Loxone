package loxone.request;

class EnumDevRequest extends Request{

	@Override
	protected String getFormatString() {
		return "jdev/sps/enumdev";
	}

}