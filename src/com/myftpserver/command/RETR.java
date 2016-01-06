package com.myftpserver.command;

import java.io.IOException;
import java.nio.file.InvalidPathException;

import com.util.Utility;
import com.myftpserver.ServerConfig;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.NotAFileException;
import com.myftpserver.exception.PathNotFoundException;

import io.netty.channel.ChannelHandlerContext;

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
public class RETR implements FtpCommandInterface {

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs,ChannelHandlerContext ctx, String param)
	{
		Logger logger=fs.getLogger();
		ServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		logger.debug("param="+param+"|");
		try 
		{
			String serverPath=fm.getFile(fs,param);
			Utility.sendFileToClient(ctx,fs,serverPath);
		}
		catch (InterruptedException|NotAFileException |AccessDeniedException |IOException err) 
		{
			Utility.closeDataChannel(ctx,fs,err.getMessage());
		}
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.closeDataChannel(ctx,fs,fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
	}	
}
