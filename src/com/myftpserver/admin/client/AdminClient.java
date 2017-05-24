package com.myftpserver.admin.client;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.myftpserver.admin.client.tx.AdminClientChannelInitializer;
import com.myftpserver.admin.client.tx.AdminClientSessionHandler;
import com.myftpserver.admin.object.AdminUser;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioSocketChannel;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

public class AdminClient
{
	private Logger logger;
	private EventLoopGroup group = new OioEventLoopGroup();
	private Bootstrap b = new Bootstrap(); 
	private Channel ch;
	public AdminClient(Logger logger)
	{
		this.logger=logger;	
	}
	public void connect(String adminServerName, int portNo) throws InterruptedException 
	{
		b.group(group);
		b.channel(OioSocketChannel.class);
		b.handler(new AdminClientChannelInitializer(logger));
		b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		ch=b.connect(adminServerName,portNo).sync().channel();
	}
	public void login(AdminUser adminUser) throws JsonProcessingException,Exception 
	{
		AdminClientSessionHandler adminClientSessionHandler=ch.pipeline().get(AdminClientSessionHandler.class);
		adminClientSessionHandler.login(adminUser);
	}
	public void shutdown()
	{
		logger.debug("Shutdown client");
		group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS);
	}
	/*public static void main(String[] args) throws InterruptedException  
	{
		int portNo=4466;
		String adminServerName="localhost";
		User user=new User();
		user.setName("���j��");
		user.setPassword("�K�X");
		Logger logger = LogManager.getLogger(AdminClient.class.getName());
		AdminClient adminClient=new AdminClient(logger);
		adminClient.connect(adminServerName,portNo);
		Thread.sleep(3000);
		adminClient.login(user);
		user.setName("Peter");
		user.setPassword("paul");
		Thread.sleep(3000);
		adminClient.login(user);
		//adminClient.shutdown();
	}*/
}

