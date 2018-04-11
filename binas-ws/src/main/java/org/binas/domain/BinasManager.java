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
	
	public UserView activateUser(String email) {
		User user = new User();
		user.setEmail(email);
		user.setCredit(0);
		user.setHasBina(false);
		users.put(user.getEmail(),user);
		return buildUserView(user);
	}
	
	private UserView buildUserView(User user) {
		UserView userView = new UserView();
		userView.setCredit(user.getCredit());
		userView.setEmail(user.getEmail());
		userView.setHasBina(user.hasBina);
		return userView;
	}

	public int getCredit(String email) throws UserNotExists_Exception {
		return users.get(email).getCredit();
	}
	
}
