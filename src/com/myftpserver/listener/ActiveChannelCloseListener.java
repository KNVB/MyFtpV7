package com.myftpserver.listener;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ActiveChannelCloseListener  implements ChannelFutureListener 
{
	Logger logger;
	String remoteIp;
	FtpSessionHandler fs;
	Configuration config;
	ChannelHandlerContext responseCtx;
	public ActiveChannelCloseListener(FtpSessionHandler fs, ChannelHandlerContext responseCtx) 
	{
		this.fs=fs;
		this.config=fs.getConfig();
		this.responseCtx=responseCtx;
		this.remoteIp=fs.getClientIp();
		this.logger=fs.getConfig().getLogger();
	}

	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		logger.debug("Active Mode Transfer channel is closed");
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, config.getFtpMessage("226_Transfer_Ok"));
	}
}