package com.myftpserver.admin.client.tx;

import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

public class AdminClientChannelInitializer extends ChannelInitializer<Channel>
{
	private Logger logger;
	public AdminClientChannelInitializer(Logger logger) 
	{
		this.logger=logger;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		ch.pipeline().addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
		ch.pipeline().addLast(new AdminClientSessionHandler(logger));
	}	

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

}