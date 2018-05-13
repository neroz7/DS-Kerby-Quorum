package example.ws.handler;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.RequestTime;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class MACHandler implements SOAPHandler<SOAPMessageContext> {

	//
	// Handler interface implementation
	//

	
	// PARA IMPLEMENTAR

	/** Symmetric cryptography algorithm. */
	private static final String SYM_ALGO = "AES";
	/** Symmetric algorithm key size. */
	private static final int SYM_KEY_SIZE = 128;
	/** Length of initialization vector. */
	private static final int SYM_IV_LEN = 16;
	/** Number generator algorithm. */
	private static final String NUMBER_GEN_ALGO = "SHA1PRNG";

	/** Message authentication code algorithm. */
	private static final String MAC_ALGO = "HmacSHA256";

	/**
	 * Symmetric cipher: combination of algorithm, block processing, and
	 * padding.
	 */
	private static final String SYM_CIPHER = "AES/CBC/PKCS5Padding";
	/** Digest algorithm. */
	private static final String DIGEST_ALGO = "SHA-256";

	
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
		System.out.println("KerberosServerHeaderHandler: Handling message.");

		//Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		try {
			System.out.println("Writing header to OUTbound SOAP message...");

			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
				
			SecretKey key = generateMACKey(SYM_KEY_SIZE);
				
			byte[] msgBytes = msg.toString().getBytes(); 
				
			byte[] cipherDigest = makeMAC(msgBytes, key);

			// verify the MAC
			boolean result = verifyMAC(cipherDigest, msgBytes, key);
				
			return result;
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}
		return true;
	}

	/** Generates a SecretKey for using in message authentication code. */
	private static SecretKey generateMACKey(int keySize) throws Exception {
		// generate an AES secret key
		KeyGenerator keyGen = KeyGenerator.getInstance(SYM_ALGO);
		keyGen.init(keySize);
		SecretKey key = keyGen.generateKey();

		return key;
	}

	/** Makes a message authentication code. */
	private static byte[] makeMAC(byte[] bytes, SecretKey key) throws Exception {

		Mac cipher = Mac.getInstance(MAC_ALGO);
		cipher.init(key);
		byte[] cipherDigest = cipher.doFinal(bytes);

		return cipherDigest;
	}

	/**
	 * Calculates new digest from text and compare it to the to deciphered
	 * digest.
	 */
	private static boolean verifyMAC(byte[] cipherDigest, byte[] bytes, SecretKey key) throws Exception {

		Mac cipher = Mac.getInstance(MAC_ALGO);
		cipher.init(key);
		byte[] cipheredBytes = cipher.doFinal(bytes);
		return Arrays.equals(cipherDigest, cipheredBytes);
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

}
