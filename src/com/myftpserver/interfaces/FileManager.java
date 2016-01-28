package com.myftpserver.interfaces;
import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.myftpserver.exception.*;
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
	 * No access permission
	 */
	public static final String NO_ACCESS="-";
	/**
	 * Read only permission
	 */
	public static final String READ_PERMISSION="r";
	/**
	 * Write permission
	 */
	public static final String WRITE_PERMISSION="w";
	/**
	 * Execute permission
	 */
	public static final String EXECUTE_PERMISSION="x";
	/**
	 * Message logger
	 */
	protected Logger logger;
	/**
	 * File Manager interface
	 * @param logger Message logger
	 */
	public FileManager(Logger logger)
	{
		this.logger=logger;
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
	 * Get server path for virtual root path
	 * @param fs FtpSessionHandler
	 * @throws AccessDeniedException
	 * @throws InvalidHomeDirectoryException
	 */
	public abstract void getRealHomePath(FtpSessionHandler fs) throws AccessDeniedException,InvalidHomeDirectoryException;

	/**
	 * Change current directory
	 * @param fs FtpSessionHandler
	 * @param inPath Destination directory
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 */
	public abstract void changeDirectory(FtpSessionHandler fs,String inPath) throws AccessDeniedException, PathNotFoundException;
	/**
	 * Create a virtual directory 
	 * @param fs  FtpSessionHandler
	 * @param inPath a virtual path that to be created
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 */
	public abstract void createDirectory(FtpSessionHandler fs, String inPath)	throws AccessDeniedException, PathNotFoundException,IOException;
	/**
	 * Delete a virtual directory 
	 * @param fs FtpSessionHandler
	 * @param inPath a virtual path that to be deleted
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 * @throws IOException
	 */
	public abstract void deleteDirectory(FtpSessionHandler fs, String inPath)	throws AccessDeniedException, PathNotFoundException,IOException;
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
	 * Download a file
	 * @param fs FtpSessionHandler
	 * @param inPath a virtual path of a file that to be downloaded
	 * @throws AccessDeniedException
	 * @throws NotAFileException
	 * @throws PathNotFoundException
	 * @throws InterruptedException
	 */
	public abstract void downloadFile(FtpSessionHandler fs, String inPath) throws AccessDeniedException, NotAFileException, PathNotFoundException, InterruptedException;
	/**
	 * Upload a file
	 * @param fs
	 * @param inPath a virtual path of a file that to be resided
	 * @throws AccessDeniedException
	 * @throws NotAFileException
	 * @throws PathNotFoundException
	 * @throws InterruptedException
	 * @throws QuotaExceedException
	 */
	public abstract void uploadFile(FtpSessionHandler fs, String inPath) throws AccessDeniedException, NotAFileException, PathNotFoundException, InterruptedException,QuotaExceedException;
	/**
	 * Delete a file
	 * @param fs FtpSessionHandler
	 * @param inPath a virtual path of a file that to be deleted
	 * @throws AccessDeniedException
	 * @throws NotAFileException
	 * @throws PathNotFoundException
	 * @throws IOException
	 */
	public abstract void deleteFile(FtpSessionHandler fs, String inPath) throws AccessDeniedException, NotAFileException, PathNotFoundException, IOException;
	/**
	 * Rename a file
	 * @param fs FtpSessionHandler
	 * @param oldFileName old virtual path name
	 * @param newFileName new virtual path name
	 * @throws AccessDeniedException
	 * @throws NotAFileException
	 * @throws PathNotFoundException
	 * @throws IOException
	 */
	public abstract void renameFile(FtpSessionHandler fs, String oldFileName,String newFileName) throws AccessDeniedException, NotAFileException, PathNotFoundException, IOException;

	public abstract void close();	
}
