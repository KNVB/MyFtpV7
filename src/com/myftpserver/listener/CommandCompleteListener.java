package com.myftpserver.listener;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class CommandCompleteListener implements ChannelFutureListener
{
	Logger logger; 
	String remoteIp,ftpMessage;
	public CommandCompleteListener(Logger logger, String remoteIp,String ftpMessage) 
	{
		// TODO Auto-generated constructor stub
		this.logger=logger;
		this.remoteIp=remoteIp;
		this.ftpMessage=ftpMessage;
	}

	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		// TODO Auto-generated method stub
		//cf.channel().close();
		logger.info("Message:"+ftpMessage+" sent to:"+remoteIp);
	}

}
