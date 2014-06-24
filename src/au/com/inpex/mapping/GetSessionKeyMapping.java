package au.com.inpex.mapping;

import au.com.inpex.mapping.exceptions.AsmaParameterException;
import au.com.inpex.mapping.exceptions.BuildMessagePayloadException;
import au.com.inpex.mapping.exceptions.SessionKeyResponseException;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupException;


public class GetSessionKeyMapping extends AbstractTransformation {
	private static String mappingType = "";
	
	/**
	 * PI mapping entry point.
	 * Instantiate dependencies; then use an abstract factory to create
	 * the appropriate SessionMessage class, based on the MAPPING_TYPE
	 * PI mapping parameter.
	 */
	public void transform(TransformationInput inputHandler, TransformationOutput outputHandler) throws StreamTransformationException {
		Boolean logoff = false; 
		
		mappingType = inputHandler.getInputParameters().getString("MAPPING_TYPE");
		traceInfo("java mapping - processing start with MAPPING_TYPE = " + mappingType);
		
		try {
			CommunicationChannel cc = new CommunicationChannelImpl(
				inputHandler.getInputParameters().getString("BUSINESS_COMPONENT"),
				inputHandler.getInputParameters().getString("BUSINESS_COMPONENT_CHANNEL")
			);
			
			AsmaParameter dynConfig = new AsmaParameterImpl(inputHandler);
			try {
				logoff = dynConfig.get("logoff").equals("TRUE");
			} catch(Exception e) {
				logoff = false;
			}
			
			if (!logoff) {
				dynConfig.set("logoff", "TRUE");
			}
		
			SessionMessageFactoryImpl smf = SessionMessageFactoryImpl.getInstance();
			SessionMessage sessionMessageHandler = smf.createSessionMessageHandler(
				mappingType,
				logoff,
				inputHandler,
				outputHandler,
				cc,
				dynConfig,
				getTrace());
			
			sessionMessageHandler.process();
		}
		catch (LookupException e) {
			throw new StreamTransformationException("Unable to lookup the comm.channel: " + e.getMessage());
		}
		catch (AsmaParameterException e) {
			throw new StreamTransformationException("Unable to access ASMA: " + e.getMessage());
		}
		catch (SessionKeyResponseException e) {
			throw new StreamTransformationException("Unable to read session key in response: " + e.getMessage());
		}
		catch (BuildMessagePayloadException e) {
			throw new BuildMessagePayloadException("Error adding session key to payload: " + e.getMessage());
		}
	}

	void traceInfo(String msg) {
		try {
			getTrace().addInfo(msg);
		}
		catch (Exception e) { }
	}

}
