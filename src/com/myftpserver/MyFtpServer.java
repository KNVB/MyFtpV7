package com.myftpserver;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.File;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
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
public final class MyFtpServer 
{
	private static Logger logger=null;
	private Stack<Integer> passivePorts;
	private static int connectionCount=0;
	private ServerConfig serverConfig=null;
	
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
//-------------------------------------------------------------------------------------------    
	/**
     * FTP Server object
     */
	public MyFtpServer()
	{
		File file = new File("conf/MyFtpServer.xml");
		LoggerContext context =(org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(file.toURI());
		logger = LogManager.getLogger(this.getClass()); 
		logger.debug("Log4j is ready.");
		ServerConfig serverConfig=new ServerConfig(logger);
		if (serverConfig.load(this))
		{	
			logger.debug("Server Configuration is loaded successfully.");
			if ((serverConfig.isSupportPassiveMode()) && (serverConfig.isPassivePortSpecified()))
			{
				passivePorts=serverConfig.passivePorts;
				//logger.debug(passivePorts==null);
				logger.info("Available passive port:"+passivePorts.toString());
			}
		}
		else
			logger.debug("Server Configuration cannot be loaded");
	}
//-------------------------------------------------------------------------------------------	
	/**
	 * Called by FtpSessionHandler object when a FTP session is ended.
	 */
	public synchronized void sessionClose()
	{
		logger.debug("Before:"+connectionCount);
		connectionCount--;
		logger.info("Concurrent Connection Count:"+connectionCount);
	}	
//-------------------------------------------------------------------------------------------	
	/**
	 *  start FTP server
	 */
	public void start()
	{
		
	}
//-------------------------------------------------------------------------------------------	
	/**
	*  stop FTP server
	*/
	public void stop()
	{
		
		//bossGroup.shutdownGracefully();
        //workerGroup.shutdownGracefully();
        logger.info("Server shutdown gracefully.");
		LoggerContext context = (LoggerContext) LogManager.getContext();
		Configurator.shutdown(context);
	}
//-------------------------------------------------------------------------------------------
	public static void main(String[] args) 
	{
		MyFtpServer m=new MyFtpServer();
		m.start();	
	}	
}
