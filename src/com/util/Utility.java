package com.util;

import org.apache.logging.log4j.Logger;

import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.listener.SendFileListener;
import com.myftpserver.listener.ReceiveFileListener;
import com.myftpserver.listener.SendFileListListener;
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
	public static void sendMessageToClient(Channel ch, Logger logger,String remoteIp,String ftpMessage) 
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(ftpMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new CommandCompleteListener(logger,remoteIp,ftpMessage));
	}
	public static void sendFileListToClient(ChannelHandlerContext responsCtx,FtpSessionHandler fs,StringBuffer resultList) 
	{
		responsCtx.writeAndFlush(Unpooled.copiedBuffer(fs.getFtpMessage("150_Open_Data_Conn")+"\r\n",CharsetUtil.UTF_8)).addListener(new SendFileListListener(responsCtx,fs,resultList));
	}
	public static void sendFileToClient(ChannelHandlerContext responsCtx,FtpSessionHandler fs, String fileName) 
	{
		responsCtx.writeAndFlush(Unpooled.copiedBuffer(fs.getFtpMessage("150_Open_Data_Conn")+"\r\n",CharsetUtil.UTF_8)).addListener(new SendFileListener(responsCtx,fs,fileName));
	}
	public static void receiveFileFromClient(ChannelHandlerContext responsCtx,FtpSessionHandler fs, String fileName) 
	{
		responsCtx.writeAndFlush(Unpooled.copiedBuffer(fs.getFtpMessage("150_Open_Data_Conn")+"\r\n",CharsetUtil.UTF_8)).addListener(new ReceiveFileListener(responsCtx,fs,fileName));
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