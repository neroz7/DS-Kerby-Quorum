package org.binas.ws;

import java.io.IOException;
import java.util.Collection;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

/** The endpoint manager starts and registers the service. */
public class BinasEndpointManager {

	/** UDDI naming server location */
	private String uddiURL = null;
	/** Web Service name */
	private String wsName = null;

	/** Get Web Service UDDI publication name */
	public String getWsName() {
		return wsName;
	}

	/** Web Service location to publish */
	private String wsURL = null;

	/** Port implementation */
	private BinasPortImpl portImpl = new BinasPortImpl(this);

	/** Obtain Port implementation */
	public BinasPortImpl getPort() {
		return portImpl;
	}

	/** Web Service end point */
	private Endpoint endpoint = null;

	/** UDDI Naming instance for contacting UDDI server */
	private UDDINaming uddiNaming = null;
	
	/** Get UDDI Naming instance for contacting UDDI server */
	UDDINaming getUddiNaming() {
		return uddiNaming;
	}

	/** output option */
	private boolean verbose = true;

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	/** constructor with provided UDDI location, WS name, and WS URL */
	public BinasEndpointManager(String uddiURL, String wsName, String wsURL) {
		this.uddiURL = uddiURL;
		this.wsName = wsName;
		this.wsURL = wsURL;
		try {
			uddiNaming = new UDDINaming(uddiURL);
		} catch (UDDINamingException e) {
			e.printStackTrace();
		}
	}

	/** constructor with provided web service URL */
	public BinasEndpointManager(String wsName, String wsURL) {
		this.wsName = wsName;
		this.wsURL = wsURL;
	}

	/* end point management */

	public void start() throws Exception {
		try {
			// publish end point
			endpoint = Endpoint.create(this.portImpl);
			if (verbose) {
				System.out.printf("Starting %s%n", wsURL);
			}
			endpoint.publish(wsURL);
		} catch (Exception e) {
			endpoint = null;
			if (verbose) {
				System.out.printf("Caught exception when starting: %s%n", e);
				e.printStackTrace();
			}
			throw e;
		}
		publishToUDDI();
	}

	public void awaitConnections() {
		if (verbose) {
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
		}
		try {
			System.in.read();
		} catch (IOException e) {
			if (verbose) {
				System.out.printf("Caught i/o exception when awaiting requests: %s%n", e);
			}
		}
	}

	public void stop() throws Exception {
		try {
			if (endpoint != null) {
				// stop end point
				endpoint.stop();
				if (verbose) {
					System.out.printf("Stopped %s%n", wsURL);
				}
			}
		} catch (Exception e) {
			if (verbose) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
		this.portImpl = null;
		unpublishFromUDDI();
	}

	/* UDDI */

	void publishToUDDI() throws UDDINamingException {
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		uddiNaming.rebind(wsName, wsURL);
	}
	
	void lookUpForStations(String name) throws UDDINamingException {
		uddiNaming.lookup(name);
	}

	void unpublishFromUDDI() throws UDDINamingException {
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		uddiNaming.unbind(wsName);
	}

	public Collection<UDDIRecord> getListOfRecords() throws UDDINamingException {
		uddiNaming = new UDDINaming("http://a09:dAgMX5F@uddi.sd.rnl.tecnico.ulisboa.pt:9090/");
		return uddiNaming.listRecords("A09_Station%");
	}

}