package com.myftpserver.command;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.ServerConfig;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.NotAFileException;
import com.myftpserver.exception.PathNotFoundException;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FileManager;
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
public class RNFR implements FtpCommandInterface {

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String inPath) 
	{
		Logger logger=fs.getLogger();
		ServerConfig serverConfig=fs.getServerConfig();
		FileManager fm=serverConfig.getFileManager();
		String message=fs.getFtpMessage("550_Not_A_File");
		try 
		{
			String serverPath=fm.getServerPath(fs, inPath, FileManager.WRITE_PERMISSION);
			if (Files.isDirectory(Paths.get(serverPath)))
			{
				message=message.replaceAll("%1", inPath);
				throw new NotAFileException(message);
			}
			else
			{
				fs.setReNameFrom(serverPath);
			}
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getFtpMessage("350_Ready_For_RNTO"));
		}
		catch (NotAFileException |AccessDeniedException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
	}

}
