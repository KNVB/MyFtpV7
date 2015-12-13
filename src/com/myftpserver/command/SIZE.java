package com.myftpserver.command;

import com.util.*;
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
		FileManager fm=fs.getConfig().getFileManager();
		String message=new String();
		try 
		{
			long size=fm.getPathSize(fs, param);
			message=fs.getConfig().getFtpMessage("213_File_Size");
			message=message.replaceAll("%1", String.valueOf(size));
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),message);
		} 
		catch (AccessDeniedException | PathNotFoundException e) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),e.getMessage());
		}
	}
}
