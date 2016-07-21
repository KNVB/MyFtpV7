package com.myftpserver;

import com.util.MyServer;
import com.util.ConfigurationFactory;
import com.myftpserver.abstracts.FtpServerConfig;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.channelinitializer.CommandChannelInitializer;

import io.netty.channel.Channel;

import java.io.File;
import java.util.Stack;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
/**
 * 
 * @author SITO3
 *
 */
public class MyFtpServer 
{
	/**
	 * Specify the transfer is send a file to client
	 */
	public static final int SENDFILE=0;
	/**
	 * Specify the transfer is receive a file to client
	 */	
	public static final int RECEIVEFILE=1;
	/**
	 * Specify the transfer is send a file listing to client
	 */
	public static final int SENDDIRLIST=2;

	
	
	private static Logger logger=null;
	private static int connectionCount=0;
	private boolean serverInitOk=false;
	private Stack<Integer> passivePorts;
	private FtpServerConfig serverConfig=null;
	private MyServer<Integer> myServer=null;
//-------------------------------------------------------------------------------------------    
	/**
     * FTP Server object
	 * @throws Exception 
     */
	public MyFtpServer()
	{
		int loadConfigResult=FtpServerConfig.LOAD_FAIL;
		File file = new File("conf/MyFtpServer.xml");
		LoggerContext context =(org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(file.toURI());
		
		logger = LogManager.getLogger(MyFtpServer.class.getName()); 
		logger.debug("Log4j2 is ready.");
		myServer=new MyServer<Integer>(MyServer.ACCEPT_MULTI_CONNECTION,logger);
		myServer.setChildHandlers(new CommandChannelInitializer(this,logger));
		ConfigurationFactory cf=new ConfigurationFactory(logger);
		serverConfig=cf.getServerConfiguration();
		loadConfigResult=serverConfig.load();
		if (loadConfigResult==FtpServerConfig.LOAD_OK)
		{
			logger.info("Server locale="+ serverConfig.getServerLocale());
			logger.info("support passive mode="+serverConfig.isSupportPassiveMode());
			if (serverConfig.isSupportPassiveMode()) 
			{
				if (serverConfig.isPassivePortSpecified())
				{
					passivePorts=serverConfig.passivePorts;
					logger.info("Available passive port:"+passivePorts.toString());
				}
				else
					logger.info("NO passive port is/are specified!!!");
			}
			myServer.setServerPort(serverConfig.getServerPort());
			myServer.setBindAddress(serverConfig.getAllBindAddress());
			serverInitOk=true;
		}
	}
//-------------------------------------------------------------------------------------------	
	/**
	 * Called by CommandChannelClosureListener object when a FTP session is ended.
	 * It reduce the concurrent connection count by 1.
	 */
	public synchronized void sessionClose()
	{
		logger.debug("Before:"+connectionCount);
		connectionCount--;
		logger.info("Concurrent Connection Count:"+connectionCount);
	}		
//-------------------------------------------------------------------------------------------	
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
	 * @return ServerConfiguration object
	 */
	public FtpServerConfig getServerConfig()
	{
		return serverConfig;
	}	
//-------------------------------------------------------------------------------------------	
	/**
	 * It is an API support raw ftp command REIN.<br>
	 * For detail information about REIN command,please refer <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>
	 *  
	 * @param ch The user channel
	 * @param remoteIp The remote user IP address. 
	 */
	public void reinitializeSession(Channel ch,String remoteIp) 
	{
		ch.pipeline().remove("MyHandler");
		ch.pipeline().addLast("MyHandler",new FtpSessionHandler(ch,this,remoteIp));
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
	 * Return port no.&nbsp;to passive port pool
	 * @param port the return passive port 
	 */
	public void returnPassivePort(int port) 
	{
		if (!passivePorts.contains(port))
		{	
			passivePorts.push(port);
			logger.info("Passive Port:"+port+" return");
		}
	}	
//-------------------------------------------------------------------------------------------	
	/**
	 *  start FTP server
	 */
	public void start()
	{
		//String message=serverConfig.getFtpMessage("Server_Started");
		myServer.start();
		//message=message.replace("%1",String.valueOf(serverConfig.getServerPort())); 
        //logger.info(message);
	}	
	/**
	*  stop FTP server
	*/
	public void stop()
	{
		myServer.stop();
		logger.info("FTP Server shutdown gracefully.");
		LoggerContext context = (LoggerContext) LogManager.getContext();
		Configurator.shutdown(context);
	}
//-------------------------------------------------------------------------------------------	
	public static void main(String[] args)  
	{
		MyFtpServer m;
		m = new MyFtpServer();
		if (m.serverInitOk)
			m.start();
	}

}
