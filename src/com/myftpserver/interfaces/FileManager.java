package com.myftpserver.interfaces;

import org.apache.logging.log4j.Logger;

import com.myftpserver.exception.*;
import com.myftpserver.Configuration;
import com.myftpserver.handler.FtpSessionHandler;
/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 * @author SITO3
 *
 */
public abstract class FileManager 
{
	/**
	 * Access ok error code
	 */
	public static final int ACCESS_OK=0;
	/**
	 * Excess quota error code
	 */
	public static final int EXCESS_QUOTA=1;
	/**
	 * Access denied error code
	 */
	public static final int ACCESS_DENIED=2;
	public static final int MULTI_HOME_DIR=3;
	/**
	 * Invalid home directory error code
	 */
	public static final int INVALID_HOME_DIR=4;
	/**
	 * Home directory not found error code
	 */
	public static final int HOME_DIR_NOT_FOUND=5;
	/**
	 * Invalid home directory permission error code
	 */
	public static final int INVALID_HOME_DIR_PERM=6;
	/**
	 * Directory not found error code
	 */
	public static final int PATH_NOT_FOUND=7;
	/**
	 * No access permission
	 */
	public static final String NO_ACCESS="x";
	/**
	 * Read only permission
	 */
	public static final String READ_PERMISSION="r";
	/**
	 * List directory permission
	 */
	public static final String LIST_PERMISSION="l";
	/**
	 * Write permission
	 */
	public static final String WRITE_PERMISSION="w";
	/**
	 * Write Tree permission
	 */
	public static final String TREE_WRITE_PERMISSION="t";
	/**
	 * Message logger
	 */
	public Logger logger;
	/**
	 * Configuration object
	 */
	public Configuration config;
	/**
	 * File Manager interface
	 * @param c Configuration object
	 */
	public FileManager(Configuration c)
	{
		this.config=c;
		this.logger=config.getLogger();
	}
	/**
	 * Get file/path size
	 * @param fs FtpSessionHandler
	 * @param clientPath virtual path
	 * @return file/path size
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 */
	public abstract long getPathSize(FtpSessionHandler fs, String clientPath)throws AccessDeniedException, PathNotFoundException;
	/**
	 * Change current directory
	 * @param fs FtpSessionHandler
	 * @param inPath Destination directory
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 */
	public abstract void changeDirectory(FtpSessionHandler fs,String inPath) throws AccessDeniedException, PathNotFoundException;
	/**
	 * Get server path for virtual root path
	 * @param fs FtpSessionHandler
	 * @throws AccessDeniedException
	 * @throws InvalidHomeDirectoryException
	 */
	public abstract void getRealHomePath(FtpSessionHandler fs) throws AccessDeniedException,InvalidHomeDirectoryException;
	/**
	 * Get server path for specified virtual root path 
	 * @param fs FtpSessionHandler
	 * @param inPath virtual path 
	 * @param requiredPermission Permission require for the next operation
	 * @return Server path
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 */
	public abstract String getServerPath(FtpSessionHandler fs, String inPath,String requiredPermission) throws AccessDeniedException,PathNotFoundException;
	/**
	 * Generate a directory full listing for a virtual path
	 * @param fs FtpSessionHandler
	 * @param inPath virtual path
	 * @return a directory full listing in a StringBuffer object
	 * @throws AccessDeniedException
	 * @throws InterruptedException
 	 * @throws NotADirectoryException
	 * @throws PathNotFoundException
	 */
	public abstract StringBuffer getFullDirList(FtpSessionHandler fs, String inPath) throws AccessDeniedException,NotADirectoryException,PathNotFoundException, InterruptedException;
	/**
	 * Generate a directory listing for a virtual path (contains file name only)
	 * @param fs FtpSessionHandler
	 * @param inPath virtual path
	 * @return a directory listing in a StringBuffer object (contains file name only)
	 * @throws AccessDeniedException
	 * @throws InterruptedException
	 * @throws NotADirectoryException
	 * @throws PathNotFoundException
	 */
	public abstract StringBuffer getFileNameList(FtpSessionHandler fs, String inPath) throws AccessDeniedException, NotADirectoryException, PathNotFoundException, InterruptedException;
	/**
	 * Get server path for specified virtual path for sending file to client 
	 * @param fs FtpSessionHandler
	 * @param inPath virtual path 
	 * @return server path
	 * @throws AccessDeniedException
	 * @throws InterruptedException
	 * @throws NotAFileException
	 * @throws PathNotFoundException
	 */
	public abstract String getFile(FtpSessionHandler fs, String inPath) throws AccessDeniedException,NotAFileException,PathNotFoundException,InterruptedException;
	/**
	 * Generate a server path for file upload 
	 * @param fs FtpSessionHandler
	 * @param inPath a virtual path that the file to be uploaded 
	 * @return server path
	 * @throws AccessDeniedException
	 * @throws InterruptedException
	 * @throws PathNotFoundException
	 * @throws QuotaExceedException
	 */
	public abstract String putFile(FtpSessionHandler fs, String inPath)	throws AccessDeniedException, PathNotFoundException,InterruptedException, QuotaExceedException;
	public abstract void close();
}
