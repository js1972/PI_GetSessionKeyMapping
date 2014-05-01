package au.com.inpex.mapping;

import java.io.InputStream;
import java.io.OutputStream;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.Payload;


public abstract class SessionMessage {
	protected AbstractTrace logger;
	protected InputStream messageInputstream;
	protected OutputStream messageOutputStream;
	protected CommunicationChannel commChannel;

	
	SessionMessage(TransformationInput in, TransformationOutput out, CommunicationChannel cc, AbstractTrace trace) {
		logger = trace;
		messageInputstream = in.getInputPayload().getInputStream();
		messageOutputStream = out.getOutputPayload().getOutputStream();
		commChannel = cc;
	}
	
	private Payload callSessionKeyWebService(Payload payload) {
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
	 */
	public final void process() {
		Payload payload = setRequestPayload();
		Payload response = callSessionKeyWebService(payload);
		String sessionId = getSessionKeyFromResponse(response);
		buildMessage(sessionId);
	}
}
