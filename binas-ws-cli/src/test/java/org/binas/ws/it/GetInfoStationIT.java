package org.binas.ws.it;

import java.util.List;

import org.binas.domain.exception.InvalidStationException;
import org.binas.domain.exception.UserNotExistsException;
import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.FullStation_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.NoBinaAvail_Exception;
import org.binas.ws.NoBinaRented_Exception;
import org.binas.ws.NoCredit_Exception;
import org.binas.ws.StationView;
import org.binas.ws.UserNotExists_Exception;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GetInfoStationIT extends BaseIT{
	
	@Before
	public void setUp() throws InvalidStation_Exception, BadInit_Exception, 
		EmailExists_Exception, InvalidEmail_Exception {
		
		client.testClear();
		client.testInitStation("A09_Station1", 20, 30, 30, 30);
		client.activateUser("ist427068@tecnico.ulisboa.pt");
		client.activateUser("alisyr1356h@gmail.com");
		client.activateUser("pedromela@ist.utl.pt");
		client.activateUser("fred@gmail.com");
	}

	@Test
    public void success()  {
		
		try {
			Assert.assertEquals(0, client.getCredit("ist427068@tecnico.ulisboa.pt"));
			client.testInit(10);
			Assert.assertEquals(10, client.getCredit("alisyr1356h@gmail.com"));

			client.rentBina("A09_Station1", "alisyr1356h@gmail.com");
			client.rentBina("A09_Station1", "ist427068@tecnico.ulisboa.pt");
			client.rentBina("A09_Station1", "pedromela@ist.utl.pt");
			client.rentBina("A09_Station1", "fred@gmail.com");
	
			StationView stationView = client.getInfoStation("A09_Station1");
			Assert.assertEquals(26, stationView.getAvailableBinas());

			client.returnBina("A09_Station1", "alisyr1356h@gmail.com");
			client.returnBina("A09_Station1", "ist427068@tecnico.ulisboa.pt");
			client.returnBina("A09_Station1", "pedromela@ist.utl.pt");
			client.returnBina("A09_Station1", "fred@gmail.com");
	
			stationView = client.getInfoStation("A09_Station1");

			Assert.assertEquals(50, client.getCredit("alisyr1356h@gmail.com"));
			Assert.assertEquals(4, stationView.getTotalGets());
			Assert.assertEquals(4, stationView.getTotalReturns());
			Assert.assertEquals(30, stationView.getAvailableBinas());
			} catch (UserNotExists_Exception e) {
				e.printStackTrace();
			} catch (BadInit_Exception e) {
				e.printStackTrace();
			} catch (AlreadyHasBina_Exception e) {
				e.printStackTrace();
			} catch (InvalidStation_Exception e) {
				e.printStackTrace();
			} catch (NoBinaAvail_Exception e) {
				e.printStackTrace();
			} catch (NoCredit_Exception e) {
				e.printStackTrace();
			} catch (FullStation_Exception e) {
				e.printStackTrace();
			} catch (NoBinaRented_Exception e) {
				e.printStackTrace();
			}
    }
	@Test(expected=InvalidStationException.class)
    public void notExistingStation() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.rentBina("A09_Station4", "alisyr1356h@gmail.com");
	}
	@Test(expected=UserNotExistsException.class)
    public void notExistingUser() throws AlreadyHasBina_Exception, InvalidStation_Exception, NoBinaAvail_Exception, NoCredit_Exception, UserNotExists_Exception {
		client.rentBina("A09_Station1", "alisyr1356h@gmail.com");
	}
	
}
