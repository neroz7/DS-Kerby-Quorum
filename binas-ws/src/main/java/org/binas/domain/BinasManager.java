package org.binas.domain;

public class BinasManager {

	// Singleton -------------------------------------------------------------

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
	
	@Override
	public UserView activateUser(String email) throws EmailExists_Exception,InvalidEmail_Exception {
	
	}
	
	 @Override
	 public StationView getInfoStation(String stationId) throws InvalidStation_Exception {

	 }
	
	 @Override
	 public List<StationView> listStations(Integer numberOfStations,CoordinatesView coordinates) {

	 }
	
	 @Override
	 public void rentBina(String stationId, String email) throws AlreadyHasBina_Exception, InvalidStation_Exception,NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {

	 }
	
	 @Override
	 public void returnBina(String stationId, String email) throws FullStation_Exception, InvalidStation_Exception ,NoBinaRented_Exception, UserNotExists_Exception {
	 
	 }
	
	 @Override
	 public int getCredit(String email) throws UserNotExists_Exception {

	 }
    //test control operations ------------------------------------------------

	@Override
	public String testPing(String inputMessage) {
		
	}
		
	@Override
	public void testClear() {
		
	}
		
	@Override
	public void testInitStation(String stationId, int x, int y, int capacity, int returnPrize) throws BadInit_Exception {
		
	}
		
	@Override
	public void testInit(int userInitialPoints) throws BadInit_Exception {
		
	}
	
}
