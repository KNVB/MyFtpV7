package com.util;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
/**
 * 
 * @author SITO3
 *
 */
public class ServerBindListener implements ChannelFutureListener 
{
	private Logger logger;
	private MyServer<?> myServer;
	/**
	 * It is triggered when a server bind a socket successfully 
	 * @param logger Message logger
	 */
	public ServerBindListener(Logger logger, MyServer<?> myServer)
	{
		this.logger=logger;
		this.myServer=myServer;
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		InetSocketAddress localAddress=(InetSocketAddress) cf.channel().localAddress();
		if (localAddress==null)
		{
			logger.info("Server bind address failure");  
			myServer.stop();
		}
		else	
		{	
			logger.info("Server listen on "+localAddress.getAddress().getHostAddress()+":"+localAddress.getPort());
		}
	}

}
