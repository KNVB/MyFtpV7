package com;

import java.io.Serializable;

public class User implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9001969874305526543L;
	String name,password;
	public User()
	{
		name="AA";
		password="pwd";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
