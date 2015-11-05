package com.myftpserver.listener;

import org.apache.log4j.Logger;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class SendFileNameListCompleteListener implements ChannelFutureListener  
{
	String clientIp;
	FtpSessionHandler fs;
	ChannelHandlerContext responseCtx;
	Logger logger;
	String remoteIp;
	Configuration config;
	PassiveServer passiveServer=null;
	public SendFileNameListCompleteListener(FtpSessionHandler fs,ChannelHandlerContext rCtx,PassiveServer txServer) 
	{
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		fs.getConfig().getLogger().debug("File name list transfered to "+remoteIp+" Completed.");
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, config.getFtpMessage("226_Transfer_Ok")); 
		//fs.getConfig().getLogger().debug("File name list transfered to "+clientIp+" Completed.");
		ch.channel().close();
	}

}
