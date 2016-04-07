package com.myftpserver.command;

import com.util.*;
import com.myftpserver.exception.*;
import com.myftpserver.interfaces.FileManager;
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
public class CWD implements FtpCommandInterface 
{
	/**
	 * This command is used change working directory to the specified directory.<br>
	 * For detail information about CWD command,please refer <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>  
	 */
	public CWD()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * It perform both security test and directory existence test;<br>
	 * if one of these tests fail, it will return error message.  
	 */
	@Override
	public void execute(FtpSessionHandler fs,String param)
	{
		Logger logger=fs.getLogger();
		FileManager fm=fs.getServerConfig().getFileManager();
		logger.debug("param="+param+"|");
		
		try 
		{
			fm.changeDirectory(fs,param);
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),fs.getFtpMessage("200_Ok"));
		} 
		catch (AccessDeniedException | PathNotFoundException err) 
		{
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),err.getMessage());
		}
	}
}
