package com.myftpserver.exception;

public class LoginFailureException extends Exception 
{

	/**
	 * Checked exception thrown when a user login failure.
	 */
	private static final long serialVersionUID = -4886964826372496090L;
	public LoginFailureException(String msg)
	{
		super(msg);
	}
}
