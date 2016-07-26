package com.util;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.logging.log4j.Logger;

/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class MyServer<T> 
{
	public static final int ACCEPT_MULTI_CONNECTION=1;
	public static final int ACCEPT_SINGLE_CONNECTION=0;
	private Logger logger=null;
	private int serverPort=-1,bindSuccessCount=0;
	private String[] bindAddress= new String[]{};
	private EventLoopGroup bossGroup=null;
    private EventLoopGroup workerGroup=null;
	private ServerBootstrap bootStrap = null;
//-------------------------------------------------------------------------------------------   
	/**
	 * It is a standard server object
	 * @param serverType  
	 * @param logger message logger
	 */
	public MyServer(int serverType,Logger logger)
	{
		bootStrap = new ServerBootstrap();
		workerGroup=new NioEventLoopGroup();
		this.logger=logger;
		if (serverType==ACCEPT_MULTI_CONNECTION)
		{
			bossGroup=new NioEventLoopGroup();
			bootStrap.group(bossGroup, workerGroup);
		}
		else
			bootStrap.group(workerGroup);
        bootStrap.channel(NioServerSocketChannel.class);
	}
	/**
	 * Server listening port
	 * @param port no. that server to listen
	 */
	public void setServerPort(int port)
	{
		serverPort=port;
	}
	/**
	 * Set serve binding address
	 * @param bindAddress
	 */
	public void setBindAddress(String[] bindAddress)
	{
		this.bindAddress=bindAddress; 
	}
	/**
	 * Set the child option
	 * @param childOption
	 * @param value
	 */
	public void setChildOption(ChannelOption<T> childOption, T value)
	{
		bootStrap.childOption(childOption, value);
	}
	/**
	 * Set the child handler
	 * @param ci child handler
	 */
	public void setChildHandlers(ChannelHandler  ci)
	{
		bootStrap.childHandler(ci);
	}
	synchronized protected void setBindStatus(ChannelFuture cf) 
	{
		if (cf.isSuccess())		
		{	
			bindSuccessCount++;
			InetSocketAddress localAddress=(InetSocketAddress) cf.channel().localAddress();
			logger.info("Server listen on "+localAddress.getAddress().getHostAddress()+":"+localAddress.getPort());
			if (bindSuccessCount>=bindAddress.length)
			{
				logger.info("Server started");
			}
		}
		else
		{
			logger.info("Server bind address failure:"+cf.cause().getMessage());
			this.stop();
		}
	}
	/**
	 * Start the server
	 */
	public void start() throws IllegalArgumentException  
	{
		ServerBindListener serverBindListener=new ServerBindListener(logger,this);
		bootStrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		/*
		 * if no binding address is specified, bind "Wildcard" IP address.
		 * https://docs.oracle.com/javase/7/docs/api/java/net/InetSocketAddress.html
		 */
		if (bindAddress.length==0) 
		{
			bootStrap.bind(serverPort).addListener(serverBindListener);
		}
		else
		{
			/*
			 * Looping through binding address array, bind it one by one.
			 */
			for (String address:bindAddress)
			{
				bootStrap.bind(address,serverPort).addListener(serverBindListener);
			}
		}
	}
	/**
	 * Stop the server
	 */
	public void stop()
	{
		if (bossGroup!=null)
		{	
			bossGroup.shutdownGracefully(0,0,TimeUnit.MILLISECONDS);
		}
		if (workerGroup!=null)
		{	
			workerGroup.shutdownGracefully(0,0,TimeUnit.MILLISECONDS);
		}
        bossGroup=null;
        workerGroup=null;
        bootStrap = null;
        logger.debug("Server shutdown gracefully.");		
	}	
}
