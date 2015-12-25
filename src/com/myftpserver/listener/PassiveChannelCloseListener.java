package com.myftpserver.listener;

import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class PassiveChannelCloseListener implements ChannelFutureListener 
{
	Logger logger;
	String remoteIp;
	Configuration config;
	FtpSessionHandler fs;
	ChannelHandlerContext responseCtx;
	private PassiveServer passiveServer;
	public PassiveChannelCloseListener(FtpSessionHandler fs, ChannelHandlerContext responseCtx, PassiveServer passiveServer) 
	{
		this.fs=fs;
		this.config=fs.getConfig();
		this.responseCtx=responseCtx;
		this.remoteIp=fs.getClientIp();
		this.passiveServer=passiveServer;
		this.logger=fs.getConfig().getLogger();
	}

	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		this.passiveServer.stop();
		this.passiveServer=null;
		//Utility.sendMessageToClient(this.responseCtx.channel(),logger, fs.getClientIp(), config.getFtpMessage("226_Transfer_Ok"));
	}
}
