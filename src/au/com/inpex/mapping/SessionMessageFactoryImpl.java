package au.com.inpex.mapping;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;


/**
 * Implementation of a Simple Factory for SessionMessage objects.
 * 
 * Note: The default implementation is the identity transform which only
 * logs the session key and does nothing to the payload.
 */
public class SessionMessageFactoryImpl implements SessionMessageFactory {
	private static SessionMessageFactoryImpl instance;
	
	public static SessionMessageFactoryImpl getInstance() {
		if (instance == null) {
			instance = new SessionMessageFactoryImpl();
		}
		
		return new SessionMessageFactoryImpl();
	}

	@Override
	public SessionMessage createSessionMessageHandler(
		String type,
		Boolean logoff,
		TransformationInput in,
		TransformationOutput out,
		CommunicationChannel cc,
		AsmaParameter dynConfig,
		AbstractTrace trace) {
		
		//
		// To logoff we set the mapping parameter binding  for LOGGOFF = TRUE
		// and DO NOT bind the mapping type. This is how we can differentiate
		// between the request and the response.
		//
		
		if (logoff) {
			return new LogoffHandlerImpl(in, out, cc, dynConfig, trace);
		} else if (type.equals("SET_FIELD")) {
			return new SessionMessagePayloadImpl(in, out, cc, dynConfig, trace);
		} else if (type.equals("ADD_FIELD")) {
			return new SessionMessageAddToPayloadImpl(in, out, cc, dynConfig, trace);
		} else if (type.equals("SOAP_HEADER")) {
			return new SessionMessageSoapHeaderImpl(in, out, cc, dynConfig, trace);
		} else {
			return new SessionMessageIdentityImpl(in, out, cc, dynConfig, trace);
		}
	}

}