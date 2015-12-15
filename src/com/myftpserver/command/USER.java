package com.myftpserver.command;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.handler.FtpSessionHandler;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;
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
public class USER implements com.myftpserver.interfaces.FtpCommandInterface
{
	@Override
	public void execute(FtpSessionHandler fs,ChannelHandlerContext ctx, String param, Logger logger) 
	{
		Configuration config=fs.getConfig();
		String message=new String();
		if (param ==null)
		{
			message=config.getFtpMessage("500_Null_Command");
		}
		else
		{
			message=config.getFtpMessage("331_Password_Required");
			message=message.replaceAll("%1", param);
			fs.setUserName(param);
		}
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);
	}
	@Override
	public String helpMessage(com.myftpserver.handler.FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}
}