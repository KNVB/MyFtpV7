package com.myftpserver.listener;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ActiveChannelCloseListener  implements ChannelFutureListener 
{
	Logger logger;
	public ActiveChannelCloseListener(Logger logger) 
	{
		// TODO Auto-generated constructor stub
		this.logger=logger;
	}

	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		// TODO Auto-generated method stub
		logger.debug("Active Mode Transfer channel is closed");	
	}

}
