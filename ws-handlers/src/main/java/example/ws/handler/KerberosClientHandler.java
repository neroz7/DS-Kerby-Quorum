package example.ws.handler;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class KerberosClientHandler implements SOAPHandler<SOAPMessageContext> {

	//
	// Handler interface implementation
	//

	
	// PARA IMPLEMENTAR

	
	
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

		System.out.println("KerberosClientHeaderHandler: Handling message.");

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		try {
			if (outboundElement.booleanValue()) {
				System.out.println("Writing header to OUTbound SOAP message...");

				final String VALID_CLIENT_NAME = "alice@A09.binas.org";
				final String VALID_CLIENT_PASSWORD = "tzpxRB7";
				final String VALID_SERVER_NAME = "binas@A09.binas.org";
				final String VALID_SERVER_PASSWORD = "yRTg3mgJK";
				final int VALID_DURATION = 30;

				SecureRandom randomGenerator = new SecureRandom();
				long nounce = randomGenerator.nextLong();

				final Key clientKey = getKey(VALID_CLIENT_PASSWORD); // get passwords keys
				final Key serverKey = getKey(VALID_SERVER_PASSWORD);
				
				String wsURL = "http://sec.sd.rnl.tecnico.ulisboa.pt:8888/kerby";
				KerbyClient client = new KerbyClient(wsURL);
				
				SessionKeyAndTicketView result = client.requestTicket(VALID_CLIENT_NAME, VALID_SERVER_NAME, nounce, VALID_DURATION);

				CipheredView cipheredSessionKey = result.getSessionKey();
				CipheredView cipheredTicket = result.getTicket();
				
				SessionKey sessionKey = new SessionKey(cipheredSessionKey, clientKey);
				
				Ticket ticket = new Ticket(cipheredTicket, serverKey);
				long timeDiff = ticket.getTime2().getTime() - ticket.getTime1().getTime();
				
				Auth auth = new Auth(VALID_CLIENT_NAME, new Date());
//				Auth auth2 = new Auth();
				Node node_t = ticket.toXMLNode("Ticket");
				Node node_a = ticket.toXMLNode("Authentication");

				
				System.out.println("Writing header to OUTbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();

				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();
				sh.appendChild(node_t);
				sh = se.addHeader();
				sh.appendChild(node_a);
				
				//Name name = se.createName("myHeader", "d", "http://demo");

				//SOAPHeaderElement element = sh.addHeaderElement(name);

				// add header element value
				//element.appendChild(node_t);
				

			} else {
				System.out.println("Reading header from INbound SOAP message...");

				

			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		
		return true;
	}

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
	
}
