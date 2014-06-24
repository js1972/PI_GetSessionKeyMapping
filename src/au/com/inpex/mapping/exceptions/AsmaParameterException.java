package au.com.inpex.mapping.exceptions;

public class AsmaParameterException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	

	public AsmaParameterException() {
	}

	public AsmaParameterException(String message) {
		super(message);
	}

	public AsmaParameterException(Throwable cause) {
		super(cause);
	}

	public AsmaParameterException(String message, Throwable cause) {
		super(message, cause);
	}

}
