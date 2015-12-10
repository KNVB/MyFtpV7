package com.myftpserver.listener;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class SendFileCompleteListener implements ChannelFutureListener
{
	FtpSessionHandler fs;
	String fileName;
	ChannelHandlerContext responseCtx;
	PassiveServer txServer=null;
	Logger logger;
	String remoteIp;
	Configuration config;

	public SendFileCompleteListener(String fileName,FtpSessionHandler fs,ChannelHandlerContext responseCtx, PassiveServer txServer)
	{
		this.fs=fs;
		this.fileName=fileName;
		this.txServer=txServer;
		this.responseCtx=responseCtx;
		this.remoteIp=fs.getClientIp();
		this.logger=fs.getConfig().getLogger();
		this.config=fs.getConfig();
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, config.getFtpMessage("226_Transfer_Ok")); 
		if (txServer==null)
			cf.channel().close().addListener(new ActiveChannelCloseListener(fs,this.responseCtx));
		else
			cf.channel().close().addListener(new PassiveChannelCloseListener(fs,this.responseCtx, txServer));
	}

}
