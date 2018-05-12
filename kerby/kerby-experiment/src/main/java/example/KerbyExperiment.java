package example;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Properties;

import pt.ulisboa.tecnico.sdis.kerby.Auth;
import pt.ulisboa.tecnico.sdis.kerby.CipheredView;
import pt.ulisboa.tecnico.sdis.kerby.SecurityHelper;
import pt.ulisboa.tecnico.sdis.kerby.SessionKey;
import pt.ulisboa.tecnico.sdis.kerby.SessionKeyAndTicketView;
import pt.ulisboa.tecnico.sdis.kerby.Ticket;
import pt.ulisboa.tecnico.sdis.kerby.cli.KerbyClient;


public class KerbyExperiment {

    public static void main(String[] args) throws Exception {
        System.out.println("Hi!");

        System.out.println();

        // receive arguments
        System.out.printf("Received %d arguments%n", args.length);

        System.out.println();

        // load configuration properties
        try {
            InputStream inputStream = KerbyExperiment.class.getClassLoader().getResourceAsStream("config.properties");
            // variant for non-static methods:
            // InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");

            Properties properties = new Properties();
            properties.load(inputStream);

            System.out.printf("Loaded %d properties%n", properties.size());

        } catch (IOException e) {
            System.out.printf("Failed to load configuration: %s%n", e);
        }

        System.out.println();

		// client-side code experiments
        System.out.println("Experiment with Kerberos client-side processing");
		System.out.println("...TODO...");
		
		final String VALID_CLIENT_NAME = "alice@CXX.binas.org";
		final String VALID_CLIENT_PASSWORD = "Zd8hqDu23t";
		final String VALID_SERVER_NAME = "binas@CXX.binas.org";
		final String VALID_SERVER_PASSWORD = "MTbvC3";
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
		
		
		
        System.out.println();

		// server-side code experiments
        System.out.println("Experiment with Kerberos server-side processing");
		System.out.println("...TODO...");

        System.out.println();
		
		System.out.println("Bye!");
    }
	
	// Test Helpers -------------------------------------------------------------
	private static Key getKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
		return SecurityHelper.generateKeyFromPassword(password);
	}
}
