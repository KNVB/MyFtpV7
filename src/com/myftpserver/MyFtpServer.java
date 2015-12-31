package com.myftpserver;

import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.File;
import java.util.Stack;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import com.myftpserver.channelinitializer.CommandChannelInitializer;
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
	
	private EventLoopGroup bossGroup=new NioEventLoopGroup();
    private EventLoopGroup workerGroup=new NioEventLoopGroup(); 
//-------------------------------------------------------------------------------------------    
	/**
     * FTP Server object
     */
	public MyFtpServer()
	{
		File file = new File("conf/MyFtpServer.xml");
		LoggerContext context =(org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(file.toURI());
		
		logger = LogManager.getLogger(MyFtpServer.class.getName()); 
		logger.debug("Log4j2 is ready.");
		serverConfig=new ServerConfig(logger);
		if (serverConfig.load(this))
		{	
			logger.debug("Server Configuration is loaded successfully.");
			if ((serverConfig.isSupportPassiveMode()) && (serverConfig.isPassivePortSpecified()))
			{
				passivePorts=serverConfig.passivePorts;
				logger.info("Available passive port:"+passivePorts.toString());
			}
		}
		else
			logger.debug("Server Configuration cannot be loaded");
	}
	//-------------------------------------------------------------------------------------------	
    /**
	 * Get message logger
	 * @return message logger 
	 */
    public Logger getLogger() 
	{
		return logger;
	}
//-------------------------------------------------------------------------------------------
	/**
	 * Get Configuration object
	 * @return Configuration object
	 */
	public ServerConfig getServerConfig()
	{
		return serverConfig;
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
	/**
	 * Check whether the concurrent connection is over the limit
	 * @return true when the concurrent connection is over the limit
	 */
	public synchronized boolean isOverConnectionLimit()
	{
		if (connectionCount<serverConfig.getMaxConnection())
		{	
			connectionCount++;
			return false;
		}
		else			
			return true;
	}
//-------------------------------------------------------------------------------------------	
	/**
	 * Get next available passive port for data transfer
	 * @return port no.<br>
	 *         -1 when no. passive port is available.
	 */
	public synchronized int getNextPassivePort()
	{
		int nextPassivePort=-1;
		if (serverConfig.isSupportPassiveMode())
		{
			if (serverConfig.isPassivePortSpecified())
			{
				if (passivePorts.size()>0)
					nextPassivePort=passivePorts.pop();
			}
		}
		return nextPassivePort;
	}
//-------------------------------------------------------------------------------------------	
	/**
	 * Return port no. to passive port pool
	 * @param port the return passive port 
	 */
	public void returnPassivePort(int port) 
	{
		if (!passivePorts.contains(port))
		{	
			passivePorts.push(port);
			logger.debug("Passive Port:"+port+" return");
		}
	}
//-------------------------------------------------------------------------------------------	
	/**
	 *  start FTP server
	 */
	public void start()
	{
		try 
        {
			ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup);
            bootStrap.localAddress(serverConfig.getServerPort());
            bootStrap.channel(NioServerSocketChannel.class);
            bootStrap.childHandler(new CommandChannelInitializer(this,logger));
            bootStrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            logger.info("My FTP Server is started.");
            // Wait until the server socket is closed.
            bootStrap.bind();
        } 
        catch (Exception e) 
        {
			e.printStackTrace();
			this.stop();
		}	        
	}
//-------------------------------------------------------------------------------------------	
	/**
	*  stop FTP server
	*/
	public void stop()
	{
		bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        bossGroup=null;
        workerGroup=null;
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
