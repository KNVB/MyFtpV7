package com.util;

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
	private int serverPort=21;
	private String[] bindAddress= new String[]{};
	private EventLoopGroup bossGroup=null;
    private EventLoopGroup workerGroup=null;
	private ServerBootstrap bootStrap = null;
//-------------------------------------------------------------------------------------------   
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
	public void setServerPort(int port)
	{
		serverPort=port;
	}
	public void setBindAddress(String[] bindAddress)
	{
		this.bindAddress=bindAddress; 
	}
	public void setChildOption(ChannelOption<T> childOption, T value)
	{
		bootStrap.childOption(childOption, value);
	}
	public void setChildHandlers(ChannelHandler  ci)
	{
		bootStrap.childHandler(ci);
	}
	public void start()
	{
		bootStrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		//if no binding address is specified, bind "Wildcard" IP address
		if (bindAddress.length==0) 
		{
			bootStrap.bind(serverPort);
		}
		else
		{
			for (String address:bindAddress)
			{
				bootStrap.bind(address,serverPort);
			}
		}
	}
	public void stop()
	{
		if (bossGroup!=null)
		{	
			bossGroup.shutdownGracefully();
		}
		if (workerGroup!=null)
		{	
			workerGroup.shutdownGracefully();
		}
        bossGroup=null;
        workerGroup=null;
        bootStrap = null;
        logger.info("Server shutdown gracefully.");		
	}
}
