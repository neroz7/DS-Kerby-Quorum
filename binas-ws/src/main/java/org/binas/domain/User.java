package org.binas.domain;

import org.binas.domain.exception.AlreadyHasBinaException;
import org.binas.domain.exception.BadInitException;
import org.binas.domain.exception.InvalidEmailException;
import org.binas.domain.exception.NoBinaRentedException;
import org.binas.domain.exception.NoCreditException;

public class User {
	protected String email;
	private String password;
    private Boolean hasBina;
    private Integer credit;
    
    public User(String email) throws InvalidEmailException {
    	checkemail(email);
    	this.email = email;
    	this.credit = 0;
    	this.hasBina = false;
    }
    
    public User(String email, Integer credit, Boolean hasBina) throws InvalidEmailException {
    	checkemail(email);
    	this.email = email;
    	this.credit = credit;
    	this.hasBina = hasBina;
    }

    private void checkemail(String email) throws InvalidEmailException {
    	if(email==null || email.trim().equals("") || email.charAt(email.length()-1)=='.') {
			throw new InvalidEmailException();
    	}
    	String[] parts = email.split("@");
		if(parts.length != 2) {
			throw new InvalidEmailException();
		}
		else {
			int atIndex = email.indexOf('@');
			if(email.charAt(atIndex-1) == '.' || email.charAt(atIndex+1) == '.')
				throw new InvalidEmailException();
			int i;
			for(i = 1 ; i < email.length(); i++)
				if(email.charAt(i-1) == '.' && email.charAt(i) == '.')
					throw new InvalidEmailException();	
		}
	}

	public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public Boolean isHasBina() {
        return hasBina;
    }

    public void setHasBina(Boolean value) {
        this.hasBina = value;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer value) {
        this.credit = value;
    }
    
    public synchronized void getBina() throws AlreadyHasBinaException, NoCreditException {
		if(this.isHasBina())
			throw new AlreadyHasBinaException();
		else {
			setHasBina(true);
			this.credit++;
		}
			
	}
	
	public synchronized void returnBina(int stationBonus) throws NoBinaRentedException{
		if(!this.isHasBina())
			throw new NoBinaRentedException();
		this.credit += stationBonus;
	}

	public synchronized void testInit(int userInitialPoints) throws BadInitException {
		if(userInitialPoints<0)
			throw new BadInitException();
		this.credit = userInitialPoints;
		
	}
}
