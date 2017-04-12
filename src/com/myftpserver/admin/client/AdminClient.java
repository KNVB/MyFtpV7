package com.myftpserver.admin.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

public class AdminClient 
{
	private Logger logger;
	private EventLoopGroup group = new NioEventLoopGroup();
	public AdminClient(String remoteHost,int portNo,Logger logger) throws InterruptedException
	{
		
		//try 
		{
			this.logger=logger;
			Bootstrap b = new Bootstrap(); 
			b.group(group);
			b.channel(NioSocketChannel.class);
			b.handler(new AdminChannelInitializer(logger));
			b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		    ChannelFuture f = b.connect(remoteHost,portNo).sync();
			//f.channel().closeFuture().sync();
		}
		/*finally 
		{
			try 
			{
				group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS).sync();
	        	this.logger.debug("Admin. client is shutdown gracefully.");
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}*/
	}
	public void disconnect()
	{
		try 
		{
			group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS).sync();
        	this.logger.debug("Admin. client is shutdown gracefully.");
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
}
