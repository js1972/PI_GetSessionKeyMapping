package au.com.inpex.mapping.exceptions;

public class SessionKeyResponseException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	
	public SessionKeyResponseException() {
	}

	public SessionKeyResponseException(String message) {
		super(message);
	}

	public SessionKeyResponseException(Throwable cause) {
		super(cause);
	}

	public SessionKeyResponseException(String message, Throwable cause) {
		super(message, cause);
	}

}
