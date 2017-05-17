package com.myftpserver.admin.client;

import org.apache.logging.log4j.Logger;

import com.myftpserver.admin.client.handler.AdminClientSessionHandler;
import com.myftpserver.admin.client.listener.AdminChannelCloseListner;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

public class AdminChannelInitializer extends ChannelInitializer<Channel> 
{
	private Logger logger;
	private AdminClient adminClient;
	private String adminUserName = new String();
	private String adminPassword = new String();
	public AdminChannelInitializer(String adminUserName,String adminPassword,Logger logger,AdminClient adminClient) 
	{
		this.logger=logger;
		this.adminClient=adminClient;
		this.adminUserName = adminUserName;
		this.adminPassword = adminPassword;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		AdminClientSessionHandler adminClientSessionHandler=new AdminClientSessionHandler(this.adminUserName,this.adminPassword,this.logger,this.adminClient);
		ch.closeFuture().addListener(new AdminChannelCloseListner(adminClient));
		ch.pipeline().addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
		ch.pipeline().addLast("frameDecoder",new LineBasedFrameDecoder(1024));
		ch.pipeline().addLast("handler",adminClientSessionHandler);
	}
}
