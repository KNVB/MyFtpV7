package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.util.Utility;
import com.myftpserver.FileManager;
import com.myftpserver.handler.FtpSession;
import com.myftpserver.FtpCommandInterface;
import com.myftpserver.exception.QuotaExceedException;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;

public class RETR implements FtpCommandInterface {

	@Override
	public String helpMessage(FtpSession fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSession fs,ChannelHandlerContext ctx, String param, Logger logger)
	{
		FileManager fm=fs.getConfig().getFileManager();
		logger.debug("param="+param+"|");
		
		try 
		{
			fm.downloadFile(fs,ctx,param);
		} 
		catch (InterruptedException|AccessDeniedException | PathNotFoundException | QuotaExceedException err) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}
	}	
}
