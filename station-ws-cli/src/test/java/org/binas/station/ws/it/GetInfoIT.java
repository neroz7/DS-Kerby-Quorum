package org.binas.station.ws.it;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.StationView;
import org.junit.Test;

public class GetInfoIT extends BaseIT {

	
	@Test
	public void GetInfoNotNullTest() throws BadInit_Exception {
		for(int i = 0; i < 11; i++) {
			client.testInit(i, i, i*10, 1);
			assertNotNull(client.getInfo());
		}
	}
	
	@Test(expected = BadInit_Exception.class)
	public void GetInfoFailureCapacityTest() throws BadInit_Exception {
		for(int i = 0; i < 11; i++) {
			if(i % 8 == 0) {
				client.testInit(i, i, -i*10, 1);
				assertNotNull(client.getInfo());
			}else {
				client.testInit(i, i, i*10, 1);
				assertNotNull(client.getInfo());
			}
		}
	}
	
	@Test(expected = BadInit_Exception.class)
	public void GetInfoFailureXTest() throws BadInit_Exception {
		for(int i = 0; i < 11; i++) {
			if(i % 8 == 0) {
				client.testInit(-i, i, i*10, 1);
				assertNotNull(client.getInfo());
			}else {
				client.testInit(i, i, i*10, 1);
				assertNotNull(client.getInfo());
			}
		}
	}
	
	@Test(expected = BadInit_Exception.class)
	public void GetInfoFailureYTest() throws BadInit_Exception {
		for(int i = 0; i < 11; i++) {
			if(i % 8 == 0) {
				client.testInit(i, -i, i*10, 1);
				assertNotNull(client.getInfo());
			}else {
				client.testInit(i, i, i*10, 1);
				assertNotNull(client.getInfo());
			}
		}
	}
	@Test(expected = BadInit_Exception.class)
	public void GetInfoFailureTest() throws BadInit_Exception {
		for(int i = 0; i < 11; i++) {
			if(i % 2 == 0) {
				client.testInit(i, i, i*10, 1);
				assertNotNull(client.getInfo());
			}else {
				client.testInit(-i, -i, -i*10, 1);
				assertNotNull(client.getInfo());
			}
		}
	}
	
	
	@Test
	public void GetInfoCheckAtributesCorrectionTest() throws BadInit_Exception {
		for(int i = 0; i < 11; i++) {
			client.testInit(i, i, i*10, 1);
			StationView s = client.getInfo();
			assertNotNull(s);
			assertTrue(s.getCoordinate().getX() == i);
			assertTrue(s.getCoordinate().getY() == i);
			assertTrue(s.getCapacity() == i*10);
		}
	}
	
}
