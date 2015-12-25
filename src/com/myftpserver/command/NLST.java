package com.myftpserver.command;

import java.nio.file.InvalidPathException;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.NotADirectoryException;
import com.myftpserver.exception.PathNotFoundException;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
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
public class NLST implements FtpCommandInterface {

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx, String param,Logger logger)	
	{
		boolean fullList=false;
		String clientPath=new String();
		Configuration config=fs.getConfig();
		StringBuffer resultList=new StringBuffer();
		FileManager fm=fs.getConfig().getFileManager();

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
			if (fs.isPassiveModeTransfer)
			{
				logger.info("Transfer Directory listing in Passive mode");
				PassiveServer ps=fs.getPassiveServer();
				ps.sendFileNameList(resultList,ctx);
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
			}
			else
			{
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
				logger.info("Transfer Directory listing in Active mode");
				ActiveClient activeClient=new ActiveClient(fs,ctx);
				activeClient.sendFileNameList(resultList);
			}
		}
		catch (InterruptedException |NotADirectoryException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
			err.printStackTrace();
		}
		catch (PathNotFoundException |InvalidPathException err)
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
		catch (AccessDeniedException e)
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("550_Permission_Denied")+":"+e.getMessage());
		} 
		catch (Exception err)
		{
			err.printStackTrace();
		}
	}
	
}
