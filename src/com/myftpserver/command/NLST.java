package com.myftpserver.command;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
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
		String clientPath=new String();
		Configuration config=fs.getConfig();
		StringBuffer resultList=new StringBuffer();
		FileManager fm=fs.getConfig().getFileManager();

		if (param.startsWith("-"))
		{
			clientPath=fs.getCurrentPath();
			fullList=true;
		}
		else
		{	
			clientPath=param;
			fullList=false;
		}
		logger.debug("fullList="+fullList+",clientPath="+clientPath+",param="+param);
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
				PassiveServer ps=fs.getPassiveServer();
				ps.sendFileNameList(resultList,ctx);
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
			logger.debug(err.getMessage());
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
		} 
		catch (InterruptedException err) 
		{
			Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),err.getMessage());
			err.printStackTrace();
		}
		catch (Exception err)
		{
			err.printStackTrace();
		}
	}
	
}
