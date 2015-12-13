package com.myftpserver;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.util.Utility;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.ReceiveFileHandler;
import com.myftpserver.handler.SendFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;
import com.myftpserver.listener.SendFileCompleteListener;
import com.myftpserver.channelinitializer.PassiveChannelInitializer;

/**
 * 
 * @author SITO3
 * This is passive mode server
 *
 */
public class PassiveServer 
{
	private int port;
	private String host; 
	private Logger logger;
	private Channel ch=null;
	private FtpSessionHandler fs;
	private MyFtpServer myFtpServer;  
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	public PassiveServer(FtpSessionHandler fs,String host, int port)
	{
		this.fs=fs;
		this.port=port;
		this.myFtpServer=fs.getServer();
		this.logger=fs.getConfig().getLogger();
		this.host=host;
	}
	public void sendFileNameList(StringBuffer fileNameList,ChannelHandlerContext responseCtx) 
	{
		InetSocketAddress inSocketAddress=new InetSocketAddress(host,port); 
		try 
        {
            ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup);
            bootStrap.channel(NioServerSocketChannel.class);
            bootStrap.childHandler(new PassiveChannelInitializer(fs,this,responseCtx,fileNameList));
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
	public void sendFile(String serverPath, ChannelHandlerContext responseCtx) throws IOException 
	{
	}
	public void receiveFile(String serverPath, ChannelHandlerContext responseCtx) 
	{
	}
	/*
	public void setChannel(Channel ch) 
	{
		logger.debug("Set Channel is triggered");
		this.ch=ch;
	}*/	

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
