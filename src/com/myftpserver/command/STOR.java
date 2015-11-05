package com.myftpserver.command;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;
import com.myftpserver.exception.QuotaExceedException;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.util.Utility;

public class STOR implements FtpCommandInterface
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
			fm.putFile(fs,ctx,param);
		} 
		catch (InterruptedException|AccessDeniedException | PathNotFoundException |QuotaExceedException err) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}

	}
}
