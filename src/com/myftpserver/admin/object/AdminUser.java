package com.myftpserver.admin.object;

import java.io.Serializable;

public class AdminUser implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9001969874305526543L;
	String name,password;
	public AdminUser()
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
