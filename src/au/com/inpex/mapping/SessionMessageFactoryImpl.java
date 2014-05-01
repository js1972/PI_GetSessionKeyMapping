package au.com.inpex.mapping;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;


/**
 * Implementation of Abstract Factory pattern for SessionMessage objects.
 *
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
		TransformationInput in,
		TransformationOutput out,
		CommunicationChannel cc,
		AsmaParameter dynConfig,
		AbstractTrace trace) {
		
		if (type.equals("SET_FIELD")) {
			return new SessionMessagePayloadImpl(in, out, cc, trace);
		} else if (type.equals("ADD_FIELD")) {
			return new SessionMessageAddToPayloadImpl(in, out, cc, trace);
		} else if (type.equals("SOAP_HEADER")) {
			return new SessionMessageSoapHeaderImpl(in, out, cc, trace);
		} else if (type.equals("ASMA")) {
			return new SessionMessageASMAImpl(in, out, cc, dynConfig, trace);
		} else {
			return new SessionMessageIdentityImpl(in, out, cc, trace);
		}
	}

}