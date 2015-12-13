package com.myftpserver.listener;

import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.util.Utility;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ReceiveFilerCompleteListener implements ChannelFutureListener 
{
	Logger logger;
	Configuration config;
	FtpSessionHandler fs;
	PassiveServer txServer=null;
	ChannelHandlerContext responseCtx;
	
	public ReceiveFilerCompleteListener(FtpSessionHandler fs,PassiveServer txServer, ChannelHandlerContext responseCtx)
	{
		this.fs=fs;
		this.config=fs.getConfig();
		this.txServer=txServer;
		this.responseCtx=responseCtx;
		this.logger=fs.getConfig().getLogger();
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, fs.getClientIp(), config.getFtpMessage("226_Transfer_Ok"));
		if (txServer==null)
			cf.channel().close().addListener(new ActiveChannelCloseListener(fs,responseCtx));
		else
			cf.channel().close().addListener(new PassiveChannelCloseListener(fs,responseCtx,txServer));
		logger.debug("File Transfer completed.");		
	}
}