package com.myftpserver;

import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.handler.FtpSessionHandler;
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
public class FtpCommandExecutor 
{
	private FtpSessionHandler thisSession=null;
	private org.apache.logging.log4j.Logger logger=null;
	/**
	 * FTP command executor
	 * @param fs FtpSessionHandler object
	 */
	public FtpCommandExecutor (FtpSessionHandler fs)
	{
		this.thisSession=fs;
		this.logger=fs.getLogger();	 
	}
	/**
	 * Execute a raw ftp command
	 * @param ctx A ChannelHandlerContext for sending command execution result to client
	 * @param inString incoming raw FTP command 
	 * @param logger message logger 
	 */
	public void doCommand(ChannelHandlerContext ctx, String inString, Logger logger) 
	{
		if (inString==null)
		{
			Utility.sendMessageToClient(ctx.channel(),logger,thisSession.getClientIp(),thisSession.getFtpMessage("500_NULL_Command"));
		}
		else
		{
			String command=new String();
			String parameters=new String();
			int i=inString.indexOf(" ");
			if (i==-1)
			{	
				command=inString;
			}
			else
			{	
				command=inString.substring(0,i).trim().toUpperCase();
				parameters=inString.substring(i+1).trim();
			}
			logger.debug("Command="+command+",p="+parameters+",isLoggined="+thisSession.isLogined());
			if (this.thisSession.isLogined())
			{
				executeCommand(ctx,command,parameters);
			}
			else
			{
				switch (command)
				{
					case "USER":
					case "SYST":	
					case "OPTS":
					case "QUIT":
					case "PASS":executeCommand(ctx,command,parameters);
								break;
					default:Utility.disconnectFromClient(ctx.channel(),logger,thisSession.getClientIp(),thisSession.getFtpMessage("530_Not_Login"));
							break;	
				}
			}
		}		
	}
	private void executeCommand(ChannelHandlerContext ctx,String cmdString,String parameters)
	{
		FtpCommandInterface cmd;
		try
		{
			cmd=(FtpCommandInterface) Class.forName("com.myftpserver.command."+cmdString.toUpperCase()).newInstance();
			cmd.execute(thisSession,parameters);
		}
		catch (InstantiationException | IllegalAccessException| ClassNotFoundException e) 
		{
			logger.info(cmdString.toUpperCase()+" command not implemented");
			Utility.sendMessageToClient(ctx.channel(),logger,thisSession.getClientIp(),thisSession.getFtpMessage("502_Command_Not_Implemeneted"));
		}
		catch (Exception err)
		{
			logger.debug(err.getMessage());
			err.printStackTrace();
		}
	}	
}