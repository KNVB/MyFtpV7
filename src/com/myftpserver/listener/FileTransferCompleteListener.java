package com.myftpserver.listener;

import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class FileTransferCompleteListener implements ChannelFutureListener 
{
	Logger logger;
	FtpSessionHandler fs;
	PassiveServer txServer=null;
	ChannelHandlerContext responseCtx;
	
	public FileTransferCompleteListener(FtpSessionHandler fs,PassiveServer txServer, ChannelHandlerContext responseCtx)
	{
		this.fs=fs;
		this.txServer=txServer;
		this.responseCtx=responseCtx;
		this.logger=fs.getConfig().getLogger();
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception {
		// TODO Auto-generated method stub
		if (txServer==null)
			cf.channel().close().addListener(new ActiveChannelCloseListener(fs,responseCtx));
		else
			cf.channel().close().addListener(new PassiveChannelCloseListener(fs,responseCtx,txServer));
		logger.debug("File Transfer completed.");
	}
}