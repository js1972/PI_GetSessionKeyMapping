package au.com.inpex.mapping.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import au.com.inpex.mapping.AsmaParameter;
import au.com.inpex.mapping.CommunicationChannel;
import au.com.inpex.mapping.SessionMessage;
import au.com.inpex.mapping.SessionMessageFactory;
import au.com.inpex.mapping.SessionMessageFactoryImpl;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.InputParameters;
import com.sap.aii.mapping.api.InputPayload;
import com.sap.aii.mapping.api.OutputPayload;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.Channel;


/**
 * Test the session message class with JUnit and Mockito.
 * Test doubles are used for the CommunicationChannelImpl and
 * AsmaParameterImpl classes.
 * A test method exists for each SessionMessage concrete class.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSessionMessage {
	// Declare mock objects
	@Mock
	SessionMessageFactory factoryMock;
	@Mock
	TransformationInput inMock;
	@Mock
	TransformationOutput outMock;
	@Mock
	AbstractTrace traceMock;
	
	// @InjectMocks is not necessary as we are using constructor injection below
	SessionMessage sessionMessageHandler;
	OutputPayload outputPayload;
	
	// Setup test doubles
	CommunicationChannel cc = new TestDoubleCommunicationChannelImpl("BC_Jason", "GetSessionKey_BasicAuth_R_SOAP");
	AsmaParameter asma = new TestDoubleAsmaParameterImpl();
	
	
	/**
	 * Configure our mocks with responses. getOutputStream() within
	 * the outMock must only respond with a new stream the first time
	 * otherwise we will lose the result of processing by the class
	 * under test.
	 */
	@Before
	public void setup() {
		when(inMock.getInputPayload()).thenReturn(new InputPayload() {
			@Override
			public InputStream getInputStream() {
				String input = "<input><id></id><dummy>hello!</dummy></input>";
				InputStream is = new ByteArrayInputStream(input.getBytes());
				return is;
			}
		});

		when(inMock.getInputParameters()).thenReturn(new InputParameters() {
			@Override
			public Object getValue(String arg0) {
				return null;
			}
			
			@Override
			public String getString(String key) {
				return "";
			}
			
			@Override
			public int getInt(String arg0) {
				return 0;
			}
			
			@Override
			public Channel getChannel(String arg0) {
				return null;
			}
		});

		outputPayload = new OutputPayload() {
			OutputStream os = null;
			
			@Override
			public OutputStream getOutputStream() {
				if (os == null) {
					os = new ByteArrayOutputStream();
				}
				return os;
			}
		};
		when(outMock.getOutputPayload()).thenReturn(outputPayload);
	}

	@Test
	public void testProcessWithIdentityImpl() {
		CommunicationChannel cc = new TestDoubleCommunicationChannelImpl("BC_Jason", "GetSessionKey_BasicAuth_R_SOAP");
		AsmaParameter asma = new TestDoubleAsmaParameterImpl();
		
		SessionMessageFactoryImpl smf = SessionMessageFactoryImpl.getInstance();
		sessionMessageHandler = smf.createSessionMessageHandler("", inMock, outMock, cc, asma, traceMock);
		sessionMessageHandler.process();
		
		String output = outputPayload.getOutputStream().toString();
		assertEquals("<input><id></id><dummy>hello!</dummy></input>", output);
		
		verify(traceMock).addInfo("Session Id: ***SessionIdValue***");
	}
	
	@Test
	public void testProcessWithIdentityAddToPayloadImpl() {
		CommunicationChannel cc = new TestDoubleCommunicationChannelImpl("BC_Jason", "GetSessionKey_BasicAuth_R_SOAP");
		AsmaParameter asma = new TestDoubleAsmaParameterImpl();
		
		SessionMessageFactoryImpl smf = SessionMessageFactoryImpl.getInstance();
		sessionMessageHandler = smf.createSessionMessageHandler("ADD_FIELD", inMock, outMock, cc, asma, traceMock);
		sessionMessageHandler.process();
		
		String output = outputPayload.getOutputStream().toString();
		assertEquals("<input><id/><dummy>hello!</dummy><sessionId>***SessionIdValue***</sessionId></input>", output);
	}
	
	@Test
	public void testProcessWithPayloadImpl() {
		CommunicationChannel cc = new TestDoubleCommunicationChannelImpl("BC_Jason", "GetSessionKey_BasicAuth_R_SOAP");
		AsmaParameter asma = new TestDoubleAsmaParameterImpl();
		
		SessionMessageFactoryImpl smf = SessionMessageFactoryImpl.getInstance();
		sessionMessageHandler = smf.createSessionMessageHandler("SET_FIELD", inMock, outMock, cc, asma, traceMock);
		sessionMessageHandler.process();
		
		String output = outputPayload.getOutputStream().toString();
		assertEquals("<input><id>***SessionIdValue***</id><dummy>hello!</dummy></input>", output);
	}
	
	@Test
	public void testProcessWithSoapHeaderImpl() {
		CommunicationChannel cc = new TestDoubleCommunicationChannelImpl("BC_Jason", "GetSessionKey_BasicAuth_R_SOAP");
		AsmaParameter asma = new TestDoubleAsmaParameterImpl();
		
		SessionMessageFactoryImpl smf = SessionMessageFactoryImpl.getInstance();
		sessionMessageHandler = smf.createSessionMessageHandler("SOAP_HEADER", inMock, outMock, cc, asma, traceMock);
		sessionMessageHandler.process();
		
		String output = outputPayload.getOutputStream().toString();
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:pi:session:key\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Header><urn:SessionHeader><urn:sessionId>***SessionIdValue***</urn:sessionId></urn:SessionHeader></soapenv:Header><soapenv:Body><input><id></id><dummy>hello!</dummy></input></soapenv:Body></soapenv:Envelope>", output);
	}
	
	@Test
	public void testProcessWithASMAImpl() {
		CommunicationChannel cc = new TestDoubleCommunicationChannelImpl("BC_Jason", "GetSessionKey_BasicAuth_R_SOAP");
		AsmaParameter asma = new TestDoubleAsmaParameterImpl();
		
		SessionMessageFactoryImpl smf = SessionMessageFactoryImpl.getInstance();
		sessionMessageHandler = smf.createSessionMessageHandler("ASMA", inMock, outMock, cc, asma, traceMock);
		sessionMessageHandler.process();
		
		String output = outputPayload.getOutputStream().toString();
		assertEquals("<input><id></id><dummy>hello!</dummy></input>", output);
		
		assertEquals("***SessionIdValue***", asma.get());
	}
}
