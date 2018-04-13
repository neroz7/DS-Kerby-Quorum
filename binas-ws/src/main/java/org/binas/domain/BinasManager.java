package org.binas.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.binas.domain.exception.EmailExistsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.UserNotExistsException;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

public class BinasManager {

	// Singleton -------------------------------------------------------------
	private String id;
	Map<String,User>users = new HashMap<String,User>();
	Map<String,StationClient>stationClients = new HashMap<String,StationClient>();
	private BinasManager() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of Singleton.getInstance()
	 * or the first access to SingletonHolder.INSTANCE, not before.
	 */
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager();
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public synchronized void clearUsers() {
		users.clear();
	}
	public User activateUser(String email) throws InvalidEmailException, EmailExistsException {
		if(users.containsKey(email)) {
			throw new EmailExistsException();
		}
		User user = new User(email);
		user.setEmail(email);
		user.setCredit(100);
		user.setHasBina(false);
		users.put(user.getEmail(),user);
		return user;
	}
	

	public int getCredit(String email) throws UserNotExistsException {
		if(!users.containsKey(email)) {
			throw new UserNotExistsException(email);
		}
		return users.get(email).getCredit();
	}

	public void setId(String wsName) {
		id = wsName;
	}

	public User getUser(String email) throws UserNotExistsException {
		if(!users.containsKey(email)) {
			throw new UserNotExistsException(email);
		}
		return users.get(email);
	}
	
	public synchronized Map<String, StationClient> getStationClients() throws UDDINamingException{
    	if(!stationClients.isEmpty()){
    		stationClients.clear();
        }
    	
        UDDINaming uddiNaming = new UDDINaming("http://a09:dAgMX5F@uddi.sd.rnl.tecnico.ulisboa.pt:9090/");

        Collection<UDDIRecord> records = uddiNaming.listRecords("A09_Station");
        StationClient stationClient = null;
        
        for(UDDIRecord record: records) {
    		try {
				stationClient = new StationClient(record.getUrl());
				stationClient.setVerbose(true);
				stationClients.put(record.getOrgName(), stationClient);
			} 
    		catch (StationClientException e) {
				new StationClientException("Binas has no client in the url" + record.getUrl());
    		}
        }
        
        return stationClients;
    }

	public Map<String, User> getUsers() {
		return users;
	}
	
}
