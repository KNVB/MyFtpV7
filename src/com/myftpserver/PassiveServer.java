package com.myftpserver;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import com.myftpserver.channelinitializer.PassiveChannelInitializer;
/**
 * 
 * @author SITO3
 * This is passive mode server
 *
 */
public class PassiveServer 
{
	EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	public PassiveServer(String host, int port,int txMode,String fileName)
	{
		Channel ch=null;
		InetSocketAddress inSocketAddress=new InetSocketAddress(host,port); 
		try 
	        {
	            ServerBootstrap bootStrap = new ServerBootstrap();
	            bootStrap.group(bossGroup, workerGroup);
	            bootStrap.channel(NioServerSocketChannel.class);
	            bootStrap.childHandler(new PassiveChannelInitializer(this,txMode,fileName));
	                     
	            ch=bootStrap.bind(inSocketAddress).sync().channel();
	            System.out.println("Server listening " +host+":" + port);
	            
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
    	System.out.println("Passive Mode Server is shutdown gracefully.");
	}
	public static void main(String[] args) throws Exception 
	{
		//PassiveServer m=new PassiveServer("localhost",1234,MyFtpServer.SENDFILE,"D:\\SITO3\\Documents\\Xmas-20141224-310.jpg");
		//PassiveServer m=new PassiveServer("localhost",1234,MyFtpServer.RECEIVEFILE,"D:\\SITO3\\Desktop\\Xmas-20141224-310.jpg");
	}
}
