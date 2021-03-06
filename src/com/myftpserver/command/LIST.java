package com.myftpserver.command;
import io.netty.channel.ChannelHandlerContext;

import java.nio.file.InvalidPathException;

import com.util.Utility;

import org.apache.logging.log4j.Logger;

import com.myftpserver.abstracts.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.abstracts.FtpServerConfig;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.NotADirectoryException;
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
public class LIST implements FtpCommandInterface 
{
	/**
	 *It returns a list of files in the specified directory.<br>
	 *For detail information about LIST command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>     
	 */
	public LIST()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(ChannelHandlerContext ctx,FtpSessionHandler fs, String param) 
	{
		Logger logger=fs.getLogger();
		String p[]=param.split(" ");
		String clientPath=new String();
		FtpServerConfig serverConfig=fs.getServerConfig();
		StringBuffer resultList=new StringBuffer();
		FileManager fm=serverConfig.getFileManager();
		logger.debug("param="+param);
		switch (p.length)
		{
			case 0:clientPath="";
					break;
			case 1:	if (param.equals(".") || param.startsWith("-"))
						clientPath="";
					else
						clientPath=param;
					break;
			case 2:	clientPath=p[1];
					break;
			default:for(int i=param.indexOf(" ");i<param.length();i++)
					{	
						if (param.charAt(i)!=' ')
						{	
							param=param.substring(i+1);
							break;
						}
					}
					clientPath=param;
					break;
		}
		try
		{
			resultList=fm.getFullDirList(fs,clientPath);
			Utility.sendFileListToClient(ctx,fs,resultList);
		}
		catch (InterruptedException |AccessDeniedException|NotADirectoryException err)
		{
			//Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
			Utility.handleTransferException(ctx,fs,err.getMessage());
		} 
		catch (PathNotFoundException |InvalidPathException err)
		{
			//Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
			Utility.handleTransferException(ctx,fs,fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}	
	}	
}
