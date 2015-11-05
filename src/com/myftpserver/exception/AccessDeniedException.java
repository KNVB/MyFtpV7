package com.myftpserver.exception;

public class AccessDeniedException extends Exception 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4886964826372496090L;
	public AccessDeniedException(String msg)
	{
		super(msg);
	}
}
