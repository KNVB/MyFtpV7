package com.myftpserver;

import org.apache.log4j.Logger;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.channelinitializer.PassiveChannelInitializer;

/**
 * 
 * @author SITO3
 * This is passive mode server
 *
 */
public class PassiveServer 
{
	private Logger logger;
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	public PassiveServer(FtpSessionHandler fs,String host, int port,int txMode,String fileName)
	{
		Channel ch=null;
		this.logger=fs.getConfig().getLogger();
		InetSocketAddress inSocketAddress=new InetSocketAddress(host,port); 
		try 
	        {
	            ServerBootstrap bootStrap = new ServerBootstrap();
	            bootStrap.group(bossGroup, workerGroup);
	            bootStrap.channel(NioServerSocketChannel.class);
	            bootStrap.childHandler(new PassiveChannelInitializer(fs,this,txMode,fileName));
	                     
	            ch=bootStrap.bind(inSocketAddress).sync().channel();
	            logger.info("Server listening " +host+":" + port);
	            
	            // Wait until the server socket is closed.
	            ch.closeFuture().sync();
	        }
		catch (Exception eg)
		{
			eg.printStackTrace();
			stop();
		}
	}
	public void stop()
	{
    	bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
		logger.debug("Passive Mode Server is shutdown gracefully.");
	}
	public static void main(String[] args) throws Exception 
	{
		//PassiveServer m=new PassiveServer("localhost",1234,MyFtpServer.SENDFILE,"D:\\SITO3\\Documents\\Xmas-20141224-310.jpg");
		//PassiveServer m=new PassiveServer("localhost",1234,MyFtpServer.RECEIVEFILE,"D:\\SITO3\\Desktop\\Xmas-20141224-310.jpg");
	}
}
