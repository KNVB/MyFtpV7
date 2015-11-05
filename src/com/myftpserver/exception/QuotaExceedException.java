package com.myftpserver.exception;

public class QuotaExceedException extends Exception 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3487638359243265219L;
	public QuotaExceedException(String msg)
	{
		//550_Quota_Exceed
		super(msg);
	}
	
}
