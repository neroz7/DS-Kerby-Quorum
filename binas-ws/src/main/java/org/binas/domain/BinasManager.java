package org.binas.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.binas.domain.exception.AlreadyHasBinaException;
import org.binas.domain.exception.BadInitException;
import org.binas.domain.exception.EmailExistsException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.NoCreditException;
import org.binas.domain.exception.UserNotExistsException;
import org.binas.station.ws.GetBalanceResponse;
import org.binas.station.ws.SetBalanceResponse;
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
	private int N;
	private int cycle = 100; //miliseconds
	private int timeout = 5000;//miliseconds
	
	private Map<Integer , Collection<UserReplic>> QuorumReads = new ConcurrentHashMap<Integer, Collection<UserReplic>>();
    private Map<Integer , Integer> QuorumWrites = new ConcurrentHashMap<Integer, Integer>();

    Map<String,User>users = new HashMap<String,User>();
	public Map<String,StationClient>stationClients = new HashMap<String,StationClient>();

	private int response_number = 0;
	private int request_number = 0;

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
			N = stationClients.values().size();

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
		/*Quorum init sincrono*/
		for(StationClient st : stationClients.values()) {	
			try{
				UserReplic userR = st.getBalance(email,0);
				if(userR.getEmail() == null) {
					UserReplic usr = new UserReplic();
					usr.setEmail(email);
					usr.setCredit(0);
					usr.setSeq(0);
					usr.setCid(cid);
					st.setBalance(email, usr , 0);
				}
			} catch(Exception e) {}
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
	
	private void WaitQuorumWrite() {
		//wait q acks
		int time = 0;

	    while (QuorumWrites.get(response_number).intValue() < N/2+1) {
		//while(finished < N/2) {
			try {
				Thread.sleep(cycle);
				time += cycle;
			} catch (InterruptedException e) {
                System.out.println("Caught interrupted exception.");
                System.out.print("Cause: ");
                System.out.println(e.getCause());
            }		
	    	
			//System.out.println("Could do something useful"); // do something usefull while waiting...
			System.out.flush();
	    	if (time >= timeout) {
	    		System.out.println("Timeout : Cant read Quorum... Could not achieve consensus!");
	    		break;
	    	}	    
	    }	
		QuorumWrites.remove(response_number);

	}
	private void WaitQuorumRead() {
		//wait q acks
		int time = 0;
	    while (QuorumReads.get(request_number).size() < N/2 + 1) {
		//while(finished < N/2) {
			try {
				Thread.sleep(cycle);
				time += cycle;
			} catch (InterruptedException e) {
                System.out.println("Caught interrupted exception.");
                System.out.print("Cause: ");
                System.out.println(e.getCause());
            }		
	    	//System.out.println("Could do something useful here!"); // do something usefull while waiting...
	    	System.out.flush();
	    	if (time >= timeout) {
	    		System.out.println("Timeout : Cant read Quorum... Could not achieve consensus!");
	    		break;
	    	}
	    }	
	}
	
	private UserReplic ReadQuorum() {
		UserReplic max = new UserReplic();
		max.setCid(0);
		max.setSeq(0);
		max.setEmail(null);
		//wait q acks
        //System.out.println("Waiting q responses");

		WaitQuorumRead();
		Collection<UserReplic> quorum;
		quorum = QuorumReads.get(request_number);
		
		for(UserReplic aux : quorum) {
			if(aux != null)
			if(aux.getEmail() != null) {
		
				//System.out.println("QuorumUser " + aux.getEmail()+ "Credit "+ aux.getCredit());

				if(aux.getSeq() > max.getSeq() || (aux.getSeq() == max.getSeq() && aux.getCid() > max.getCid()))
					max = aux;
			}
		}	
		
		QuorumReads.remove(request_number);
		//reset Quorum
		System.out.println("Quorum Read : User " + max.getEmail()+ " found!");
		
		return max;
		
	}

	/*Gets the user with the max tag*/
	public UserReplic getUserReplic(String email) {
		/*Quorum Read */
		request_number++;
		QuorumReads.put(request_number, new ArrayList<UserReplic>());

		System.out.println("Trying to find user...");
		for(StationClient st : stationClients.values()) {	
			// asynchronous call with callback
			AsyncHandler<GetBalanceResponse> handler = getBalanceAsyncHandler();
			st.getBalanceAsync(email, request_number, handler);

		}
		return ReadQuorum();
		
	}
	
    public void incCredit(String email) {
		/*Quorum Write*/
		response_number++;
		QuorumWrites.put(response_number, 0);

    	UserReplic maxTagUser = getUserReplic(email);
		maxTagUser.setSeq(maxTagUser.getSeq()+1);
		maxTagUser.setCid(cid);
		maxTagUser.setCredit(maxTagUser.getCredit()+1);
		
		for(StationClient st : stationClients.values()) {	
			st.setBalanceAsync(email, maxTagUser, response_number, setBalanceAsyncHandler());
		}
		//wait q acks
		WaitQuorumWrite();		
    }
	
	public void returnBina(String email, int stationBonus) throws UserNotExistsException {
		/*Quorum Write*/
		response_number++;
		QuorumWrites.put(response_number, 0);
		
		UserReplic maxTagUser = getUserReplic(email);
		maxTagUser.setSeq(maxTagUser.getSeq()+1);
		maxTagUser.setCid(cid);
		maxTagUser.setCredit(maxTagUser.getCredit()+stationBonus);
		
		for(StationClient st : stationClients.values()) {	
			//st.setBalance(email, maxTagUser);
			st.setBalanceAsync(email, maxTagUser, response_number, setBalanceAsyncHandler());
		}
		
		//wait q acks
		WaitQuorumWrite();
		
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
        
        N = stationClients.values().size();
        
        return stationClients;
    }
	
	private AsyncHandler<GetBalanceResponse> getBalanceAsyncHandler() {
		AsyncHandler<GetBalanceResponse> handler = new AsyncHandler<GetBalanceResponse>() {


			@Override
	        public void handleResponse(Response<GetBalanceResponse> response) {
	            try {
	            	UserReplic u = response.get().getUser();
	            	if(u.getRequestId() >=request_number){
	            	if(u != null && u.getEmail() != null) {
	            		if(QuorumReads.containsKey(u.getRequestId())){
	            			QuorumReads.get(u.getRequestId()).add(u);
	            		}else{
	            			Collection<UserReplic> aux = new ArrayList<UserReplic>();
	            			aux.add(u);
	            			QuorumReads.put(u.getRequestId(), aux);
	            		}
	            		if(u.getRequestId() != request_number){
			                System.out.println(u.getEmail()+"AsyncHandler : has diferent request id was " + u.getRequestId()+" and should be "+request_number);	            			
	            		}	     
	            	}	
	            	else
		                System.out.print("AsyncHandler : Received Async NULL");
	            	}
	            		
	            } catch (InterruptedException e) {
	                System.out.println("Caught interrupted exception.");
	                System.out.print("Cause: ");
	                System.out.println(e.getCause());
	            } catch (ExecutionException e) {
	                System.out.println("Caught execution exception.");
	                System.out.print("Cause: ");
	                System.out.println(e.getCause());
	            }
	        }
	    }; 
		return handler;
	}
	private AsyncHandler<SetBalanceResponse> setBalanceAsyncHandler() {
		AsyncHandler<SetBalanceResponse> handler = new AsyncHandler<SetBalanceResponse>() {


			@Override
	        public void handleResponse(Response<SetBalanceResponse> response) {
	            try {
	                int id = response.get().getId();
	                if(id >= response_number) {
            		if(QuorumWrites.containsKey(id)){
            			QuorumWrites.replace(id, QuorumWrites.get(id)+1);
            		}else{
            			QuorumWrites.put(id, 0);
            		}
            		if(id != response_number)
		                System.out.println("AsyncHandler : has diferent request id was " + id+" and should be "+request_number);	            			
	                }
	            } catch (InterruptedException e) {
	                System.out.println("Caught interrupted exception.");
	                System.out.print("Cause: ");
	                System.out.println(e.getCause());
	            } catch (ExecutionException e) {
	                System.out.println("Caught execution exception.");
	                System.out.print("Cause: ");
	                System.out.println(e.getCause());
	            }
	        }
	    }; 
		return handler;
	}
}
