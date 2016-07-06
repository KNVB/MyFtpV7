package com.myftpserver.command;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.abstracts.ServerConfiguration;
import com.myftpserver.abstracts.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.exception.NotAFileException;
import com.myftpserver.interfaces.FtpCommandInterface;
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
public class RNTO implements FtpCommandInterface {

	/**
	 *This command specifies the new pathname of the file specified in the immediately preceding "rename from" command.<br>
	 *For detail information about RNTO command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>
	 */
	public RNTO()
	{
		
	}

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void execute(FtpSessionHandler fs,String inPath) 
	{
		String newFileName;
		Logger logger=fs.getLogger();
		ServerConfiguration serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		logger.debug("inPath="+inPath+"|");
		newFileName=inPath;
		if (newFileName.indexOf("/")==-1)
			newFileName=fs.getCurrentPath()+"/"+newFileName;
		try
		{
			fm.renameTo(fs, newFileName);
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),fs.getFtpMessage("250_Rename_Ok"));
		}
		catch (AccessDeniedException|NotAFileException err) 
		{
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),err.getMessage());
		}
		
		catch (PathNotFoundException|InvalidPathException|IOException err) 
		{
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),fs.getFtpMessage("450_File_Rename_Fail")+":"+err.getMessage());
		} 
	}
	/*@Override
	public void execute(FtpSessionHandler fs,String inPath) 
	{
		Logger logger=fs.getLogger();
		ServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		Path renameFrom,renameTo;
		String destPath=new String(),fileName;
		logger.debug("inPath="+inPath+"|");
		try 
		{
			fileName=inPath;
			if (fileName.indexOf("/")==-1)
				fileName=fs.getCurrentPath()+"/"+fileName;
			destPath=fm.putFile(fs,fileName);
			renameFrom=Paths.get(fs.getReNameFrom());
			renameTo=Paths.get(destPath);
			Files.move(renameFrom, renameTo);
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),fs.getFtpMessage("250_Rename_Ok"));
		}
		catch (InterruptedException|AccessDeniedException|QuotaExceedException err) 
		{
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),err.getMessage());
		}
		
		catch (PathNotFoundException|InvalidPathException|IOException err) 
		{
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),fs.getFtpMessage("450_File_Rename_Fail")+":"+err.getMessage());
		}
	}*/

}
