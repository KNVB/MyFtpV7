package com.myftpserver;
import java.io.IOException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

import org.apache.logging.log4j.Logger;

import com.util.MyServer;
import com.myftpserver.interfaces.SendHandler;
import com.myftpserver.channelinitializer.PassiveChannelInitializer;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.SendTextFileHandler;
import com.myftpserver.handler.SendBinaryFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;
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
 *
 */
public class PassiveServer 
{
	private int port;
	private User user;
	private Channel ch;
	private Logger logger;
	private FtpSessionHandler fs;
	private MyFtpServer myFtpServer;
	private MyServer<Integer> myServer=null;
	/**
	 * This is passive mode server object for provide passive mode transfer
	 * @param fs FTP Session Handler
	 * @param localIP Server IP address
	 * @param port Passive port no.
	 */
	public PassiveServer(FtpSessionHandler fs, String localIP, int port) 
	{
		this.fs=fs;
		this.port=port;
		this.user=fs.getUser();
		this.myFtpServer=fs.getServer();
		this.logger=fs.getLogger();
		fs.setPassiveServer(this);
		myServer=new MyServer<Integer>(MyServer.ACCEPT_SINGLE_CONNECTION,logger);
		myServer.setChildOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK,  1);
		myServer.setChildOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK,  1);
		myServer.setBindAddress(localIP,port);
		myServer.setChildHandlers(new PassiveChannelInitializer(fs));
		myServer.start();
		logger.info("Passive Server listening " +localIP+":" + port);
	}
	/**
	 * Send a file to client
	 */
	public void sendFile() throws IOException 
	{
		if (user.getDownloadSpeedLitmit()==0L)
			logger.info("File download speed is limited by connection speed");
		else
		{
			logger.info("File download speed limit:"+user.getDownloadSpeedLitmit()+" kB/s");
			ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(user.getDownloadSpeedLitmit()*1024,0L));
		}
		SendHandler sendFileHandler;
		if (fs.getDataType().equals("I"))
			sendFileHandler=new SendBinaryFileHandler(fs);
		else
			sendFileHandler=new SendTextFileHandler(fs);
		ch.closeFuture().addListener(sendFileHandler);
		ch.pipeline().addLast(sendFileHandler);
	}
	/**
	 * Send a file name list to client
	 * @param fileNameList A StringBuffer object that contains file listing
	 */
	public void sendFileNameList(StringBuffer fileNameList) 
	{
		ch.pipeline().remove("ReceiveHandler");
		SendHandler sendFileNameListHandler=new SendFileNameListHandler(fileNameList, fs);
		
		ch.closeFuture().addListener(sendFileNameListHandler);
		ch.pipeline().addLast(sendFileNameListHandler);
	}
	/**
	 * Set a channel for passive mode
	 * @param ch a channel for passive mode
	 */
	public void setChannel(Channel ch) 
	{
		logger.debug("Set Channel is triggered");
		this.ch=ch;
	}	
	/**
	 * Stop the passive server and return passive port to passive port pool 
	 */
	public void stop()
	{
		myServer.stop();
		myFtpServer.returnPassivePort(port);
		logger.info("Passive Mode Server is shutdown gracefully.");
		
	}	
}
