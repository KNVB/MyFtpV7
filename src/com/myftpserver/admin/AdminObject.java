package com.myftpserver.admin;


public class AdminObject 
{
	/**
	 * 
	 */
	private int adminFunctionCode=-1;
	private String jsonString=null;
	public int getAdminFunctionCode() {
		return adminFunctionCode;
	}
	public void setAdminFunctionCode(int adminFunctionCode) {
		this.adminFunctionCode = adminFunctionCode;
	}
	public String getJsonString() {
		return jsonString;
	}
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
}
