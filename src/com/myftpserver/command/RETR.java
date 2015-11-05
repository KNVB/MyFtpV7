package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.util.Utility;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.QuotaExceedException;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;

public class RETR implements FtpCommandInterface {

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs,ChannelHandlerContext ctx, String param, Logger logger)
	{
		FileManager fm=fs.getConfig().getFileManager();
		logger.debug("param="+param+"|");
		
		try 
		{
			fm.getFile(fs,ctx,param);
		} 
		catch (InterruptedException|AccessDeniedException | PathNotFoundException | QuotaExceedException err) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}
	}	
}
