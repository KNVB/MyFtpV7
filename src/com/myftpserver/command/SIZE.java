package com.myftpserver.command;

import java.nio.file.InvalidPathException;

import com.util.*;
import com.myftpserver.Configuration;
import com.myftpserver.exception.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

public class SIZE implements FtpCommandInterface
{
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param, Logger logger) 
	{
		Configuration config=fs.getConfig();
		FileManager fm=config.getFileManager();
		String message=new String();
		try 
		{
			long size=fm.getPathSize(fs, param);
			message=fs.getConfig().getFtpMessage("213_File_Size");
			message=message.replaceAll("%1", String.valueOf(size));
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),message);
		} 
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
		catch (AccessDeniedException e) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("550_Permission_Denied")+":"+e.getMessage());
		}
	}
}
