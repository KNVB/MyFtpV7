package com.myftpserver.abstracts;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;


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
	public static final int BLOCKED_IO_TYPE=1;
	public static final int NON_BLOCKED_IO_TYPE=0;
	public String[] bindAddress= new String[]{};
	
	private int serverPort=-1;
	private EventLoopGroup bossGroup=null;
    private EventLoopGroup workerGroup=null;
	private int serverIOType=NON_BLOCKED_IO_TYPE;
	private int connectionAcceptanceType=ACCEPT_MULTI_CONNECTION;
	private Logger logger;
    private ServerBootstrap bootStrap = null;
//-------------------------------------------------------------------------------------------   
	/**
	 * It is a standard server object
	 */
	public MyServer(Logger logger) 
	{
		bootStrap = new ServerBootstrap();		
		bootStrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		this.logger=logger;
	}
	/**
	 * Set the server connection acceptance type  
	 * e.g.
	 * ACCEPT_MULTI_CONNECTION or
	 * ACCEPT_SINGLE_CONNECTION
	 * Default value is ACCEPT_MULTI_CONNECTION
	 * @param serverType
	 */
	public void setConnectionAcceptanceType(int connectionAcceptanceType)
	{
		this.connectionAcceptanceType=connectionAcceptanceType;
	}
	/**
	 * Set the server IO type
	 * E.g.
	 * NON_BLOCKED_IO_TYPE or
	 * BLOCKED_IO_TYPE
	 * Default value is NON_BLOCKED_IO_TYPE
	 * @param serverIOType
	 * 	 
	 */
	public void setServerIOType(int serverIOType)
	{
		this.serverIOType=serverIOType;
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
	 * Set the child handler
	 * @param ci child handler
	 */
	public void setChildHandlers(ChannelHandler  ci)
	{
		bootStrap.childHandler(ci);
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
	 * Start the server
	 */
	public void start(ChannelFutureListener bindListener) throws IllegalArgumentException  
	{
		if (serverIOType==NON_BLOCKED_IO_TYPE)
		{
			logger.debug("Server IO Type=NON_BLOCKED_IO_TYPE");
			workerGroup=new NioEventLoopGroup();
			bossGroup=new NioEventLoopGroup();
			bootStrap.channel(NioServerSocketChannel.class);
		}		
		else
		{
			logger.debug("Server IO Type=BLOCKED_IO_TYPE");
			bossGroup=new OioEventLoopGroup();
			workerGroup=new OioEventLoopGroup();
			bootStrap.channel(OioServerSocketChannel.class);
		}
		if (connectionAcceptanceType==ACCEPT_MULTI_CONNECTION)
		{
			bootStrap.group(bossGroup, workerGroup);
			logger.debug("Connection Acceptance Type=ACCEPT_MULTI_CONNECTION");
		}
		else
		{
			bootStrap.group(workerGroup);
			bossGroup.shutdownGracefully(0,0,TimeUnit.MILLISECONDS);
			bossGroup=null;
			logger.debug("Connection Acceptance Type=ACCEPT_SINGLE_CONNECTION");
		}
		/*
		 * if no binding address is specified, bind "Wildcard" IP address.
		 * https://docs.oracle.com/javase/7/docs/api/java/net/InetSocketAddress.html
		 */
		if (bindAddress.length==0) 
		{
			bootStrap.bind(serverPort).addListener(bindListener);
		}
		else
		{
			/*
			 * Looping through binding address array, bind it one by one.
			 */
			for (String address:bindAddress)
			{
				bootStrap.bind(address,serverPort).addListener(bindListener);
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
	}	
}