package com.myftpserver;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.channelinitializer.ActiveChannelInitializer;

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
	 * Send a file listing to client
	 * @throws InterruptedException
	 */
	public void sendFileNameList(StringBuffer resultList) 
	{
		EventLoopGroup group = new NioEventLoopGroup();
		try 
		{
			Bootstrap b = new Bootstrap(); 
			b.group(group);
			b.channel(NioSocketChannel.class);
			b.remoteAddress(new InetSocketAddress(fs.getClientIp(), fs.getActiveDataPortNo()));
			b.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK,  1);
	        b.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 1);
	        b.handler(new ActiveChannelInitializer(fs,resultList));
	        b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
	        ChannelFuture f = b.connect().sync();
			f.channel().closeFuture().sync();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS).sync();
	        	logger.debug("Active Mode client is shutdown gracefully.");
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
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
            b.option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK,  1);
	        b.option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 1);
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
        	group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS).sync();
        	logger.info("Active Mode client is shutdown gracefully.");
        	group = null;
        }
    }
	/**
	 * Receive a file from client
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
        	group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS).sync();
        	logger.debug("Active Mode client is shutdown gracefully.");
        }		
	}	
}
