package org.binas.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.binas.domain.exception.EmailExistsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.UserNotExistsException;
import org.binas.station.ws.UserReplic;
import org.binas.station.ws.cli.StationClient;
import org.binas.station.ws.cli.StationClientException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

public class BinasManager {

	// Singleton -------------------------------------------------------------
	private String id;
	Map<String,User>users = new HashMap<String,User>();
	public Map<String,StationClient>stationClients = new HashMap<String,StationClient>();

	private BinasManager() {
		try {
			stationClients = getStationClients();
		} catch (UDDINamingException e) {
			e.printStackTrace();
		}
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
		User user = new User(email,0,false);

		/*new implementation*/
		for(StationClient st : stationClients.values()) {	
			UserReplic userR = st.getBalance(email);
			if(userR == null) {
				UserReplic usr = new UserReplic();
				usr.setEmail(email);
				usr.setCredit(0);
				st.setBalance(email, usr);
			}
		}
		/*old implementation*/
		if(users.containsKey(email)) {
			throw new EmailExistsException();
		}
		users.put(user.getEmail(),user);
		return user;
	}
	

	public int getCredit(String email) throws UserNotExistsException {
		//if(!users.containsKey(email)) {
			//throw new UserNotExistsException(email);
		//}
		return getUser(email).getCredit();
	}

	public void setId(String wsName) {
		id = wsName;
	}

	public UserReplic getUserReplic(String email) throws UserNotExistsException {
		int N = stationClients.values().size();
		int n = 0;
		UserReplic user = null;
		Map<Integer,Integer>values_counts = new HashMap<Integer,Integer>();
		for(StationClient st : stationClients.values()) {	
			user = st.getBalance(email);
			if(!values_counts.containsKey(user.getCredit()))
				values_counts.put(user.getCredit(), 1);
			else
				values_counts.replace(user.getCredit(), values_counts.get(user.getCredit()));
		}
		for(Integer counter : values_counts.values()) {	
			if(counter > n) n = counter;
		}
		if (n >= N/2 + 1) {
			return user;
		}
		else {
			throw new UserNotExistsException(email);
		}
	}
	
	public User getUser(String email) throws UserNotExistsException {
		if(!users.containsKey(email)) {
			throw new UserNotExistsException(email);
		}
		return users.get(email);
	}
	

	public Map<String, User> getUsers() {
		return users;
	}
	
	public synchronized Map<String, StationClient> getStationClients() throws UDDINamingException {
		Map<String,StationClient>stationClients = new HashMap<String,StationClient>();
    	
    	UDDINaming uddiNaming = new UDDINaming("http://a09:dAgMX5F@uddi.sd.rnl.tecnico.ulisboa.pt:9090/");
		 
        Collection<UDDIRecord> records = uddiNaming.listRecords("A09_Station%");
 
        for(UDDIRecord record: records) {
    		try {
                StationClient stationClient = null;

				stationClient = new StationClient(record.getUrl());
				stationClient.setVerbose(true);
				stationClients.put(record.getOrgName(), stationClient);
				
			} 
    		catch (StationClientException e) {
    			System.out.println("Can not find Station "+ record.getOrgName()+" at "+record.getUrl());

    		}
        }

        return stationClients;
    }
	
}
