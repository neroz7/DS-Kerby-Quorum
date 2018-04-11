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
				
				BinasPortImpl port = new BinasPortImpl(endpoint);
				port.addStation("A09_Station1");
				
				port.activateUser("asda@gmail.com");
				
				port.rentBina("A09_Station1", "asda@gmail.com");
				//System.out.println(BinasApp.class.getSimpleName() + " running");

				try {
					endpoint.start();
					endpoint.awaitConnections();
				} finally {
					endpoint.stop();
				}
				
				/*System.out.printf("Contacting UDDI at %s%n", uddiURL);
				UDDINaming uddiNaming = new UDDINaming(uddiURL);

				System.out.printf("Looking for '%s'%n", name);
				String endpointAddress = uddiNaming.lookup(name);

				if (endpointAddress == null) {
					System.out.println("Not found!");
					return;
				} else {
					System.out.printf("Found %s%n", endpointAddress);
				}

				System.out.println("Creating stub ...");
				StationService service = new StationService();
				StationPortType port = service.getStationPort();
				
				System.out.println("Setting endpoint address ...");
				BindingProvider bindingProvider = (BindingProvider) port;
				Map<String, Object> requestContext = bindingProvider.getRequestContext();
				requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);*/
				
				
	}
}