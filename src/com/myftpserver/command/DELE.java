package com.myftpserver.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.NotAFileException;
import com.myftpserver.exception.PathNotFoundException;

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
public class DELE implements FtpCommandInterface {

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs,ChannelHandlerContext ctx, String inPath, Logger logger)
	{
		Configuration config=fs.getConfig();
		FileManager fm=fs.getConfig().getFileManager();
		logger.debug("param="+inPath+"|");
		try 
		{
			String serverPath=fm.getServerPath(fs, inPath, FileManager.WRITE_PERMISSION);
			if (Files.isDirectory(Paths.get(serverPath)))
			{
				String message=config.getFtpMessage("550_Not_A_File");
				message=message.replaceAll("%1", inPath);
				throw new NotAFileException(message);
			}
			else	
				Files.delete(Paths.get(serverPath));
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("250_Delete_Ok"));
		}
		catch (IOException|NotAFileException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("550_File_Delete_Failure")+":"+err.getMessage());
		}
		catch (AccessDeniedException e) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("550_Permission_Denied"));
		}
		
	}	
}
