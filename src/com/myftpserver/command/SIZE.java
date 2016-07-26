package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import java.nio.file.InvalidPathException;

import com.util.*;
import com.myftpserver.abstracts.FtpServerConfig;
import com.myftpserver.exception.*;
import com.myftpserver.abstracts.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;

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
	/**
	 *The FTP command, SIZE OF FILE (SIZE), is used to obtain the transfer size of a file from the server-FTP process.<br>
	 *For detail information about SIZE command,please refer  <a href="https://tools.ietf.org/html/rfc3659">RFC 3659</a> 
	 */
	public SIZE()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void execute(ChannelHandlerContext ctx,FtpSessionHandler fs, String param) 
	{
		Logger logger=fs.getLogger();
		FtpServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		String message=new String();
		try 
		{
			long size=fm.getPathSize(fs, param);
			message=fs.getFtpMessage("213_File_Size");
			message=message.replace("%1", String.valueOf(size));
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),message);
		} 
		catch (PathNotFoundException|InvalidPathException |AccessDeniedException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
	}
}
