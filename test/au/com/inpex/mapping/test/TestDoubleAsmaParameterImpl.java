package au.com.inpex.mapping.test;

import au.com.inpex.mapping.AsmaParameter;

public class TestDoubleAsmaParameterImpl implements AsmaParameter {
	String asma = "";
	
	@Override
	public String get() {
		return asma;
	}

	@Override
	public void set(String value) {
		asma = value;
	}

}
