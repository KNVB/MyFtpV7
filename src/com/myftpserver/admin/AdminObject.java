package com.myftpserver.admin;

import java.io.Serializable;

public class AdminObject implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
