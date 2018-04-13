package org.binas.station.ws.cli;

import java.util.Collection;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

/** Client application. */
public class StationClientApp {

	public static void main(String[] args) throws Exception {
		// Check arguments.
		if (args.length == 0) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + StationClientApp.class.getName() + " wsURL OR uddiURL wsName");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;
		//if (args.length == 1) {
		//	wsURL = args[0];
		//} else if (args.length >= 2) {
		//	uddiURL = args[0];
		//	wsName = args[1];
		//}
		wsURL = args[0];
		uddiURL = args[1];
		wsName = args[2];
		System.out.println(StationClientApp.class.getSimpleName() + " running");

		// Create client.
		StationClient client = null;

		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
	    	client = new StationClient(wsURL);
		} else if (uddiURL != null) {
			System.out.printf("Creating client using UDDI at %s for server with name %s%n", uddiURL, wsName);
			client = new StationClient(uddiURL, wsName);
		}
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		 
        Collection<UDDIRecord> records = uddiNaming.listRecords("A09_Station%");
        
        for(UDDIRecord r:records) {
        	//System.out.println(r.getOrgName());
        	client = new StationClient(r.getUrl());

    		System.out.println(client.getInfo().getId());
        }
		
    	//client = new StationClient(wsURL);

		//System.out.println(client.getInfo().getId());

		
		// The following remote invocation is just a basic example.
		// The actual tests are made using JUnit.

		System.out.println("Invoke ping()...");
		String result = client.testPing("client");
		System.out.print("Result: ");
		System.out.println(result);
	}

}
