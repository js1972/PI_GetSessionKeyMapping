package au.com.inpex.mapping;

import au.com.inpex.mapping.exceptions.AsmaParameterException;
import au.com.inpex.mapping.exceptions.BuildMessagePayloadException;
import au.com.inpex.mapping.exceptions.SessionKeyResponseException;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupException;


public class LogoffSessionKeyMapping extends AbstractTransformation {
	private static String mappingType = "";
	
	
	@Override
	public void transform(TransformationInput inputHandler, TransformationOutput outputHandler) throws StreamTransformationException {
		mappingType = inputHandler.getInputParameters().getString("MAPPING_TYPE");
		traceInfo("java mapping - processing start with MAPPING_TYPE = " + mappingType);
		
		try {
			String businessComponentName = inputHandler.getInputParameters().getString("BUSINESS_COMPONENT");
			String channelName = inputHandler.getInputParameters().getString("BUSINESS_COMPONENT_CHANNEL_LO");
			String dcNamespace = inputHandler.getInputParameters().getString("DC_NAMESPACE");
			String dcKey = inputHandler.getInputParameters().getString("DC_NAME");
			
			String loginXml = "<SessionKeyRequest xmlns=\"urn:pi:session:key\"><data>LOGOFF => session key</data><sessionid></sessionid></SessionKeyRequest>";
			
			au.com.inpex.mapping.lib.SessionMessageFactoryImpl smf = au.com.inpex.mapping.lib.SessionMessageFactoryImpl.getInstance();
			au.com.inpex.mapping.lib.SessionMessage sessionMessageHandler = smf.createLogoffHandler(
				inputHandler,
				outputHandler,
				businessComponentName,
				channelName,
				dcNamespace,
				dcKey,
				getTrace(),
				loginXml,
				"sessionid",
				true);
			
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
