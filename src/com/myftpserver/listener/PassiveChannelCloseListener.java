package com.myftpserver.listener;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

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
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, config.getFtpMessage("226_Transfer_Ok"));
		this.passiveServer.stop();
	}

}
