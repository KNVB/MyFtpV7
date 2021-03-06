package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.Logger;

import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.util.Utility;

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
public class REIN implements FtpCommandInterface
{
	/**
	 *This command terminates a USER, flushing all I/O and account
     *information, except to allow any transfer in progress to be completed.<br>
     *For detail information about REIN command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>
     */
	public REIN()
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
		String message=new String();
		Logger logger=fs.getLogger();
		message=fs.getFtpMessage("220_Reinitialize");
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);
		fs.reinitialize(ctx);
		
		//MyFtpServer myFtpServer=fs.getServer();
		//myFtpServer.	Reinitialize
	}

}
