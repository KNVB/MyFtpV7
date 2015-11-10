package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.util.Utility;
import com.myftpserver.ActiveClient;
import com.myftpserver.Configuration;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
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
		Configuration config=fs.getConfig();
		FileManager fm=fs.getConfig().getFileManager();
		logger.debug("param="+param+"|");
		
		try 
		{
			String serverPath=fm.getFile(fs,param);
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
			if (fs.isPassiveModeTransfer)
			{
				logger.debug("Passive mode");
			}
			else
			{
				logger.debug("Active mode");
				ActiveClient activeClient=new ActiveClient(fs,ctx);
				activeClient.sendFile(serverPath);
			}
		} 
		catch (InterruptedException|AccessDeniedException | PathNotFoundException err) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}
	}	
}
