package com.myftpserver.admin;


public class AdminObject 
{
	/**
	 * 
	 */
	private int adminFunctionCode=-1;
	private Object adminObject=null;
	public int getAdminFunctionCode() {
		return adminFunctionCode;
	}
	public void setAdminFunctionCode(int adminFunctionCode) {
		this.adminFunctionCode = adminFunctionCode;
	}
	public Object getAdminObject() {
		return adminObject;
	}
	public void setAdminObject(Object adminObject) {
		this.adminObject = adminObject;
	}
}
