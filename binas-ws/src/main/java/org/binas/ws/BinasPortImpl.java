package org.binas.ws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.binas.domain.BinasManager;


/**
 * This class implements the Web Service port type (interface). The annotations
 * below "map" the Java class to the WSDL definitions.
 */
// TODO
 @WebService(endpointInterface = "org.binas.ws.BinasPortType",
 wsdlLocation = "binas.1_0.wsdl",
 name ="BinasWebService",
 portName = "BinasPort",
 targetNamespace="http://ws.binas.org/",
 serviceName = "BinasService"
 )
public class BinasPortImpl implements BinasPortType {

	/**
	 * The Endpoint manager controls the Web Service instance during its whole
	 * lifecycle.
	 */
	private BinasEndpointManager endpointManager;
	
	private BinasManager binas;
	Map<String,StationView>stations = new HashMap<String,StationView>();
	
	/** Constructor receives a reference to the endpoint manager. */
	public BinasPortImpl(BinasEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.binas = BinasManager.getInstance();
	}
	
	public UserView activateUser(String email) {
		return binas.activateUser(email);
	}

	private double distance(CoordinatesView c1, CoordinatesView c2) {
		return Math.sqrt( Math.pow( (c1.getX()-c2.getX()) , 2) + Math.pow( (c1.getY()-c2.getY()) , 2));	
	}
	
	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates){
		List<StationView>stationsList = new ArrayList(stations.keySet());
		Comparator<StationView> cmp = new Comparator<StationView>() {
		    @Override
		    public int compare(StationView st1, StationView st2) {
		    	double dif = distance(coordinates, st1.getCoordinate()) - distance(coordinates, st2.getCoordinate());
		        if(dif == 0){
		            return 0;
		        }
		        return dif<0 ? -1 : 1;
		     }
		};
		stationsList.sort(cmp);
		List<StationView> res = new ArrayList<StationView>();
		for(int i = 0; i < numberOfStations; i++) {
			res.add(stationsList.get(i));
		}
		return res;
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		return stations.get(stationId);
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		return binas.getCredit(email);
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
