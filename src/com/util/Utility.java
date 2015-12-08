package com.util;

import org.apache.log4j.Logger;

import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.listener.SessionClosureListener;
import com.myftpserver.listener.CommandCompleteListener;
import com.myftpserver.listener.SendFileNameListCompleteListener;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class Utility
{
	public static void sendMessageToClient(Channel ch, Logger logger,String remoteIp,String ftpMessage) 
	{
		// TODO Auto-generated method stub
		ch.writeAndFlush(Unpooled.copiedBuffer(ftpMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new CommandCompleteListener(logger,remoteIp,ftpMessage));
	}
	public static void sendFileNameList(Channel ch,ChannelHandlerContext responseCtx,StringBuilder fileNameList,FtpSessionHandler fs,PassiveServer passiveServer)
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(fileNameList.toString(),CharsetUtil.UTF_8)).addListener(new SendFileNameListCompleteListener(fs,responseCtx,passiveServer));
	}		
	public static void disconnectFromClient(FtpSessionHandler fs, Logger logger,String remoteIp,String goodByeMessage)
	{
		fs.getChannel().writeAndFlush(Unpooled.copiedBuffer(goodByeMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new SessionClosureListener(fs,null,logger,remoteIp,goodByeMessage));
	}
	public static void disconnectFromClient(Channel ch, Logger logger,String remoteIp,String goodByeMessage)
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(goodByeMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new SessionClosureListener(null,ch,logger,remoteIp,goodByeMessage));
	}	
	public static final String getSystemType(Logger logger)
	{
		 String loc = System.getProperty("user.timezone");
	        final int p = loc.indexOf("/");
	        if (p > 0) {
	            loc = loc.substring(0, p);
	        }
	        loc = loc + "/"+ System.getProperty("user.language");
	        String result=System.getProperty("os.arch") + " "
	                + System.getProperty("os.name") + " "
	                + System.getProperty("os.version") + ", " + loc;
	        logger.debug("System type="+result);
	        return result;
	}
}