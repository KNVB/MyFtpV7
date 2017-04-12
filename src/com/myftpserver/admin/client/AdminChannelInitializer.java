package com.myftpserver.admin.client;

import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class AdminChannelInitializer extends ChannelInitializer<Channel> 
{
	private Logger logger;
	public AdminChannelInitializer(Logger logger) 
	{
		this.logger=logger;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
	}
}
