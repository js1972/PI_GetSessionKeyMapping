package au.com.inpex.mapping;

import com.sap.aii.mapping.api.DynamicConfiguration;
import com.sap.aii.mapping.api.DynamicConfigurationKey;
import com.sap.aii.mapping.api.TransformationInput;

public class AsmaParameterImpl implements AsmaParameter {
	DynamicConfiguration adapterConfig;
	DynamicConfigurationKey keySessionId;
	
	public AsmaParameterImpl(TransformationInput in) {
		String namespace = in.getInputParameters().getString("DC_NAMESPACE");
		String key = in.getInputParameters().getString("DC_NAME");
		
		adapterConfig = in.getDynamicConfiguration();
		keySessionId = DynamicConfigurationKey.create(namespace, key);
	}
	
	@Override
	public String get() {
		return adapterConfig.get(keySessionId);
	}

	@Override
	public void set(String value) {
		adapterConfig.put(keySessionId, value);
	}

}
