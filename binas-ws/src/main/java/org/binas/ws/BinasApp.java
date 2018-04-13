package org.binas.ws;

import java.util.Map;

import javax.xml.ws.BindingProvider;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import org.binas.domain.BinasManager;
import org.binas.station.ws.StationPortType;
import org.binas.station.ws.StationService;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;


public class BinasApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
				if (args.length < 2) {
					System.err.println("Argument(s) missing!");
					System.err.printf("Usage: java %s uddiURL name%n", BinasApp.class.getName());
					return;
				}

				String uddiURL = args[0];
				String wsName = args[1];
				String wsURL = args[2];
			
				BinasEndpointManager endpoint = new BinasEndpointManager(uddiURL, wsName, wsURL);
				BinasManager.getInstance().setId(wsName);
				
				try {
					
					endpoint.start();
					endpoint.awaitConnections();
								
				} finally {
					endpoint.stop();
				}
				
	}
}