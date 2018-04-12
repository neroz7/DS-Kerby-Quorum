package org.binas.ws;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import javax.xml.ws.BindingProvider;

import org.binas.domain.BinasManager;
import org.binas.domain.User;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.StationPortType;
import org.binas.station.ws.StationService;
import org.binas.station.ws.cli.StationClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;


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
	
	Map<String,StationView> stations = new HashMap<String,StationView>();
	
	StationService service = null;
	
	StationPortType port = null;
	
	/** Constructor receives a reference to the endpoint manager. */
	public BinasPortImpl(BinasEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
		this.binas = BinasManager.getInstance();
	}
	
	public void addStation(String stationId) throws UDDINamingException {
		
		System.out.printf("add Station with name %s",stationId);
		String endpointAddress = endpointManager.getUddiNaming().lookup(stationId);

		if (endpointAddress == null) {
			System.out.println("Not found!");
			return;
		} else {
			System.out.printf("Found %s%n", endpointAddress);
		}

		StationService service = new StationService();
		StationPortType port = service.getStationPort();
		
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		
		StationView station = new StationView();
		
		station.setAvailableBinas(port.getInfo().getAvailableBinas());
		station.setCapacity(port.getInfo().getCapacity());
		station.setFreeDocks(port.getInfo().getFreeDocks());
		station.setId(port.getInfo().getId());
		station.setTotalGets(port.getInfo().getTotalGets());
		station.setTotalReturns(port.getInfo().getTotalReturns());
		CoordinatesView crd = new CoordinatesView();
		crd.setX(port.getInfo().getCoordinate().getX());
		crd.setY(port.getInfo().getCoordinate().getY());
		station.setCoordinate(crd);
		
		stations.put(station.getId(),station);
	}
	public UserView activateUser(String email) {
		return buildUserView(binas.activateUser(email));
	}

	private UserView buildUserView(User user) {
		UserView userView = new UserView();
		userView.setCredit(user.getCredit());
		userView.setEmail(user.getEmail());
		userView.setHasBina(user.isHasBina());
		return userView;
	}
	
	private void lookUpAndCreatStup(String stationId) throws InvalidStation_Exception {
		String endpointAddress = null;
		try {
			endpointAddress = endpointManager.getUddiNaming().lookup(stationId);
		} catch (UDDINamingException e) {
			e.printStackTrace();
		}

		if (endpointAddress == null) {
			throw new InvalidStation_Exception(stationId, null);
		}

	    service = new StationService();
		port = service.getStationPort();
		
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
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
		
		if(binas.getUser(email).getCredit() == 0) {
			throw new NoCredit_Exception(email, null);
		}
		if(binas.getUser(email).isHasBina()) {
			throw new AlreadyHasBina_Exception(email, null);
		}
		
		lookUpAndCreatStup(stationId);
		
		binas.getUser(email).setHasBina(true);
		
		if(port.getInfo().getCapacity() == 0) {
			throw new NoBinaAvail_Exception(stationId, null);
		}
		
		try {
			port.getBina();
		}  catch (org.binas.station.ws.NoBinaAvail_Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void returnBina(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		
		User user = binas.getUser(email);
		
		lookUpAndCreatStup(stationId);
		
		if(port.getInfo().getAvailableBinas() == 0) {
			throw new NoBinaRented_Exception(stationId, null);
		}
		
		try {
			port.returnBina();
		} catch (NoSlotAvail_Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String testPing(String inputMessage) {
		// If no input is received, return a default name.
		if (inputMessage == null || inputMessage.trim().length() == 0)
			inputMessage = "friend";
				
		// If the station does not have a name, return a default.
		String wsName = endpointManager.getWsName();
		if (wsName == null || wsName.trim().length() == 0)
				wsName = "Station";
			
		// Build a string with a message to return.
		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(inputMessage);
		builder.append(" from ").append(wsName);
		return builder.toString();
	}

	@Override
	public void testClear() {
		BinasManager.getInstance().clearUsers();
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws BadInit_Exception {
		try {
			port.testInit(x, y, capacity, returnPrize);
		} catch (org.binas.station.ws.BadInit_Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		// TODO Auto-generated method stub
		
	}
	
}
