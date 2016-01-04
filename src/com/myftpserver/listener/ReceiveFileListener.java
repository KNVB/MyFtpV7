package com.myftpserver.listener;

import org.apache.logging.log4j.Logger;

import com.myftpserver.ActiveClient;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ReceiveFileListener implements ChannelFutureListener 
{
	Logger logger;
	String remoteIp;
	FtpSessionHandler fs;
	String fileName;
	ChannelHandlerContext responseCtx;
	public ReceiveFileListener(ChannelHandlerContext responseCtx,FtpSessionHandler fs,String fileName)
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
			logger.info("Receive File in Passiveive mode");
			PassiveServer ps=fs.getPassiveServer();
			ps.receiveFile(fileName, responseCtx);
		}
		else
		{
			logger.info("Receive File in Active mode");
			ActiveClient activeClient=new ActiveClient(fs,responseCtx);
			activeClient.receiveFile(fileName);
		}
		
	}

}
