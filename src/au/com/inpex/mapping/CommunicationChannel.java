package au.com.inpex.mapping;

import com.sap.aii.mapping.lookup.Payload;

public interface CommunicationChannel {
	public Payload call(Payload request);
}
