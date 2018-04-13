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
			client.testInitStation("A09_Station1", 4, 5, 30, 30);
			client.testInitStation("A09_Station2", 10, 11, 30, 30);
			client.testInitStation("A09_Station3", 23, 28, 30, 30);
			client.activateUser("alisyr1356h@gmail.com");
		} catch (BadInit_Exception | EmailExists_Exception | InvalidEmail_Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
    public void rightOrder() {
		CoordinatesView crd = new CoordinatesView();
		crd.setX(1);
		crd.setY(2);
		List<StationView> stations = client.listStations(3, crd);
		Assert.assertEquals("A09_Station1", stations.get(0).getId());
		Assert.assertEquals("A09_Station2", stations.get(1).getId());
		Assert.assertEquals("A09_Station3", stations.get(2).getId());
	}
	
	
}
