package com.myftpserver.command;


import java.io.IOException;
import java.nio.file.InvalidPathException;

import com.util.Utility;
import com.myftpserver.ActiveClient;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;
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
			if (fs.isPassiveModeTransfer)
			{
				logger.debug("Passive mode");
				PassiveServer ps=fs.getPassiveServer();
				ps.sendFile(serverPath,ctx);
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
			}
			else
			{
				logger.debug("Active mode");
				Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("150_Open_Data_Conn"));
				ActiveClient activeClient=new ActiveClient(fs,ctx);
				activeClient.sendFile(serverPath);
			}
			
		} 
		catch (InterruptedException|IOException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		}
		catch (PathNotFoundException|InvalidPathException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("550_File_Path_Not_Found")+":"+err.getMessage());
		}
		catch (AccessDeniedException e) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("550_Permission_Denied")+":"+e.getMessage());
		}
		
	}	
}
