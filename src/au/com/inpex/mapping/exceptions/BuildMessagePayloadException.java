package au.com.inpex.mapping.exceptions;

public class BuildMessagePayloadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	
	public BuildMessagePayloadException() {
	}

	public BuildMessagePayloadException(String message) {
		super(message);
	}

	public BuildMessagePayloadException(Throwable cause) {
		super(cause);
	}

	public BuildMessagePayloadException(String message, Throwable cause) {
		super(message, cause);
	}

}
