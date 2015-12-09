package com.myftpserver.exception;

public class AccessDeniedException extends Exception 
{

	/**
	 * Checked exception thrown when a file system operation is denied, typically due to a file permission or other access check.
	 */
	private static final long serialVersionUID = -4886964826372496090L;
	public AccessDeniedException(String msg)
	{
		super(msg);
	}
}
