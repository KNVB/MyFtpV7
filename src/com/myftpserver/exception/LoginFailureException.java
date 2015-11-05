package com.myftpserver.exception;

public class LoginFailureException extends Exception 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4886964826372496090L;
	public LoginFailureException(String msg)
	{
		super(msg);
	}
}
