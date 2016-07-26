package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

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
public class QUIT implements FtpCommandInterface
{
	/**
	 *This command cause the current user logout from the ftp server.
	 *For detail information about QUIT command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>
	 */
	public QUIT()
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
		//Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getConfig().getFtpMessage("221_Logout_Ok"));
		String goodByeMsg=fs.getFtpMessage("221_Logout_Ok");
		String remoteIp=fs.getClientIp();
		Utility.disconnectFromClient(ctx.channel(), fs.getLogger(), remoteIp, goodByeMsg);
	}
}