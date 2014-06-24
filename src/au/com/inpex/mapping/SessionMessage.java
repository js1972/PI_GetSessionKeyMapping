package au.com.inpex.mapping;

import java.io.InputStream;
import java.io.OutputStream;

import au.com.inpex.mapping.exceptions.BuildMessagePayloadException;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupException;
import com.sap.aii.mapping.lookup.Payload;


public abstract class SessionMessage {
	protected AbstractTrace logger;
	protected AsmaParameter dynConfig = null;
	protected InputStream messageInputstream;
	protected OutputStream messageOutputStream;
	protected CommunicationChannel commChannel;
	protected String fieldName = "";
	protected String sessionKeyResponseField = "";

	
	SessionMessage(TransformationInput in, TransformationOutput out, CommunicationChannel cc, AsmaParameter dc, AbstractTrace trace) {
		logger = trace;
		dynConfig = dc;
		messageInputstream = in.getInputPayload().getInputStream();
		messageOutputStream = out.getOutputPayload().getOutputStream();
		commChannel = cc;
		
		fieldName = in.getInputParameters().getString("FIELD_NAME");
		
		sessionKeyResponseField = in.getInputParameters().getString("SESSION_KEY_RESPONSE_FIELD");
		if (sessionKeyResponseField == null || sessionKeyResponseField.equals("")) {
			throw new BuildMessagePayloadException("Enter a value for the SESSION_KEY_RESPONSE_FIELD mapping parameter!");
		}
	}
	
	private Payload callSessionKeyWebService(Payload payload) throws LookupException {
		return commChannel.call(payload);
	}
	
	protected void logInfo(String msg) {
		try {
			logger.addInfo(msg);
		}
		catch (Exception e) { }
	}
	
	/**
	 * Override this method in implementation class to set the payload for the 
	 * Get Session Request web service call.
	 * @return Payload
	 */
	protected abstract Payload setRequestPayload();
	
	/**
	 * Override this method to extract the session key value from the web
	 * service response.
	 * @param Payload response
	 * @return String session id value
	 */
	protected abstract String getSessionKeyFromResponse(Payload response);
	
	/**
	 * Override this method to build the output message which is sent to
	 * the adapter.
	 */
	protected abstract void buildMessage(String sessionId);
	
	/**
	 * Template-pattern method
	 * @throws LookupException 
	 */
	public final void process() throws LookupException {
		Payload payload = setRequestPayload();
		Payload response = callSessionKeyWebService(payload);
		String sessionId = getSessionKeyFromResponse(response);
		buildMessage(sessionId);
		
		if (sessionId != null && !sessionId.equals("")) {
			dynConfig.set("sessionId", sessionId);
		}
	}
}
