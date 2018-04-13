package org.binas.ws.it;

import java.util.List;

import org.binas.ws.BadInit_Exception;
import org.binas.ws.CoordinatesView;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.binas.ws.InvalidStation_Exception;
import org.binas.ws.StationView;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class ListStationsIT extends BaseIT{
	
	@BeforeClass
	public static void setUp() {
		try {
			client.testClear();
			client.testInitStation("A09_Station1", 9, 21, 50, 50);
			client.testInitStation("A09_Station2", 13, 19, 50, 50);
			client.testInitStation("A09_Station3", 0, 18, 50, 50);
			client.activateUser("alisyr1356h@gmail.com");
		} catch (BadInit_Exception | EmailExists_Exception | InvalidEmail_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
    public void rightOrder() {
		CoordinatesView view = new CoordinatesView();
		view.setX(12);
		view.setY(20);
		List<StationView> stations = client.listStations(3, view);
		Assert.assertEquals("A09_Station1", stations.get(0).getId());
		Assert.assertEquals("A09_Station2", stations.get(1).getId());
		Assert.assertEquals("A09_Station3", stations.get(2).getId());
	}
	
	
}
