package org.binas.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.binas.ws.UserView;

public class BinasManager {

	// Singleton -------------------------------------------------------------
	private String id;
	Map<String,User>users = new HashMap<String,User>();
	
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
	public User activateUser(String email) {
		User user = new User();
		user.setEmail(email);
		user.setCredit(100);
		user.setHasBina(false);
		users.put(user.getEmail(),user);
		return user;
	}
	

	public int getCredit(String email) throws UserNotExists_Exception {
		return users.get(email).getCredit();
	}

	public void setId(String wsName) {
		id = wsName;
	}

	public User getUser(String email) throws UserNotExists_Exception {
		if(!users.containsKey(email)) {
			throw new UserNotExists_Exception(email, null);
		}
		return users.get(email);
	}
	
}
