package com.myftpserver.command;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.myftpserver.Configuration;
import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;
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
public class EPSV implements FtpCommandInterface 
{

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param, Logger logger) 
	{
		int port;
		Configuration config=fs.getConfig();
		String message=new String(),localIp=new String();
		MyFtpServer myFtpServer=fs.getServer();
		if (config.isSupportPassiveMode())
		{
			port=myFtpServer.getNextPassivePort();
			if (port==-1)
				message=fs.getConfig().getFtpMessage("550_CANT_CONNECT_CLNT");
			else
			{	
				message=fs.getConfig().getFtpMessage("229_EPSV_Ok");
				message=message.replaceAll("%1", String.valueOf(port));
				localIp=((InetSocketAddress)ctx.channel().localAddress()).getAddress().getHostAddress();
				fs.isPassiveModeTransfer=true;						
				PassiveServer passiveServer=new PassiveServer(fs,localIp,port);
				fs.setPassiveServer(passiveServer);
			}
		}
		else
		{
			message=fs.getConfig().getFtpMessage("502_Command_Not_Implemeneted");
		}
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);	
		
	}

}
