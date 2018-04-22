package org.binas.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.binas.domain.exception.AlreadyHasBinaException;
import org.binas.domain.exception.BadInitException;
import org.binas.domain.exception.EmailExistsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.NoCreditException;
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
	private int cid;
	Map<String,User>users = new HashMap<String,User>();
	public Map<String,StationClient>stationClients = new HashMap<String,StationClient>();

	private BinasManager() {
		try {
			stationClients = getStationClients();
		} catch (UDDINamingException e) {
			e.printStackTrace();
		}
	}
	private BinasManager(int _cid) {
		cid = _cid;
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
	private static int Counter = 0;
	
	private static class SingletonHolder {
		private static final BinasManager INSTANCE = new BinasManager(++Counter);
	}

	public static synchronized BinasManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	public void setId(String wsName) {
		id = wsName;
	}
	public void setCid(int _cid) {
		cid = _cid;
	}
	public synchronized void clearUsers() {
		users.clear();
	}
	public User activateUser(String email) throws InvalidEmailException, EmailExistsException {
		User user = new User(email,0,false);
		/*old implementation*/
		if(users.containsKey(email)) {
			throw new EmailExistsException();
		}
		users.put(user.getEmail(),user);
		
		/*new implementation*/
		/*Quorum init*/
		for(StationClient st : stationClients.values()) {	
			UserReplic userR = st.getBalance(email);
			if(userR.getEmail() == null) {
				UserReplic usr = new UserReplic();
				usr.setEmail(email);
				usr.setCredit(0);
				usr.setSeq(0);
				usr.setCid(cid);
				st.setBalance(email, usr);
			}
		}
		
		return user;
	}
	

	public int getCredit(String email) throws UserNotExistsException {
		return getUser(email).getCredit();
	}
	public int getCreditReplic(String email) {
		return getUserReplic(email).getCredit();
	}
	
	public User getUser(String email) throws UserNotExistsException {
		if(!users.containsKey(email)) {
			throw new UserNotExistsException(email);
		}
		return users.get(email);
	}
	/*Same as getMaxTagUser but filter by email*/
	public UserReplic getUserReplic(String email) {
		/*Quorum Read */
		UserReplic max = new UserReplic(), aux;
		max.setCid(0);
		max.setSeq(0);
		for(StationClient st : stationClients.values()) {	
			aux = st.getMaxTagUserByName(email);
			if(aux.getEmail() != null)
				if(aux.getSeq() > max.getSeq() || (aux.getSeq() == max.getSeq() && aux.getCid() > max.getCid()))
					max = aux;
			}
		
		//wait q acks
		return max;
	}
	

	public Map<String, User> getUsers() {
		return users;
	}
	
	public void testInit(int userInitialPoints) throws BadInitException {
		if(userInitialPoints<0)
			throw new BadInitException();
		for(StationClient st : stationClients.values()) {	
			st.testInitUsers(userInitialPoints);
		}
		
	}
	
	private UserReplic getMaxTagUser() {
		/*Quorum Read possibly*/

		UserReplic max = new UserReplic(), aux;
		max.setCid(0);
		max.setSeq(0);
		for(StationClient st : stationClients.values()) {	
			aux = st.getMaxTagUser();
			if(aux.getEmail() != null)
				if(aux.getSeq() > max.getSeq() || (aux.getSeq() == max.getSeq() && aux.getCid() > max.getCid()))
					max = aux;
		}
		return max;
	}
    public void incCredit(String email) {
		/*Quorum Write*/
		int N = stationClients.values().size();
		UserReplic maxTagUser = getUserReplic(email);
		maxTagUser.setSeq(maxTagUser.getSeq()+1);
		maxTagUser.setCid(cid);
		maxTagUser.setCredit(maxTagUser.getCredit()+1);
		
		for(StationClient st : stationClients.values()) {	
			st.setBalance(email, maxTagUser);
		}
		//wait q acks
			
	}
	
	public void returnBina(String email, int stationBonus) throws UserNotExistsException {
		/*Quorum Write*/
		
		int N = stationClients.values().size();
		UserReplic maxTagUser = getUserReplic(email);
		maxTagUser.setSeq(maxTagUser.getSeq()+1);
		maxTagUser.setCid(cid);
		maxTagUser.setCredit(maxTagUser.getCredit()+stationBonus);
		
		for(StationClient st : stationClients.values()) {	
			st.setBalance(email, maxTagUser);
		}
		//wait q acks
		
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
