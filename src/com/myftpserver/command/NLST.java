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
		String clientPath=new String();
		String p[]=param.split(" ");
		boolean fullList=false;
		FileManager fm=fs.getConfig().getFileManager();
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
				fm.showFullDirList(fs,ctx,clientPath);
			else
				fm.showFileNameList(fs,ctx,clientPath);
		}
		catch (AccessDeniedException|PathNotFoundException err)
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		} 
		catch (InterruptedException err) 
		{
			// TODO Auto-generated catch block
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
			err.printStackTrace();
		}
	}
	
}
