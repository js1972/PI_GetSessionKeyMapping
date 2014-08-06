package au.com.inpex.mapping;

import au.com.inpex.mapping.exceptions.AsmaParameterException;
import au.com.inpex.mapping.exceptions.BuildMessagePayloadException;
import au.com.inpex.mapping.exceptions.SessionKeyResponseException;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupException;
import com.sap.tc.logging.Location;


public class GetSessionKeyMapping extends AbstractTransformation {
	private static String mappingType = "";
	
	/**
	 * PI mapping entry point.
	 * Instantiate dependencies; then use an abstract factory to create
	 * the appropriate SessionMessage class, based on the MAPPING_TYPE
	 * PI mapping parameter.
	 */
	public void transform(TransformationInput inputHandler, TransformationOutput outputHandler) throws StreamTransformationException {
		mappingType = inputHandler.getInputParameters().getString("MAPPING_TYPE");
		
		Location location = Location.getLocation(this.getClass().getName());
		location.debugT("java mapping - processing start with MAPPING_TYPE = " + mappingType);
		
		try {
			String businessComponentName = inputHandler.getInputParameters().getString("BUSINESS_COMPONENT");
			String channelName = inputHandler.getInputParameters().getString("BUSINESS_COMPONENT_CHANNEL");
			String sessionIdField = inputHandler.getInputParameters().getString("FIELD_NAME");
			String newSessionIdField = inputHandler.getInputParameters().getString("NEW_SESSIONID_FIELD_NAME");
			String sessionIdResponseField = inputHandler.getInputParameters().getString("SESSION_KEY_RESPONSE_FIELD");
			String dcNamespace = inputHandler.getInputParameters().getString("DC_NAMESPACE");
			String dcKey = inputHandler.getInputParameters().getString("DC_NAME");
			
			String loginXml = "<SessionKeyRequest xmlns=\"urn:pi:session:key\"><data>session key request from add-to-payload handler</data></SessionKeyRequest>";
			
			au.com.inpex.mapping.lib.SessionMessageFactoryImpl smf = au.com.inpex.mapping.lib.SessionMessageFactoryImpl.getInstance();
			au.com.inpex.mapping.lib.SessionMessage sessionMessageHandler = smf.createSessionMessageHandler(
				mappingType,
				inputHandler,
				outputHandler,
				businessComponentName,
				channelName,
				dcNamespace,
				dcKey,
				getTrace(),
				loginXml,
				sessionIdField,
				newSessionIdField,
				sessionIdResponseField);
			
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
}
