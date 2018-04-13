package org.binas.ws.it;

import org.binas.ws.AlreadyHasBina_Exception;
import org.binas.ws.BadInit_Exception;
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
    public void success() throws EmailExists_Exception, 
    InvalidEmail_Exception, UserNotExists_Exception,
    InvalidStation_Exception, BadInit_Exception, 
    AlreadyHasBina_Exception, NoBinaAvail_Exception,
    NoCredit_Exception, FullStation_Exception, NoBinaRented_Exception {
		
	
		Assert.assertEquals(0, client.getCredit("ist427068@tecnico.ulisboa.pt"));
		client.testInit(10);

		Assert.assertEquals(10, client.getCredit("alisyr1356h@gmail.com"));
	
	
		client.rentBina("A44_Station1", "alisyr1356h@gmail.com");
		client.rentBina("A44_Station1", "ist427068@tecnico.ulisboa.pt");
		client.rentBina("A44_Station1", "pedromela@ist.utl.pt");
		client.rentBina("A44_Station1", "fred@gmail.com");
	
		StationView stationView = client.getInfoStation("A44_Station1");
		Assert.assertEquals(45, stationView.getAvailableBinas());

		client.returnBina("A44_Station1", "alisyr1356h@gmail.com");
		client.returnBina("A44_Station1", "ist427068@tecnico.ulisboa.pt");
		client.returnBina("A44_Station1", "pedromela@ist.utl.pt");
		client.returnBina("A44_Station1", "fred@gmail.com");
	
		stationView = client.getInfoStation("A44_Station1");
		Assert.assertNotNull(stationView);

		Assert.assertEquals(50, client.getCredit("alisyr1356h@gmail.com"));
		Assert.assertEquals(4, stationView.getTotalGets());
		Assert.assertEquals(4, stationView.getTotalReturns());
		Assert.assertEquals(30, stationView.getAvailableBinas());
		
    }
}
