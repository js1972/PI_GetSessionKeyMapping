package au.com.inpex.mapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupService;
import com.sap.aii.mapping.lookup.Payload;


public class SessionMessageSoapHeaderImpl extends SessionMessage {
	private String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		+ "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:pi:session:key\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
		+ "<soapenv:Header>" + "<urn:SessionHeader>" + "<urn:sessionId>";
	private String suffix = "</urn:sessionId></urn:SessionHeader></soapenv:Header><soapenv:Body>";
	private String envelope = "</soapenv:Body></soapenv:Envelope>";
	
	
	SessionMessageSoapHeaderImpl(TransformationInput in, TransformationOutput out, CommunicationChannel cc, AbstractTrace trace) {
		super(in, out, cc, trace);
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
			NodeList nodes = document.getElementsByTagName("key");
			Node node = nodes.item(0);
			
			if (node != null) {
				node = node.getFirstChild();
				if (node != null) {
					sessionId = node.getNodeValue();
				}
			}
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sessionId;
	}

	@Override
	protected Payload setRequestPayload() {
		String loginXml = "<SessionKeyRequest xmlns=\"urn:pi:session:key\"><data>abc 123</data></SessionKeyRequest>";
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
		logInfo("Building message with sessionId: " + sessionId);
		
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
			
			logInfo(str.toString());
			
			// wrap payload in soap envelope/header

			messageOutputStream.write(prefix.getBytes());
			messageOutputStream.write(sessionId.getBytes());
			messageOutputStream.write(suffix.getBytes());
			messageOutputStream.write(str.toString().getBytes());
			messageOutputStream.write(envelope.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
