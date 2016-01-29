package com.myftpserver;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.channelinitializer.ActiveChannelInitializer;
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
 */
public class ActiveClient 
{
	private Logger logger;
	private FtpSessionHandler fs;
	/**
	 * This is an active mode client for file transfer and file listing transfer	
	 * @param fs FtpSessionHandler Object
	 */
	public ActiveClient(FtpSessionHandler fs) 
	{
		this.fs=fs;
		this.logger=fs.getLogger();
	}
	/**
	 * Send file listing to client
	 * @param fileNameList A StringBuffer object that contains file listing
	 * @throws InterruptedException
	 */
	public void sendFileNameList(StringBuffer fileNameList) throws InterruptedException
	{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.handler(new ActiveChannelInitializer(fs,fileNameList));
            b.remoteAddress(new InetSocketAddress(fs.getClientIp(), fs.getActiveDataPortNo()));
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        }
        catch (Exception eg)
		{
			eg.printStackTrace();
        	//logger.debug(eg.getMessage());
		}
        finally 
        {
        	group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS).sync();
        	group=null;
        	logger.debug("Active Mode client is shutdown gracefully.");
        }
	}
	/**
	 * Send a file to client
	 * @throws InterruptedException
	 */
	public void sendFile() throws InterruptedException   
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.remoteAddress(new InetSocketAddress(fs.getClientIp(), fs.getActiveDataPortNo()));
            b.handler(new ActiveChannelInitializer(fs,MyFtpServer.SENDFILE));
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        }
        catch (Exception eg)
		{
			eg.printStackTrace();
		}
        finally 
        {
        	group.shutdownGracefully().sync();
        	logger.info("Active Mode client is shutdown gracefully.");
        	group = null;
        }
    }
	/**
	 * Receive a file from client
	 * @param fileName the location of the file to be resided.
	 * @throws InterruptedException
	 */
	public void receiveFile() throws InterruptedException 
	{
		EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.remoteAddress(new InetSocketAddress(fs.getClientIp(), fs.getActiveDataPortNo()));
            b.handler(new ActiveChannelInitializer(fs,MyFtpServer.RECEIVEFILE));
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        }
        catch (Exception eg)
		{
			eg.printStackTrace();
		}
        finally 
        {
        	group.shutdownGracefully().sync();
        	logger.debug("Active Mode client is shutdown gracefully.");
        }		
	}	
}
