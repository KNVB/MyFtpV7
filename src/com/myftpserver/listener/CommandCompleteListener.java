package com.myftpserver.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.apache.log4j.Logger;

public class CommandCompleteListener implements ChannelFutureListener
{
	Logger logger; 
	String remoteIp,ftpMessage;
	public CommandCompleteListener(Logger logger, String remoteIp,String ftpMessage) 
	{
		this.logger=logger;
		this.remoteIp=remoteIp;
		this.ftpMessage=ftpMessage;
	}

	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		logger.info("Message:"+ftpMessage+" sent to:"+remoteIp);
	}
}
