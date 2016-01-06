package com.myftpserver.command;

import java.io.File;
import java.nio.file.InvalidPathException;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.ServerConfig;
import com.myftpserver.impl.FileUtil;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;
import com.myftpserver.exception.NotADirectoryException;

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
public class RMD implements FtpCommandInterface
{

	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String inPath) 
	{
		boolean result;
		File serverFolder;
		Logger logger=fs.getLogger();
		String serverPath=new String(),newPathName,message;
		ServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		logger.debug("inPath="+inPath+"|");
		try 
		{
			newPathName=inPath;
			if (newPathName.indexOf("/")==-1)
				newPathName=fs.getCurrentPath()+"/"+newPathName;
			serverPath=fm.deleteDirectory(fs,newPathName);
			logger.debug("serverPath="+serverPath+",newPathName="+newPathName);
			serverFolder=new File(serverPath);
			if (serverFolder.isDirectory())
			{	
				result=FileUtil.deleteDirectory(serverFolder);
				if (result)
				{
					message=fs.getFtpMessage("250_RMD");
					message=message.replaceAll("%1", inPath);
					Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),message);
				}
				else
					throw new PathNotFoundException("");
			}
			else
			{	
				message=fs.getFtpMessage("550_Not_A_Directory");
				message=message.replaceAll("%1", inPath);
				throw new NotADirectoryException(message);
			}
		} 
		catch (AccessDeniedException|NotADirectoryException|PathNotFoundException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}
		catch (InvalidPathException err) 
		{
			//err.printStackTrace();
			message=fs.getFtpMessage("550_RMD_Failure");
			message=message.replaceAll("%1", inPath);
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),message+":"+err.getMessage());
		}
	}
}
