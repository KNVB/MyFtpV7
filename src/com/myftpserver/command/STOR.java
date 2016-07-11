package com.myftpserver.command;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.abstracts.ServerConfig;
import com.myftpserver.abstracts.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.NotAFileException;
import com.myftpserver.exception.QuotaExceedException;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;
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
public class STOR implements FtpCommandInterface
{
	/**
	 *This command causes the server-DTP to accept the data transferred via the data connection and to store the data as a file at the server site.<br>
	 *For detail information about STOR command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>
	 */
	public STOR()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void execute(FtpSessionHandler fs, String param) 
	{
		String fileName;
		Logger logger=fs.getLogger();
		ServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		logger.debug("param="+param+"|");
		try 
		{
			fileName=param;
			if (fileName.indexOf("/")==-1)
				fileName=fs.getCurrentPath()+"/"+fileName;
			fs.setUploadFile(fm.getUploadFileObject(fs, fileName));
			Utility.receiveFileFromClient(fs);
		}
		catch (NotAFileException | IOException|InterruptedException|QuotaExceedException|AccessDeniedException err) 
		{
			Utility.handleTransferException(fs,err.getMessage());
		}
		
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.handleTransferException(fs,fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
	}
	/*@Override
	public void execute(FtpSessionHandler fs, String param) 
	{
		Logger logger=fs.getLogger();
		String serverPath=new String(),fileName;
		ServerConfig serverConfig=fs.getServerConfig();
		
		FileManager fm=serverConfig.getFileManager();
		logger.debug("param="+param+"|");
		try 
		{
			fileName=param;
			if (fileName.indexOf("/")==-1)
				fileName=fs.getCurrentPath()+"/"+fileName;
			serverPath=fm.putFile(fs,fileName);
			fs.setUploadFileName(serverPath);
			logger.debug("serverPath="+serverPath+",fileName="+fileName);
			Utility.receiveFileFromClient(fs,serverPath);
		} 
		catch (InterruptedException|QuotaExceedException|AccessDeniedException err) 
		{
			Utility.handleTransferException(fs,err.getMessage());
		}
		
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.handleTransferException(fs,fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
	}*/
}
