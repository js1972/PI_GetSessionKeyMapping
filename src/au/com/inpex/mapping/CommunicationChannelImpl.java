package au.com.inpex.mapping;

import com.sap.aii.mapping.lookup.Channel;
import com.sap.aii.mapping.lookup.LookupException;
import com.sap.aii.mapping.lookup.LookupService;
import com.sap.aii.mapping.lookup.Payload;
import com.sap.aii.mapping.lookup.SystemAccessor;


/**
 * Abstraction of the PI Communication Channel Lookup Service
 *
 */
public class CommunicationChannelImpl implements CommunicationChannel {
	public String businessComponent = "";
	public String channelName = "";
	
	
	public CommunicationChannelImpl(String bc, String cn) {
		businessComponent = bc;
		channelName = cn;
	}

	@Override
	public Payload call(Payload request) throws LookupException {
		SystemAccessor accessor = null;
		Payload response = null;

		Channel piChannel = LookupService.getChannel(businessComponent, channelName);
		accessor = LookupService.getSystemAccessor(piChannel);
		response = accessor.call(request);

		return response;
	}

}
