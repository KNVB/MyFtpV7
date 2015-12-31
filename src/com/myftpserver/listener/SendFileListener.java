package com.myftpserver.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import org.apache.logging.log4j.Logger;

import com.myftpserver.ActiveClient;
import com.myftpserver.handler.FtpSessionHandler;

public class SendFileListener implements ChannelFutureListener  
{
	Logger logger;
	String remoteIp;
	FtpSessionHandler fs;
	String fileName;
	ChannelHandlerContext responseCtx;
	public SendFileListener(ChannelHandlerContext responseCtx,FtpSessionHandler fs,String fileName)
	{
		this.fs=fs;
		this.logger=fs.getLogger();
		this.fileName=fileName;
		this.responseCtx=responseCtx;
		this.remoteIp=fs.getClientIp();
	}
	
	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		logger.info("Message:"+fs.getFtpMessage("150_Open_Data_Conn")+" sent to:"+remoteIp);
		if (fs.isPassiveModeTransfer)
		{
			logger.info("Transfer File in Passive mode");
		}
		else
		{
			logger.info("Transfer File in Active mode");
			ActiveClient activeClient=new ActiveClient(fs,responseCtx);
			activeClient.sendFile(fileName);
		}
	}


}
