package au.com.inpex.mapping;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;


public class GetSessionKeyMapping extends AbstractTransformation {
	private static String mappingType = "";
	
	/**
	 * PI mapping entry point.
	 * Instantiate dependencies; then use an abstract factory to create
	 * the appropriate SessionMessage class, based on the MAPPING_TYPE
	 * PI mapping parameter.
	 */
	public void transform(TransformationInput arg0, TransformationOutput arg1) throws StreamTransformationException {
		mappingType = arg0.getInputParameters().getString("MAPPING_TYPE");
		traceInfo("java mapping - processing start with MAPPING_TYPE = " + mappingType);
		
		CommunicationChannel cc = new CommunicationChannelImpl(
			arg0.getInputParameters().getString("BUSINESS_COMPONENT"),
			arg0.getInputParameters().getString("BUSINESS_COMPONENT_CHANNEL")
		);
		AsmaParameter dynConfig = new AsmaParameterImpl(arg0);
		
		SessionMessageFactoryImpl smf = SessionMessageFactoryImpl.getInstance();
		SessionMessage sessionMessageHandler = smf.createSessionMessageHandler(mappingType, arg0, arg1, cc, dynConfig, getTrace());
		sessionMessageHandler.process();
	}

	void traceInfo(String msg) {
		try {
			getTrace().addInfo(msg);
		}
		catch (Exception e) { }
	}

}
