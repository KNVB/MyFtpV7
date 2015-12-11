package com.myftpserver.listener;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class SendFileNameListCompleteListener implements ChannelFutureListener  
{
	Logger logger;
	String remoteIp;
	FtpSessionHandler fs;
	Configuration config;
	PassiveServer passiveServer=null;
	ChannelHandlerContext responseCtx;
	public SendFileNameListCompleteListener(FtpSessionHandler fs,ChannelHandlerContext rCtx,PassiveServer txServer) 
	{
		this.fs=fs;
		this.remoteIp=fs.getClientIp();
		this.logger=fs.getConfig().getLogger();
		this.responseCtx=rCtx;
		this.config=fs.getConfig();
		this.passiveServer=txServer;
	}

	@Override
	public void operationComplete(ChannelFuture ch) throws Exception 
	{
		fs.getConfig().getLogger().debug("File name list transfered to "+remoteIp+" Completed.");
		if (passiveServer!=null)
			ch.addListener(new PassiveChannelCloseListener(fs, responseCtx, passiveServer));
		ch.channel().close();
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, config.getFtpMessage("226_Transfer_Ok"));
		
	}
}
