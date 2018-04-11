package org.binas.domain;

public class User {
	protected String email;
    protected Boolean hasBina;
    protected Integer credit;

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
}
