package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.handler.*;
import com.myftpserver.exception.*;

import org.apache.logging.log4j.Logger;

import com.myftpserver.abstracts.FileManager;
import com.myftpserver.abstracts.UserManager;
import com.myftpserver.abstracts.FtpServerConfig;
import com.myftpserver.interfaces.FtpCommandInterface;
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
public class PASS implements FtpCommandInterface 
{
	/**
	 *The argument field is a Telnet string specifying the user's password.<br>
     *This command must be immediately preceded by the user name command,<br>
     *and, for some sites, completes the user's identification for access control.<br>
     *For detail information about PASS command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>  
	 */
	public PASS()
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
		FtpServerConfig serverConfig=fs.getServerConfig();
		String message=new String();
		if ((param==null) || (param.isEmpty()))
		{
			message=fs.getFtpMessage("500_Null_Command");
		}
		else
		{
			UserManager um=serverConfig.getUserManager();
			FileManager fm=serverConfig.getFileManager();
			try 
			{
				logger.debug("User name=" +fs.getUserName()+",param="+param+",(um==null)?"+(um==null));
				User user=um.login(fs, param);
				fs.setUser(user);
				fs.setIsLogined(true);
				fs.setCurrentPath("/");
				fm.getRealHomePath(fs);
				message=fs.getFtpMessage("230_Login_Ok").replace("%1", fs.getUserName());
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);
			}
			catch (LoginFailureException e)
			{
				Utility.disconnectFromClient(ctx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("530_Invalid_Login"));
			}
			catch (AccessDeniedException | InvalidHomeDirectoryException e) 
			{
				Utility.disconnectFromClient(ctx.channel(), logger,fs.getClientIp(),e.getMessage());
				//Utility.disconnectFromClient(fs.getChannel(), logger,fs.getClientIp(),fs.getFtpMessage(e.getMessage()));
			} 
		}
	}
}
