package com.myftpserver.command;
import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;
import com.myftpserver.handler.FtpSession;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

public class LIST implements FtpCommandInterface 
{
	
	@Override
	public String helpMessage(FtpSession fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSession fs, ChannelHandlerContext ctx,String param, Logger logger) 
	{
		// TODO Auto-generated method stub
		logger.debug("param="+param);
		//logger.debug("Server currentPath:"+fs.getServerCurrentPath());
		String p[]=param.split(" ");
		String clientPath=new String();
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
			fm.showFullDirList(fs,ctx,clientPath);
		}
		catch (InterruptedException|AccessDeniedException|PathNotFoundException err)
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		} 
	}	
}
