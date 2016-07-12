package com.myftpserver.command;

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
public class HELP implements FtpCommandInterface 
{
	/**
	 *This command shall cause the server to send helpful information regarding its implementation status over the control connection to the user.
	 *For detail information about HELP command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a> 
	 */
	public HELP()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void execute(FtpSessionHandler fs, String param) 
	{
		Logger logger=fs.getLogger();
		String message=fs.getFtpMessage("214_Command_Recognized");
		message+="\r\n";
		try 
		{
			message+=Utility.getAllSupportingCommand();
			if (!message.endsWith("\r\n"))
				message+="\r\n";
			message+=fs.getFtpMessage("214_Ok");
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),message);
		} 
		catch (ClassNotFoundException e) 
		{
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),e.getMessage());
		}
		
	}
}
