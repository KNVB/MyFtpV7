package com.myftpserver.exception;

public class InvalidHomeDirectoryException extends Exception 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4886964826372496090L;
	public InvalidHomeDirectoryException(String msg)
	{
		super(msg);
	}
}
