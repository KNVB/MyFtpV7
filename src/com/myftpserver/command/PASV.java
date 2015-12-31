package com.myftpserver.command;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.handler.*;
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
public class PASV implements FtpCommandInterface 
{

	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx, String param) 
	{
		int port;
		Logger logger=fs.getLogger();
		ServerConfig serverConfig=fs.getServerConfig();
		MyFtpServer server=fs.getServer();
		String message=new String(),localIP=((InetSocketAddress)ctx.channel().localAddress()).getAddress().getHostAddress();
		if (serverConfig.isSupportPassiveMode())
		{
			port=server.getNextPassivePort();
			if (port==-1)
				message=fs.getFtpMessage("550_CANT_CONNECT_CLNT");
			else
			{	
				logger.debug("Port "+port+" is assigned.");
				fs.isPassiveModeTransfer=true;
				message=fs.getFtpMessage("227_Enter_Passive_Mode");
				message=message.replaceAll("%1", localIP.replaceAll("\\.", ","));
				message=message.replaceAll("%2", String.valueOf(port/256));
				message=message.replaceAll("%3", String.valueOf(port % 256));
				PassiveServer ps=new PassiveServer(fs,localIP,port);
				fs.setPassiveServer(ps);
			}				
		}
		else
		{
			fs.isPassiveModeTransfer=false;
			message=fs.getFtpMessage("502_Command_Not_Implemeneted");
		}
		//Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);
	}
}
