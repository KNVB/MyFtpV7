package com.myftpserver.admin.client;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
public class AdminClient 
{
	private Logger logger;
	private int adminServerPort;
	private AdminConsole adminConsole;
	private String adminUserName = new String();
	private String adminPassword = new String();
	private String adminServerName = new String();
	private EventLoopGroup group = new NioEventLoopGroup();
	public AdminClient(String adminServerName,int adminServerPort,String adminUserName,String adminPassword,AdminConsole adminConsole,Logger logger)
	{
		this.adminServerName = adminServerName;
		this.adminServerPort = adminServerPort;
		this.adminUserName = adminUserName;
		this.adminPassword = adminPassword;
		this.adminConsole=adminConsole;
		this.logger=logger;
	}
	public void connect() throws Exception 
	{
		Bootstrap b = new Bootstrap(); 
		b.group(group);
		b.channel(NioSocketChannel.class);
		b.handler(new AdminChannelInitializer(this.adminUserName,this.adminPassword,logger,this));
		b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		b.connect(adminServerName ,adminServerPort).sync();
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
