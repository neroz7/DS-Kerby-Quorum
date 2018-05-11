package org.binas.ws.cli;

import java.util.List;

import org.binas.ws.CoordinatesView;
import org.binas.ws.StationView;

public class BinasClientApp {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length == 0) {
            System.err.println("Argument(s) missing!");
            System.err.println("Usage: java " + BinasClientApp.class.getName()
                    + " wsURL OR uddiURL wsName");
            return;
        }
        String uddiURL = null;
        String wsName = null;
        String wsURL = null;
        if (args.length == 1) {
            wsURL = args[0];
        } else if (args.length >= 2) {
            uddiURL = args[0];
            wsName = args[1];
        }

		System.out.println(BinasClientApp.class.getSimpleName() + " running");
		
		BinasClient client = null;

		if (wsURL != null) {
			System.out.printf("Creating client for server at %s%n", wsURL);
			client = new BinasClient(wsURL);
		} else if (uddiURL != null) {
			System.out.printf("Creating client using UDDI at %s for server with name %s%n", uddiURL, wsName);
			client = new BinasClient(uddiURL, wsName);
		}
		
		System.out.println("Invoke ping()...");
		String result = client.testPing("Hello there");
		System.out.println(result);
        /*
		System.out.println("Invoke stationInfo()...");
		String result2  = client.getInfoStation("A09_Station1").getId();
		System.out.print("Result: " + result2);
		*/
		CoordinatesView crd = new CoordinatesView();
		crd.setX(1);
		crd.setY(2);
		List<StationView> stations = client.listStations(3, crd);
		for(StationView st:stations) {
			System.out.println(st.getId());
		}
        
	 }
}

