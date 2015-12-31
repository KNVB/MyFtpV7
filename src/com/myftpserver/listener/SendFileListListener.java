package com.myftpserver.listener;

import org.apache.logging.log4j.Logger;

import com.myftpserver.ActiveClient;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class SendFileListListener implements ChannelFutureListener  
{
	Logger logger;
	String remoteIp;
	FtpSessionHandler fs;
	StringBuffer resultList;
	ChannelHandlerContext responseCtx;
	public SendFileListListener(ChannelHandlerContext responseCtx,FtpSessionHandler fs,StringBuffer resultList)
	{
		this.fs=fs;
		this.logger=fs.getLogger();
		this.resultList=resultList;
		this.responseCtx=responseCtx;
		this.remoteIp=fs.getClientIp();
	}
	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		logger.info("Message:"+fs.getFtpMessage("150_Open_Data_Conn")+" sent to:"+remoteIp);
		if (fs.isPassiveModeTransfer)
		{
			logger.info("Transfer Directory listing in Active mode");
			PassiveServer ps=fs.getPassiveServer();
			ps.sendFileNameList(resultList,responseCtx);
		}
		else
		{
			logger.info("Transfer Directory listing in Active mode");
			ActiveClient activeClient=new ActiveClient(fs,responseCtx);
			activeClient.sendFileNameList(resultList);
		}
	}

}
