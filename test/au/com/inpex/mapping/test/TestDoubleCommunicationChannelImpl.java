package au.com.inpex.mapping.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import au.com.inpex.mapping.CommunicationChannel;

import com.sap.aii.mapping.lookup.LookupService;
import com.sap.aii.mapping.lookup.Payload;


public class TestDoubleCommunicationChannelImpl implements CommunicationChannel {
	public String businessComponent = "";
	public String channelName = "";
	
	
	public TestDoubleCommunicationChannelImpl(String bc, String cn) {
		businessComponent = bc;
		channelName = cn;
	}
	
	@Override
	public Payload call(Payload request) {
		String loginXml = "<SessionKeyRequest><key>***SessionIdValue***</key></SessionKeyRequest>";
		InputStream is = new ByteArrayInputStream(loginXml.getBytes());
		Payload payload = LookupService.getXmlPayload(is);
		return payload;
	}

}
