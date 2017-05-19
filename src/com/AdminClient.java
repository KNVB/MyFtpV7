package com;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import org.apache.logging.log4j.Logger;

public class AdminClient 
{
	Logger logger;
	AdminClientSessionHandler adminClientSessionHandler=null;
	EventLoopGroup group = new NioEventLoopGroup();
	public AdminClient(Logger logger) 
	{
		this.logger=logger;
	}

	public void connect(String adminServerName, int adminServerPortNo) throws InterruptedException 
	{
		 try {
	            Bootstrap b = new Bootstrap();
	            b.group(group)
	             .channel(NioSocketChannel.class)
	             .handler(new ChannelInitializer<SocketChannel>() {
	                @Override
	                public void initChannel(SocketChannel ch) throws Exception {
	                    ChannelPipeline p = ch.pipeline();
	                  
	                    p.addLast(
	                            new ObjectEncoder(),
	                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
	                            new AdminClientSessionHandler(logger)
	                           );
	                }
	             });
	            
	            // Start the connection attempt.
	            Channel ch =b.connect(adminServerName, adminServerPortNo).sync().channel();
	            adminClientSessionHandler=ch.pipeline().get(AdminClientSessionHandler.class);
	            
	        } finally {
	            group.shutdownGracefully();
	        }
		
	}

	public void login(String adminUserName, String adminPassword) throws Exception 
	{
		User user=new User();
		user.setName(adminUserName);
		user.setPassword(adminPassword);
		adminClientSessionHandler.login(user);
	}

	public void shutdown() 
	{
		// TODO Auto-generated method stub
		
	}
}
