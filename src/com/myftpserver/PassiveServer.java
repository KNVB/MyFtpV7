package com.myftpserver;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.myftpserver.User;
import com.myftpserver.interfaces.SendHandler;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.SendBinaryFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;
import com.myftpserver.handler.SendTextFileHandler;
import com.myftpserver.channelinitializer.PassiveChannelInitializer;
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
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
	/**
	 * This is passive mode server
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
		InetSocketAddress inSocketAddress=new InetSocketAddress(localIP,port); 
		try 
        {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup);
            bootStrap.channel(NioServerSocketChannel.class);
            bootStrap.childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK,  1);
            bootStrap.childOption(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 1);
            bootStrap.childHandler(new PassiveChannelInitializer(fs));
            bootStrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootStrap.bind(inSocketAddress);
            logger.info("Passive Server listening " +localIP+":" + port);
            
            // Wait until the server socket is closed.
            //ch.closeFuture().sync();
        }
		catch (Exception eg)
		{
			eg.printStackTrace();
			stop();
		}
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
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		myFtpServer.returnPassivePort(port);
		bossGroup=null;
		workerGroup=null;
		logger.info("Passive Mode Server is shutdown gracefully.");
		
	}	
}

