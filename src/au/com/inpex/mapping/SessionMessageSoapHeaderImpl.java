package au.com.inpex.mapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.com.inpex.mapping.exceptions.BuildMessagePayloadException;
import au.com.inpex.mapping.exceptions.SessionKeyResponseException;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupService;
import com.sap.aii.mapping.lookup.Payload;


/**
 * Copy the input to the output and wrap in a soap header with the session key.
 * The soap header fields are defined by variables node and fieldName. These are
 * extracted from PI mapping parameters and are used as follows:
 * 
 * <node><fieldName>---sessionkey---</fieldName></node>
 * 
 * This scenario requires the receiver adapter to be in nosoap mode to allow us
 * manually construct the soap header. You cannot alter the soap header
 * otherwise.
 * 
 */
public class SessionMessageSoapHeaderImpl extends SessionMessage {
	private String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:pi:session:key\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
		+ "<soapenv:Header>" + "<urn:SessionHeader>" + "<urn:sessionId>";
	private String suffix = "</urn:sessionId></urn:SessionHeader></soapenv:Header><soapenv:Body>";
	private String envelope = "</soapenv:Body></soapenv:Envelope>";
	
	private String node = "";
	
	
	SessionMessageSoapHeaderImpl(TransformationInput in, TransformationOutput out, CommunicationChannel cc, AsmaParameter dc, AbstractTrace trace) {
		super(in, out, cc, dc, trace);
		node = in.getInputParameters().getString("SOAP_HEADER_NODE");
	}
	
	@Override
	protected String getSessionKeyFromResponse(Payload response) {
		String sessionId = null;
		
		InputStream is = response.getContent();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document document;
			document = builder.parse(is);
			NodeList nodes = document.getElementsByTagName(sessionKeyResponseField);
			Node node = nodes.item(0);
			
			if (node != null) {
				node = node.getFirstChild();
				if (node != null) {
					sessionId = node.getNodeValue();
				}
			}
			
		} catch (Exception e) {
			throw new SessionKeyResponseException(e.getMessage());
		}
		
		return sessionId;
	}

	@Override
	protected Payload setRequestPayload() {
		String loginXml = "<SessionKeyRequest xmlns=\"urn:pi:session:key\"><data>session key request from soap-header handler</data></SessionKeyRequest>";
		InputStream is = new ByteArrayInputStream(loginXml.getBytes());
		Payload payload = LookupService.getXmlPayload(is);
		
		return payload;
	}

	/**
	 * Copy input to output then wrap it in a soap envelope with
	 * the session Id in the soap header.
	 * Note: requires comm.channel to be in 'nosoap' mode.
	 */
	@Override
	protected void buildMessage(String sessionId) {		
		try {
			char[] buffer = new char[100];
			StringBuilder str = new StringBuilder();
			
			Reader reader = new InputStreamReader(messageInputstream);
			
			try {
				for (;;) {
					int read_size = reader.read(buffer, 0, buffer.length);
					if (read_size < 0) {
						break;
					}
					str.append(buffer, 0, read_size);
				}
			}
			catch (IOException e) {
			}
			finally {
				try {
					reader.close();
				}
				catch (IOException e) {
					
				}
			}
			
			// wrap payload in soap envelope/header
			prefix.replaceAll("SessionHeader", node);
			prefix.replaceAll("sessionId", fieldName);
			
			messageOutputStream.write(prefix.getBytes());
			messageOutputStream.write(sessionId.getBytes());
			messageOutputStream.write(suffix.getBytes());
			messageOutputStream.write(str.toString().getBytes());
			messageOutputStream.write(envelope.getBytes());
			
		} catch (IOException e) {
			throw new BuildMessagePayloadException(e.getMessage());
		}
	}

}
