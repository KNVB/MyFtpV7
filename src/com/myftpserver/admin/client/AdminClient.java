package com.myftpserver.admin.client;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import com.myftpserver.admin.client.handler.AdminClientSessionHandler;
import com.myftpserver.admin.object.AdminUser;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioSocketChannel;

public class AdminClient 
{
	private AdminClientSessionHandler handler;
	private AdminConsole adminConsole;
	private Channel ch;
	private EventLoopGroup group = new OioEventLoopGroup();
	private int adminServerPort;
	private Logger logger;
	private String adminServerName = new String();
	public AdminClient(String adminServerName,int adminServerPort,AdminConsole adminConsole,Logger logger)
	{
		this.adminServerName = adminServerName;
		this.adminServerPort = adminServerPort;
		this.adminConsole=adminConsole;
		this.logger=logger;
	}
	public void connect() throws Exception 
	{
		Bootstrap b = new Bootstrap(); 
		b.group(group);
		b.channel(OioSocketChannel.class);
		b.handler(new AdminChannelInitializer(logger,this));
		b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		ch=b.connect(adminServerName ,adminServerPort).sync().channel();
		handler = ch.pipeline().get(AdminClientSessionHandler.class);
	}
	public void login(String adminUserName,String adminPassword) throws Exception
	{
		handler.sendRequest(new AdminUser(adminUserName,adminPassword,"login"));
	}
	public void shutdown() 
	{
		try 
		{
			group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS);
			logger.info("Admin. client is shutdown gracefully.");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}		
	}
	public void showErrorMessage(String message) 
	{
		adminConsole.showErrorMessage(message); 
	}
	public void channelClosed() 
	{
		logger.info("Admin. channel is closed.");
	}	
}
