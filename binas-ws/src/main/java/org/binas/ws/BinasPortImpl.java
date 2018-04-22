package org.binas.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;
import org.binas.domain.BinasManager;
import org.binas.domain.User;
import org.binas.domain.exception.AlreadyHasBinaException;
import org.binas.domain.exception.BadInitException;
import org.binas.domain.exception.EmailExistsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.InvalidStationException;
import org.binas.domain.exception.NoBinaRentedException;
import org.binas.domain.exception.NoCreditException;
import org.binas.domain.exception.UserNotExistsException;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.UserReplic;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;


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
	
	private boolean replicated = true;
	
	Map<String,StationClient>stationClients = new HashMap<String,StationClient>();
	
	/** Constructor receives a reference to the endpoint manager. 
	  */
	public BinasPortImpl(BinasEndpointManager endpointManager)  {
		this.endpointManager = endpointManager;
		this.binas = BinasManager.getInstance();
		this.stationClients = binas.stationClients;
	}
	
	public UserView activateUser(String email) throws EmailExists_Exception, InvalidEmail_Exception {
		try {
			return buildUserView(binas.activateUser(email));
		}catch(EmailExistsException e) {
			throwEmailExistsException("This email: " + email + "already exists!");
			return null;
		}catch (InvalidEmailException e) {
			throwInvalidEmailException("Invalid Email: " + email);
			return null;
		}
	}

	private double distance(CoordinatesView c1, CoordinatesView c2) {
		return Math.sqrt( Math.pow( (c1.getX()-c2.getX()) , 2) + Math.pow( (c1.getY()-c2.getY()) , 2));	
	}
	
	@Override
	public List<StationView> listStations(Integer numberOfStations, CoordinatesView coordinates){
		
		List<StationView>stationsList = new ArrayList<StationView>();
		
		for(StationClient st : stationClients.values()) {
			stationsList.add(buildStationView(st.getInfo()));
		}
		
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
		
		System.out.println(stationsList.size());
		List<StationView> res = new ArrayList<StationView>();
		for(int i = 0; i < numberOfStations; i++) {
			//System.out.println(stationsList.get(i).getId());
			res.add(stationsList.get(i));
		}
		return res;
	}

	@Override
	public StationView getInfoStation(String stationId) throws InvalidStation_Exception {
		try {
			if(!stationClients.containsKey(stationId)) {
				throw new InvalidStationException();
			}
			return buildStationView(stationClients.get(stationId).getInfo());
		} catch(InvalidStationException e) {
			throwInvalidStationException("Can not find Station with Id: " + stationId);
			return null;
		}
	}

	@Override
	public int getCredit(String email) throws UserNotExists_Exception {
		if(!replicated)
		try {
			return binas.getCredit(email);
		}catch(UserNotExistsException e) {
			throwUserNotExistsException("No User with email "+email);
			return -1;
		}
		else
			return binas.getCreditReplic(email);
	}

	@Override
	public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		try {
			UserReplic usr = null;
			if(!stationClients.containsKey(stationId)) {
				throw new InvalidStationException();
			}
			User user = binas.getUser(email);
			if(replicated)
				usr = binas.getUserReplic(email);
			
			if(user.isHasBina())
				throw new AlreadyHasBinaException();
			else {
				user.setHasBina(true);
				if(!replicated)
					user.setCredit(user.getCredit()+1);
				else
					binas.incCredit(email);
			}
			stationClients.get(stationId).getBina();
						
		} catch(InvalidStationException e){
			throwInvalidStationException("No Station with Id "+stationId);
		} /*catch(NoCreditException e){
			throwNoCreditException("No enough credit with user "+email);
		}*/ catch(UserNotExistsException e){
			throwUserNotExistsException("No User with email "+email);
		} catch (org.binas.station.ws.NoBinaAvail_Exception e) {
			e.printStackTrace();
		} catch (AlreadyHasBinaException e) {
			throwAlreadyHasBinaException("User with email "+email+" already has bina");
		}
	}

	@Override
	public void returnBina(String stationId, String email)
			throws FullStation_Exception, InvalidStation_Exception, NoBinaRented_Exception, UserNotExists_Exception {
		try {
			UserReplic usr = null;
			if(!stationClients.containsKey(stationId)) {
				throw new InvalidStationException();
			}
			StationClient client = stationClients.get(stationId);
			User user = binas.getUser(email);
			if(replicated)
				usr = binas.getUserReplic(email);

			try {
				if(!replicated)
					user.returnBina(client.returnBina());
				else
					binas.returnBina(email, client.returnBina());
			} catch (NoSlotAvail_Exception e) {
				e.printStackTrace();
			}
		} catch(InvalidStationException e) {
			throwInvalidStationException("No Station with Id "+stationId);
		} catch(NoBinaRentedException e) {
			throwNoBinaRentedException("No rented bina in station "+stationId+" for user "+email);
		} catch(UserNotExistsException e) {
			throwUserNotExistsException("No User with email "+email);
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
		for(StationClient client: stationClients.values()) {
			client.testClear();
		}
		BinasManager.getInstance().clearUsers();
	}

	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize)
			throws BadInit_Exception {
		StationClient client;
		try {
			client = stationClients.get(stationId);
			client.testInit(x, y, capacity, returnPrize);
		} catch (org.binas.station.ws.BadInit_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		if(!replicated)
		for(User user: binas.getUsers().values())
			try {
				user.testInit(userInitialPoints);
			} catch (BadInitException e) {
				throwBadInitException("bad inital point" + userInitialPoints);
			}
		else
			try {
				binas.testInit(userInitialPoints);
			} catch (BadInitException e) {
				throwBadInitException("bad inital point" + userInitialPoints);
			} 
	}
	//Views Builders
	
	private StationView buildStationView(org.binas.station.ws.StationView oldStation) {
		StationView station = new StationView();
		station.setAvailableBinas(oldStation.getAvailableBinas());
		station.setCapacity(oldStation.getCapacity());
		station.setCoordinate(buildCoordinatesView(oldStation.getCoordinate()));
		station.setFreeDocks(oldStation.getFreeDocks());
		station.setId(oldStation.getId());
		station.setTotalGets(oldStation.getTotalGets());
		station.setTotalReturns(oldStation.getTotalReturns());
		return station;
	}

	private CoordinatesView buildCoordinatesView(org.binas.station.ws.CoordinatesView view) {
		int x = view.getX();
		int y = view.getY();
		CoordinatesView coordinateView = new CoordinatesView();
		coordinateView.setX(x);
		coordinateView.setY(y);
		return coordinateView;
	}
	
	private UserView buildUserView(User user) {
		UserView userView = new UserView();
		userView.setCredit(user.getCredit());
		userView.setEmail(user.getEmail());
		userView.setHasBina(user.isHasBina());
		return userView;
	}
	
	//Exceptions throwers
	private void throwInvalidEmailException(final String message) throws InvalidEmail_Exception {
		InvalidEmail faultInfo = new InvalidEmail();
		faultInfo.message = message;
		throw new InvalidEmail_Exception(message, faultInfo);
	}
	
	private void throwEmailExistsException(final String message) throws EmailExists_Exception {
		EmailExists faultInfo = new EmailExists();
		faultInfo.message = message;
		throw new EmailExists_Exception(message, faultInfo);
	}
	
	private void throwUserNotExistsException(final String message) throws UserNotExists_Exception {
		UserNotExists faultInfo = new UserNotExists();
		faultInfo.message = message;
		throw new UserNotExists_Exception(message, faultInfo);
	}
	
	private void throwAlreadyHasBinaException(final String message) throws AlreadyHasBina_Exception {
		AlreadyHasBina faultInfo = new AlreadyHasBina();
		faultInfo.message = message;
		throw new AlreadyHasBina_Exception(message, faultInfo);
	}
	
	private void throwNoCreditException(final String message) throws NoCredit_Exception {
		NoCredit faultInfo = new NoCredit();
		faultInfo.message = message;
		throw new NoCredit_Exception(message, faultInfo);
	}
	
	private void throwBadInitException(final String message) throws BadInit_Exception {
		BadInit faultInfo = new BadInit();
		faultInfo.message = message;
		throw new BadInit_Exception(message, faultInfo);
	}
	
	private void throwInvalidStationException(final String message) throws InvalidStation_Exception {
		InvalidStation faultInfo = new InvalidStation();
		faultInfo.message = message;
		throw new InvalidStation_Exception(message, faultInfo);
	}
	
	private void throwNoBinaRentedException(final String message) throws NoBinaRented_Exception {
		NoBinaRented faultInfo = new NoBinaRented();
		faultInfo.message = message;
		throw new NoBinaRented_Exception(message, faultInfo);
	}
	
}
