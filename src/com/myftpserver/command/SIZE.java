package com.myftpserver.command;

import java.nio.file.InvalidPathException;

import com.util.*;
import com.myftpserver.ServerConfig;
import com.myftpserver.exception.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;

import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.Logger;
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
public class SIZE implements FtpCommandInterface
{
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param) 
	{
		Logger logger=fs.getLogger();
		ServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		String message=new String();
		try 
		{
			long size=fm.getPathSize(fs, param);
			message=fs.getFtpMessage("213_File_Size");
			message=message.replaceAll("%1", String.valueOf(size));
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),message);
		} 
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
		catch (AccessDeniedException e) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getFtpMessage("550_Permission_Denied")+":"+e.getMessage());
		}
	}
}
