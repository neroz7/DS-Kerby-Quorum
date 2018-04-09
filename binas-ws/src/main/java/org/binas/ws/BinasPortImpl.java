package org.binas.ws;

import java.util.List;

import javax.jws.WebService;

import org.binas.domain.BinasManager;


/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
// TODO
 @WebService(endpointInterface = "org.binas.station.ws.StationPortType",
 wsdlLocation = "binas.1_0.wsdl",
 name ="StationWebService",
 portName = "StationPort",
 targetNamespace="http://ws.station.binas.org/",
 serviceName = "StationService"
 )
public class BinasPortImpl implements BinasPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private BinasEndpointManager endpointManager;
	
	private BinasManager binas;
	/** Constructor receives a reference to the endpoint manager. */
	public BinasPortImpl(BinasEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.binas = BinasManager.getInstance();
	}
	
	public UserView activateUser(String email) {
		UserView userView = new UserView();
		userView.setEmail(email);
		userView.setCredit(0);
		return userView;
	}

	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception,
			NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void returnBina(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String testPing(String inputMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void testClear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws BadInit_Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		// TODO Auto-generated method stub
		
	}
	
}
