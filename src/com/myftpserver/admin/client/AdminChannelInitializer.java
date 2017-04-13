package com.myftpserver.admin.client;

import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

public class AdminChannelInitializer extends ChannelInitializer<Channel> 
{
	private Logger logger;
	private AdminClient adminClient;
	private AdminConsole adminConsole;
	
	public AdminChannelInitializer(Logger logger, AdminConsole adminConsole,AdminClient adminClient) 
	{
		this.logger=logger;
		this.adminConsole=adminConsole;
		this.adminClient=adminClient;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		ch.closeFuture().addListener(adminClient);
		ch.pipeline().addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
		ch.pipeline().addLast("frameDecoder",new LineBasedFrameDecoder(1024));
		ch.pipeline().addLast(new SessionHandler(logger,adminConsole));
	}
}
