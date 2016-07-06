package com.myftpserver.command;

import com.util.Utility;
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
public class PORT implements FtpCommandInterface 
{
	String temp=new String();
	/**
	 * The PORT command is sent by an FTP client to establish a secondary connection (address and port) for data to travel over.<br>
	 * For detail information about PORT command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>
	 */
	public PORT()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs,String param) 
	{
		Logger logger=fs.getLogger();
		param=param.trim();
		String[] p=param.split(",");
		if (p.length!=6)
		{
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(), fs.getFtpMessage("500_Null_Command"));
		}
		else
		{
			int clientPort;
			int high = Integer.parseInt(p[4]);
		    int low = Integer.parseInt(p[5]);
		    if (high < 0 || high > 255 || low < 0 || low > 255)
			{
		    	Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(), fs.getFtpMessage("500_Null_Command"));
			}
		    else
		    {	 
		    	clientPort=(high << 8) + low;
		    	logger.debug("Port="+clientPort);
		    	fs.isPassiveModeTransfer=false;
		    	fs.setActiveDataPortNo(clientPort);
		    	Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(), fs.getFtpMessage("200_Port_Ok"));
		    }
		}
	}
}
