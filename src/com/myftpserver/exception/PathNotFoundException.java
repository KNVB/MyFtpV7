package com.myftpserver.exception;

public class PathNotFoundException extends Exception 
{

	/**
	 *Checked exception thrown when a path does not exists in file system 
	 */
	private static final long serialVersionUID = -4886964826372496090L;
	public PathNotFoundException(String msg)
	{
		super(msg);
	}
}
