package au.com.inpex.mapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupService;
import com.sap.aii.mapping.lookup.Payload;

/**
 * Implement a SessionMessage handler (based on the Template pattern).
 * The output payload contains the session key as a field appended to the
 * input payload.
 * 
 * @author jscott
 *
 */
public class SessionMessagePayloadImpl extends SessionMessage {

	SessionMessagePayloadImpl(TransformationInput in, TransformationOutput out, CommunicationChannel cc, AbstractTrace trace) {
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
	 * Copy input to output and change the specified node to hold the 
	 * session Id.
	 */
	@Override
	protected void buildMessage(String sessionId) {
		logInfo("Building message with sessionId: " + sessionId);
		
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document document = builder.parse(messageInputstream);
			NodeList nodes = document.getElementsByTagName("id");
			
			if (nodes.getLength() == 0) {
				logInfo("'id' element not found!");
			} else {
				Node node = nodes.item(0);
				node.setTextContent(sessionId);
			}
			
			DOMSource source = new DOMSource(document);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			
			StreamResult streamResult = new StreamResult(messageOutputStream);
			transformer.transform(source, streamResult);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
