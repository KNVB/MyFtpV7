package com.myftpserver.command;

import com.util.Utility;
import java.net.InetSocketAddress;
import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;
import org.apache.logging.log4j.Logger;
import com.myftpserver.handler.FtpSessionHandler;
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
public class EPSV implements FtpCommandInterface 
{
	/**
	 * It is specified a port in server side for passive mode operation.<br>
	 * It supports both IPV6 and IPV4.<br>
	 * For detail information about EPSV command,please refer <a href="https://tools.ietf.org/html/rfc2428">RFC 2428</a>   
	 */
	public EPSV()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * It start a passive server which listen on a data port and wait for a connection.<br>
	 * And then return the data port no. &nbsp; to ftp client for passive mode operation. 
	 */
	@Override
	public void execute(FtpSessionHandler fs, String param) 
	{
		int port;
		Logger logger=fs.getLogger();
		FtpServerConfig serverConfig=fs.getServerConfig();
		String message=new String(),localIp=new String();
		MyFtpServer myFtpServer=fs.getServer();
		if (serverConfig.isSupportPassiveMode())
		{
			port=myFtpServer.getNextPassivePort();
			if (port==-1)
				message=fs.getFtpMessage("550_CANT_CONNECT_CLNT");
			else
			{	
				message=fs.getFtpMessage("229_EPSV_Ok");
				message=message.replace("%1", String.valueOf(port));
				localIp=((InetSocketAddress)fs.getChannel().localAddress()).getAddress().getHostAddress();
				fs.isPassiveModeTransfer=true;						
				PassiveServer passiveServer=new PassiveServer(fs,localIp,port);
				fs.setPassiveServer(passiveServer);
			}
		}
		else
		{
			message=fs.getFtpMessage("502_Command_Not_Implemeneted");
		}
		Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(), message);	
		
	}

}
