package com.myftpserver.admin.object;

import java.io.Serializable;

public class AdminUser implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9001969874305526543L;
	String name,password,action;
	
	public AdminUser(String name,String password,String action)
	{
		this.name=name;
		this.password = password;
		this.action = action;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
