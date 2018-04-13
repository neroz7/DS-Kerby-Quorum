package org.binas.ws.it;


import org.binas.ws.BadInit_Exception;
import org.binas.ws.EmailExists_Exception;
import org.binas.ws.InvalidEmail_Exception;
import org.junit.Before;
import org.junit.Test;

public class ActivateUserIT extends BaseIT{
	
	private static final String EMAIL = "sss@tecnico.ulisboa.pt";
	
	@Before
	public void setUp() throws BadInit_Exception, EmailExists_Exception, InvalidEmail_Exception {
		client.testClear();
		client.activateUser(EMAIL);
		client.testInit(20);
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void emptyEmail() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("");
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void nullEmail() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser(null);
	}

	@Test(expected = InvalidEmail_Exception.class)
	public void wrongEmailFormatwithMoreThanOneAt() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("gds@vfg@ttt.er");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void noDomain() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser("66tya09@");
	}
	
	@Test(expected = InvalidEmail_Exception.class)
	public void wrongEmailFormatwithMoreThanOnePoint() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser(".fre.@.43.r");
	}
	
	
	
	@Test(expected = EmailExists_Exception.class)
	public void emailAlreadyExists() throws EmailExists_Exception, InvalidEmail_Exception {
		client.activateUser(EMAIL);
	}

	

}
