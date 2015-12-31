package com.myftpserver;

import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.myftpserver.User;
//import com.myftpserver.handler.SendFileHandler;
import com.myftpserver.handler.FtpSessionHandler;
//import com.myftpserver.handler.ReceiveFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;
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
	 * @param host Server IP address
	 * @param port Passive port no.
	 */
	public PassiveServer(FtpSessionHandler fs,String host, int port)
	{
		this.fs=fs;
		this.port=port;
		this.user=fs.getUser();
		this.myFtpServer=fs.getServer();
		this.logger=fs.getLogger();
		InetSocketAddress inSocketAddress=new InetSocketAddress(host,port); 
		try 
        {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup);
            bootStrap.channel(NioServerSocketChannel.class);
            bootStrap.childHandler(new PassiveChannelInitializer(fs,this));
            bootStrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootStrap.bind(inSocketAddress);
            logger.info("Passive Server listening " +host+":" + port);
            
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
	 * Send a file name list to client
	 * @param fileNameList A StringBuffer object that contains file listing
	 * @param responseCtx A ChannelHandlerContext for sending file name list transfer result to client 
	 */
	public void sendFileNameList(StringBuffer fileNameList,ChannelHandlerContext responseCtx) 
	{
		ch.pipeline().addLast(new SendFileNameListHandler(fileNameList,responseCtx, fs,this));
	}
	/**
	 * Send a file to client
	 * @param serverPath A file to be sent to client 
	 * @param responseCtx A ChannelHandlerContext for sending file transfer result to client
	 */
	/*public void sendFile(String serverPath, ChannelHandlerContext responseCtx) throws IOException 
	{
		ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(user.getDownloadSpeedLitmit()*1024,0L));
		ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
		ch.pipeline().addLast("handler",new SendFileHandler(serverPath,fs,responseCtx, this));
	}*/
	/**
	 * Receive a file from client
	 * @param serverPath the location of the file to be resided.
	 * @param responseCtx A ChannelHandlerContext for sending file receive result to client
	 */
	/*public void receiveFile(String serverPath, ChannelHandlerContext responseCtx) 
	{
		ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(0L,user.getUploadSpeedLitmit()*1024));
		ch.pipeline().addLast(new ReceiveFileHandler(fs, serverPath,responseCtx,this));
	}*/
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
		logger.debug("Passive Mode Server is shutdown gracefully.");
		myFtpServer.returnPassivePort(port);
	}
	/*public static void main(String[] args) throws Exception 
	{
		//PassiveServer m=new PassiveServer("localhost",1234,MyFtpServer.SENDFILE,"D:\\SITO3\\Documents\\Xmas-20141224-310.jpg");
		//PassiveServer m=new PassiveServer("localhost",1234,MyFtpServer.RECEIVEFILE,"D:\\SITO3\\Desktop\\Xmas-20141224-310.jpg");
	}*/	
}
