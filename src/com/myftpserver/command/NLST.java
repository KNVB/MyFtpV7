package com.myftpserver.command;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

public class NLST implements FtpCommandInterface {

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx, String param,Logger logger)	
	{
		boolean fullList=false;
		Configuration config=fs.getConfig();
		String p[]=param.split(" ");
		String clientPath=new String();
		StringBuilder resultList=new StringBuilder();
		FileManager fm=fs.getConfig().getFileManager();
		logger.debug("p.length="+p.length);
		switch (p.length)
		{
			case 0:clientPath="";
					break;
			case 1:	if (param.equals(".") || param.startsWith("-"))
					{
						fullList=true;
						clientPath="";
					}
					else
						clientPath=param;
					break;
		}
		logger.debug("fullList="+fullList);

		try
		{
			if (fullList)
				resultList=fm.getFullDirList(fs,clientPath);
			else
				resultList=fm.getFileNameList(fs,clientPath);
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
			if (fs.isPassiveModeTransfer)
			{
				logger.debug("Passive mode");
			}
			else
			{
				logger.debug("Active mode");
				ActiveClient activeClient=new ActiveClient(fs,ctx);
				activeClient.sendFileNameList(resultList);
			}
		}
		catch (AccessDeniedException|PathNotFoundException err)
		{
			err.printStackTrace();
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		} 
		catch (InterruptedException err) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
			err.printStackTrace();
		}
		catch (Exception err)
		{
			err.printStackTrace();
		}
	}
	
}
