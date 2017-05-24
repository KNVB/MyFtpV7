package com.myftpserver.admin.object;

public class AdminUser
{
	/**
	 * 
	 */
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
