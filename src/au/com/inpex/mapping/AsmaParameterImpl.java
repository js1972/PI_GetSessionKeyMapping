package au.com.inpex.mapping;

import au.com.inpex.mapping.exceptions.AsmaParameterException;

import com.sap.aii.mapping.api.DynamicConfiguration;
import com.sap.aii.mapping.api.DynamicConfigurationKey;
import com.sap.aii.mapping.api.TransformationInput;

public class AsmaParameterImpl implements AsmaParameter {
	DynamicConfiguration adapterConfig;
	DynamicConfigurationKey keySessionId;
	String namespace = "";
	
	public AsmaParameterImpl(TransformationInput in) {
		try {
			namespace = in.getInputParameters().getString("DC_NAMESPACE");
			String key = in.getInputParameters().getString("DC_NAME");
			if (namespace == null || namespace.equals("") || namespace == null || namespace.equals("")) {
				throw new AsmaParameterException("PI mapping parameters not defined (DC_NAMESPACE, DC_NAME)");
			}

			adapterConfig = in.getDynamicConfiguration();
			keySessionId = DynamicConfigurationKey.create(namespace, key);
		}
		catch (Exception e) {
			throw new AsmaParameterException(e.getMessage());
		}
	}
	
	@Override
	public String get(String key) {
		DynamicConfigurationKey dck = DynamicConfigurationKey.create(namespace, key);
		return adapterConfig.get(dck);
	}

	@Override
	public void set(String key, String value) {
		DynamicConfigurationKey dck = DynamicConfigurationKey.create(namespace, key);
		adapterConfig.put(dck, value);
	}

}
