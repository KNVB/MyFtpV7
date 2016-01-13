package com.myftpserver;

import org.apache.logging.log4j.Logger;


import java.net.InetSocketAddress;


import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.myftpserver.handler.FtpSessionHandler;
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
	public PassiveServer(FtpSessionHandler fs)
	{
		this.fs=fs;
		this.port=fs.getPassivePort();
		this.myFtpServer=fs.getServer();
		this.logger=fs.getLogger();
	}
	public void sendFileNameList(StringBuffer resultList,ChannelHandlerContext responseCtx) 
	{
		String host=((java.net.InetSocketAddress)responseCtx.channel().localAddress()).getAddress().getHostAddress();
		InetSocketAddress inSocketAddress=new InetSocketAddress(host,port); 
		try 
        {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup);
            bootStrap.channel(NioServerSocketChannel.class);
            bootStrap.childHandler(new PassiveChannelInitializer(fs,responseCtx,this,resultList));
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
	public void sendFile(String fileName, ChannelHandlerContext responseCtx) 
	{
		String host=((java.net.InetSocketAddress)responseCtx.channel().localAddress()).getAddress().getHostAddress();
		InetSocketAddress inSocketAddress=new InetSocketAddress(host,port); 
		try 
        {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup);
            bootStrap.channel(NioServerSocketChannel.class);
            bootStrap.childHandler(new PassiveChannelInitializer(fs,responseCtx,this,MyFtpServer.SENDFILE, fileName));
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
	public void receiveFile(String fileName, ChannelHandlerContext responseCtx) 
	{
		String host=((java.net.InetSocketAddress)responseCtx.channel().localAddress()).getAddress().getHostAddress();
		InetSocketAddress inSocketAddress=new InetSocketAddress(host,port); 
		try 
        {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup);
            bootStrap.channel(NioServerSocketChannel.class);
            bootStrap.childHandler(new PassiveChannelInitializer(fs,responseCtx,this,MyFtpServer.RECEIVEFILE, fileName));
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
	 * Stop the passive server and return passive port to passive port pool 
	 */
	public void stop()
	{
    	bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		bossGroup=null;
		workerGroup=null;
		logger.info("Passive Mode Server is shutdown gracefully.");
		myFtpServer.returnPassivePort(port);
	}
	/*public static void main(String[] args) throws Exception 
	{
		//PassiveServer m=new PassiveServer("localhost",1234,MyFtpServer.SENDFILE,"D:\\SITO3\\Documents\\Xmas-20141224-310.jpg");
		//PassiveServer m=new PassiveServer("localhost",1234,MyFtpServer.RECEIVEFILE,"D:\\SITO3\\Desktop\\Xmas-20141224-310.jpg");
	}*/
}
