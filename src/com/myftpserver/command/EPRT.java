package com.myftpserver.command;

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
public class EPRT implements FtpCommandInterface 
{
	/**
	 * It is specified a port in client side for active mode operation.<br>
	 * It support both IPV6 and IPV4.<br>
	 * For detail information about EPRT command,please refer <a href="https://tools.ietf.org/html/rfc2428">RFC 2428</a>   
	 */
	public EPRT()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * It gets port no.&nbsp; from ftp client response for active mode operation. 
	 */
	@Override
	public void execute(FtpSessionHandler fs, String param) 
	{
		String temp[]=param.trim().split("\\|");
		Logger logger=fs.getLogger();
		if (temp.length!=4)
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(), fs.getFtpMessage("500_Null_Command"));
		else
		{	
			try
			{
				int portNo=Integer.parseInt(temp[temp.length-1]);
				logger.debug("Port="+portNo);
				fs.isPassiveModeTransfer=false;
				fs.setActiveDataPortNo(portNo);
				Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(), fs.getFtpMessage("200_Port_Ok"));
			}
			catch (Exception e)
			{
				Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(), fs.getFtpMessage("550_CANT_CONNECT_CLNT"));
			}
		}
	}

}
