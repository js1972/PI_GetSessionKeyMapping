package au.com.inpex.mapping;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public interface SessionMessageFactory {
	public SessionMessage createSessionMessageHandler(
		String type,
		TransformationInput in,
		TransformationOutput out,
		CommunicationChannel cc,
		AsmaParameter dynConfig,
		AbstractTrace trace
	);
}
