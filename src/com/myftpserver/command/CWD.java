package com.myftpserver.command;

import com.util.*;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.*;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
public class CWD implements FtpCommandInterface 
{

	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param, Logger logger)
	{
		// TODO Auto-generated method stub
		FileManager fm=fs.getConfig().getFileManager();
		logger.debug("param="+param+"|");
		
		try 
		{
			fm.changeDirectory(fs,param);
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getConfig().getFtpMessage("200_Ok"));
		} 
		catch (AccessDeniedException | PathNotFoundException err) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}
	}
}
