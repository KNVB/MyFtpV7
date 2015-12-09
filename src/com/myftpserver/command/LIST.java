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

public class LIST implements FtpCommandInterface 
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
		logger.debug("param="+param);
		//logger.debug("Server currentPath:"+fs.getServerCurrentPath());
		String p[]=param.split(" ");
		String clientPath=new String();
		Configuration config=fs.getConfig();
		StringBuffer resultList=new StringBuffer();
		FileManager fm=fs.getConfig().getFileManager();
		switch (p.length)
		{
			case 0:clientPath="";
					break;
			case 1:	if (param.equals(".") || param.startsWith("-"))
						clientPath="";
					else
						clientPath=param;
					break;
			case 2:	clientPath=p[1];
					break;
			default:for(int i=param.indexOf(" ");i<param.length();i++)
					{	
						if (param.charAt(i)!=' ')
						{	
							param=param.substring(i+1);
							break;
						}
					}
					clientPath=param;
					break;
		}
		try
		{
			resultList=fm.getFullDirList(fs,clientPath);
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
		catch (InterruptedException|AccessDeniedException|PathNotFoundException err)
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		} 
	}	
}
