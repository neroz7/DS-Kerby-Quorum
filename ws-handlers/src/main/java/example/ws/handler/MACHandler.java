package example.ws.handler;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class MACHandler implements SOAPHandler<SOAPMessageContext> {

	

	private static final String VALID_CLIENT_NAME = "alice@A09.binas.org";
	private static final String VALID_CLIENT_PASSWORD = "tzpxRB7";
	private static final String VALID_SERVER_NAME = "binas@A09.binas.org";
	private static final String VALID_SERVER_PASSWORD = "yRTg3mgJK";
	private static final int VALID_DURATION = 30;
	//
	// Handler interface implementation
	//

	
	// PARA IMPLEMENTAR
	public static final String CONTEXT_KCS = "my.Kcs";
	public static final String CONTEXT_KCS2 = "my.Kcs2";

	
	
	/**
	 * Gets the header blocks that can be processed by this Handler instance. If
	 * null, processes all.
	 */
	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	/**
	 * The handleMessage method is invoked for normal processing of inbound and
	 * outbound messages.
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("MACHandler: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				System.out.println("Writing MAC to header to OUTbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				

				// add header element (name, namespace prefix, namespace)
				Name name = se.createName("MAC", "mac", "http://demo");
				
				SOAPElement element = sh.addHeaderElement(name);
				Key sessionKey = (Key) smc.get(CONTEXT_KCS);
				//smc.remove(CONTEXT_KCS);
				
				//OutputStream out = new ByteArrayOutputStream();
				//msg.writeTo(out);
				String M = msg.getSOAPBody().getTextContent();

				
				final Key MAC = getKey(M + new String(sessionKey.getEncoded()));
				System.out.println("SEND Session Key : " + new String(sessionKey.getEncoded()));
				System.out.println("SEND SOAP BODY : " + M);

				// add header element value
				String MACString = BytesToStringHex(MAC.getEncoded());
				System.out.println("SEND MAC : " + MACString);

				element.addTextNode(MACString);
				
			} else {
				System.out.println("Reading MAC in header from INbound SOAP message...");

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				
				// get first header element
				Name name = se.createName("MAC", "mac", "http://demo");
				Iterator<?> it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();
				
				// get header element value
				String MACString = element.getValue();
				Key sessionKey = (Key) smc.get(CONTEXT_KCS);
				
				if(sessionKey == null) {
					System.out.println("SESSION KEY NULL!");
				}else {	
					//OutputStream out = new ByteArrayOutputStream();
					//msg.writeTo(out);
					String M = msg.getSOAPBody().getTextContent();
					final Key MAC = getKey(M + new String(sessionKey.getEncoded()));
					System.out.println("RECEIVE Session Key : " + new String(sessionKey.getEncoded()));;
					System.out.println("RECEIVE SOAP BODY : " + M);
					System.out.println("RECEIVE MAC : " + MACString);
	
					String MACString2 = BytesToStringHex(MAC.getEncoded());
					System.out.println("GENERATED MAC : " + MACString2);
	
					if(!MACString.equals(MACString2)) {
						System.out.println("INTEGRIDADE VIOLADA! MAC");
						//FAULT
					}
				}
				// print received header
				//System.out.println("Header value is " + value);

				/*// put header in a property context
				smc.put(CONTEXT_PROPERTY, value);
				// set property scope to application client/server class can
				// access it
				smc.setScope(CONTEXT_PROPERTY, Scope.APPLICATION);
*/
			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		return true;	}

	/** The handleFault method is invoked for fault message processing. */
	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		return true;
	}

	/**
	 * Called at the conclusion of a message exchange pattern just prior to the
	 * JAX-WS runtime dispatching a message, fault or exception.
	 */
	@Override
	public void close(MessageContext messageContext) {
		// nothing to clean up
	}
	// Test Helpers -------------------------------------------------------------
	private static Key getKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return SecurityHelper.generateKeyFromPassword(password);
	}
	
	public static byte[] StringHexToBytes(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String BytesToStringHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
