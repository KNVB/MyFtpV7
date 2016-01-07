package com.myftpserver.listener;


import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.util.Utility;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class PassiveChannelCloseListener implements ChannelFutureListener 
{
	Logger logger;
	String remoteIp;
	FtpSessionHandler fs;
	ChannelHandlerContext responseCtx;
	private PassiveServer passiveServer;
	/**
	 * It is triggered when a passive mode channel is closed.  
	 * @param fs FTP session 
	 * @param responseCtx Response Channel
	 * @param passiveServer Passive Server object
	 */
	public PassiveChannelCloseListener(FtpSessionHandler fs, ChannelHandlerContext responseCtx, PassiveServer passiveServer) 
	{
		this.fs=fs;
		this.responseCtx=responseCtx;
		this.remoteIp=fs.getClientIp();
		this.passiveServer=passiveServer;
		this.logger=fs.getLogger();
	}

	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		this.passiveServer.stop();
		this.passiveServer=null;
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, fs.getClientIp(), fs.getFtpMessage("226_Transfer_Ok"));
	}
}
