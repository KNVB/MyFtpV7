package com.myftpserver;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.channelinitializer.ActiveChannelInitializer;

/**
 * 
 * @author SITO3
 * This is active mode client
 */
public class ActiveClient 
{
	FtpSessionHandler fs;
	ChannelHandlerContext responseCtx;
	public ActiveClient(FtpSessionHandler fs, ChannelHandlerContext ctx)
	{
		this.fs=fs;
		this.responseCtx=ctx;
	}
	public void sendFileNameList(StringBuilder fileNameList) throws InterruptedException
	{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class);
            b.remoteAddress(new InetSocketAddress(fs.getClientIp(), fs.getClientDataPortNo()));
            b.handler(new ActiveChannelInitializer(fs,responseCtx,fileNameList));
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
        	fs.getConfig().getLogger().debug("Active Mode client is shutdown gracefully.");
        }
	}

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
        	System.out.println("Active Mode client is shutdown gracefully.");
        }
    }
}
