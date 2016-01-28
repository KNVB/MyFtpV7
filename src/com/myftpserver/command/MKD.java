package com.myftpserver.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.ServerConfig;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
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
public class MKD implements FtpCommandInterface
{

	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs,String inPath) 
	{
		String serverPath=new String(),newPathName,message;
		Logger logger=fs.getLogger();
		ServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		
		logger.debug("inPath="+inPath+"|");
		try 
		{
			newPathName=inPath;
			if (newPathName.indexOf("/")==-1)
				newPathName=fs.getCurrentPath()+"/"+newPathName;
			serverPath=fm.putFile(fs,newPathName);
			logger.debug("serverPath="+serverPath+",newPathName="+newPathName);
			Files.createDirectories(Paths.get(serverPath));
			message=fs.getFtpMessage("257_MKD");
			message=message.replace("%1", inPath);
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),message);
		} 
		catch (InterruptedException|QuotaExceedException|AccessDeniedException err) 
		{
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),err.getMessage());
		}
		catch (PathNotFoundException|InvalidPathException|IOException err) 
		{
			message=fs.getFtpMessage("550_MKD_Failure");
			message=message.replace("%1", inPath);
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),message+":"+err.getMessage());
		}
	}
}
