package com.myftpserver.command;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.ActiveClient;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.QuotaExceedException;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;

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
		String serverPath=new String(),fileName;
		Configuration config=fs.getConfig();
		FileManager fm=fs.getConfig().getFileManager();
		logger.debug("param="+param+"|");
		try 
		{
			fileName=param;
			if (fileName.indexOf("/")==-1)
				fileName=fs.getCurrentPath()+"/"+fileName;
			serverPath=fm.putFile(fs,fileName);
			logger.debug("serverPath="+serverPath+",fileName="+fileName);

			if (fs.isPassiveModeTransfer)
			{
				logger.debug("Passive mode");
				PassiveServer ps=fs.getPassiveServer();
				ps.receiveFile(serverPath,ctx);
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
			}
			else
			{
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
				logger.debug("Active mode");
				ActiveClient activeClient=new ActiveClient(fs,ctx);
				activeClient.receiveFile(serverPath);
			}
		} 
		catch (InterruptedException|AccessDeniedException |PathNotFoundException |QuotaExceedException err) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}

	}
}
