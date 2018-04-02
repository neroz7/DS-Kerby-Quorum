package org.binas.station.ws.it;

import static org.junit.Assert.*;

import org.binas.station.ws.NoBinaAvail_Exception;
import org.junit.Assert;
import org.junit.Test;

public class GetBinaIT extends BaseIT {
	
	@Test
	public void GetBinaTest() {
		Assert.assertTrue(true);
		try {
			client.getBina();
		} catch (NoBinaAvail_Exception e) {
			Assert.assertFalse(true);
		}
	}
	
}
