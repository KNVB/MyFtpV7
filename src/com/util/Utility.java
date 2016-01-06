package com.util;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.myftpserver.ActiveClient;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.listener.CloseDataChannel;
import com.myftpserver.listener.SessionClosureListener;
import com.myftpserver.listener.CommandCompleteListener;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
 */
public class Utility
{
	public static void disconnectFromClient(Channel ch, Logger logger,String remoteIp,String goodByeMessage)
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(goodByeMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new SessionClosureListener(null,ch,logger,remoteIp,goodByeMessage));
	}
	public static void sendMessageToClient(Channel ch, Logger logger,String remoteIp,String ftpMessage) 
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(ftpMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new CommandCompleteListener(logger,remoteIp,ftpMessage));
	}
	public static void sendFileListToClient(ChannelHandlerContext responseCtx,FtpSessionHandler fs,StringBuffer resultList) throws InterruptedException 
	{
		Logger logger=fs.getLogger();
		if (fs.isPassiveModeTransfer)
		{
			logger.info("Transfer File in Passive mode");
			PassiveServer ps=fs.getPassiveServer();
			ps.sendFileNameList(resultList, responseCtx);
			sendMessageToClient(responseCtx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("150_Open_Data_Conn"));
		}
		else
		{
			logger.info("Transfer File in Active mode");
			sendMessageToClient(responseCtx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("150_Open_Data_Conn"));
			ActiveClient activeClient=new ActiveClient(fs,responseCtx);
			activeClient.sendFileNameList(resultList);
		}
	}
	public static void sendFileToClient(ChannelHandlerContext responseCtx,FtpSessionHandler fs, String fileName) throws InterruptedException, IOException 
	{
		Logger logger=fs.getLogger();
		if (fs.isPassiveModeTransfer)
		{
			logger.info("Transfer File in Passive mode");
			PassiveServer ps=fs.getPassiveServer();
			ps.sendFile(fileName, responseCtx);
			sendMessageToClient(responseCtx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("150_Open_Data_Conn"));
		}
		else
		{
			logger.info("Transfer File in Active mode");
			sendMessageToClient(responseCtx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("150_Open_Data_Conn"));
			ActiveClient activeClient=new ActiveClient(fs,responseCtx);
			activeClient.sendFile(fileName);
		}
	}
	public static void receiveFileFromClient(ChannelHandlerContext responseCtx,FtpSessionHandler fs, String fileName) throws InterruptedException 
	{
		Logger logger=fs.getLogger();
		if (fs.isPassiveModeTransfer)
		{
			logger.info("Receive File in Passiveive mode");
			PassiveServer ps=fs.getPassiveServer();
			ps.receiveFile(fileName, responseCtx);
			sendMessageToClient(responseCtx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("150_Open_Data_Conn"));
		}
		else
		{
			logger.info("Receive File in Active mode");
			sendMessageToClient(responseCtx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("150_Open_Data_Conn"));
			ActiveClient activeClient=new ActiveClient(fs,responseCtx);
			activeClient.receiveFile(fileName);
		}		
		
	}
	public static void closeDataChannel(ChannelHandlerContext ctx,FtpSessionHandler fs, String message) 
	{
		ctx.writeAndFlush(Unpooled.copiedBuffer(message+"\r\n",CharsetUtil.UTF_8)).addListener(new CloseDataChannel(fs,message));
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