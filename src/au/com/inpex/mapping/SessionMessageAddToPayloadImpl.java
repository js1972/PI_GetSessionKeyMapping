package au.com.inpex.mapping;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.com.inpex.mapping.exceptions.BuildMessagePayloadException;
import au.com.inpex.mapping.exceptions.SessionKeyResponseException;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.LookupService;
import com.sap.aii.mapping.lookup.Payload;

public class SessionMessageAddToPayloadImpl extends SessionMessage {
	String newSessionIdFieldName = "";
	
	SessionMessageAddToPayloadImpl(TransformationInput in, TransformationOutput out, CommunicationChannel cc, AsmaParameter dc, AbstractTrace trace) {
		super(in, out, cc, dc, trace);
		
		if (fieldName == null || fieldName.equals("")) {
			throw new BuildMessagePayloadException("Enter a value for the FIELD_NAME mapping parameter!");
		}
		newSessionIdFieldName = in.getInputParameters().getString("NEW_SESSIONID_FIELD_NAME");
		if (newSessionIdFieldName == null || newSessionIdFieldName.equals("")) {
			throw new BuildMessagePayloadException("Enter a value for the NEW_SESSIONID_FIELD_NAME mapping parameter!");
		}
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
		String loginXml = "<SessionKeyRequest xmlns=\"urn:pi:session:key\"><data>session key request from add-to-payload handler</data></SessionKeyRequest>";
		InputStream is = new ByteArrayInputStream(loginXml.getBytes());
		Payload payload = LookupService.getXmlPayload(is);
		
		return payload;
	}

	/**
	 * Copy input to output and add a new node for the session Id.
	 */
	@Override
	protected void buildMessage(String sessionId) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = docFactory.newDocumentBuilder();
			Document document = builder.parse(messageInputstream);
			NodeList nodes = document.getElementsByTagName(fieldName);
			
			Element sessionIdElement = document.createElement(newSessionIdFieldName);
			sessionIdElement.appendChild(document.createTextNode(sessionId));
			
			if (nodes.getLength() == 0) {
				logInfo(fieldName + " element not found!");
			} else {
				Node node = nodes.item(0);
				Node parent = node.getParentNode();
				parent.appendChild(sessionIdElement);
			}
			
			DOMSource source = new DOMSource(document);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			
			StreamResult streamResult = new StreamResult(messageOutputStream);
			transformer.transform(source, streamResult);
			
		} catch (Exception e) {
			throw new BuildMessagePayloadException(e.getMessage()); 
		}
	}

}
