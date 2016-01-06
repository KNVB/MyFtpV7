package com.myftpserver.command;

import java.nio.file.InvalidPathException;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.ServerConfig;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
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
public class NLST implements FtpCommandInterface 
{
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param) 
	{
		boolean fullList=false;
		Logger logger=fs.getLogger();
		String clientPath=new String();
		ServerConfig serverConfig=fs.getServerConfig();
		StringBuffer resultList=new StringBuffer();
		FileManager fm=serverConfig.getFileManager();

		if (param.startsWith("-"))
		{
			clientPath=fs.getCurrentPath();
			fullList=true;
		}
		else
		{	
			clientPath=param;
			fullList=false;
		}
		logger.debug("fullList="+fullList+",clientPath="+clientPath+",param="+param);
		try 
		{
			if (fullList)
				resultList=fm.getFullDirList(fs,clientPath);
			else
				resultList=fm.getFileNameList(fs,clientPath);
			Utility.sendFileListToClient(ctx,fs,resultList);
		}		
		catch (InterruptedException |AccessDeniedException|NotADirectoryException err) 
		{
			//Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
			//err.printStackTrace();
			Utility.closeDataChannel(ctx,fs,err.getMessage());
		}
		catch (PathNotFoundException |InvalidPathException err)
		{
			//Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
			Utility.closeDataChannel(ctx,fs,fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
		catch (Exception err)
		{
			//err.printStackTrace();
			Utility.closeDataChannel(ctx,fs,err.getMessage());
		}
	}

}
