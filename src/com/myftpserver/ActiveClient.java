package com.myftpserver;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelHandlerContext;
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
	Logger logger;
	FtpSessionHandler fs;
	ChannelHandlerContext responseCtx;
	/** 
	* This is an active mode client for file transfer and file listing transfer	
	**/
	public ActiveClient(FtpSessionHandler fs, ChannelHandlerContext ctx)
	{
		this.fs=fs;
		logger=fs.getConfig().getLogger();
		this.responseCtx=ctx;
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
            b.handler(new ActiveChannelInitializer(fs,responseCtx,fileNameList));
            b.remoteAddress(new InetSocketAddress(fs.getClientIp(), fs.getClientDataPortNo()));
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        }
        catch (Exception eg)
		{
			eg.printStackTrace();
        	logger.debug(eg.getMessage());
		}
        finally 
        {
        	group.shutdownGracefully().sync();
        	fs.getConfig().getLogger().debug("Active Mode client is shutdown gracefully.");
        }
	}
	/**
	 * Send a file to client
	 * @param fileName the file name to be sent to client 
	 * @throws InterruptedException
	 */
	public void sendFile(String fileName) throws InterruptedException   
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.remoteAddress(new InetSocketAddress(fs.getClientIp(), fs.getClientDataPortNo()));
            b.handler(new ActiveChannelInitializer(fs,responseCtx,MyFtpServer.SENDFILE,fileName));
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
        }
    }
	/**
	 * Receive a file from client
	 * @param fileName the location of the file to be resided.
	 * @throws InterruptedException
	 */
	public void receiveFile(String fileName) throws InterruptedException 
	{
		EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.remoteAddress(new InetSocketAddress(fs.getClientIp(), fs.getClientDataPortNo()));
            b.handler(new ActiveChannelInitializer(fs,responseCtx,MyFtpServer.RECEIVEFILE,fileName));
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
