package com.myftpserver.channelinitializer;

import org.apache.logging.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.MyFtpServer;
import com.myftpserver.interfaces.SendHandler;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.ReceiveFileHandler;
import com.myftpserver.handler.SendTextFileHandler;
import com.myftpserver.handler.SendBinaryFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
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
public class ActiveChannelInitializer extends ChannelInitializer<Channel> 
{
	private int mode;
	private User user;
	private Logger logger;
	private FtpSessionHandler fs;
	private ChannelHandlerContext ctx;
	private StringBuffer fileNameList;
	/**
	 * Initialize an active mode channel for file transmission
	 * @param fs {@link FtpSessionHandler} FtpSessionHandler object
	 * @param mode Transfer mode (Please refer {@link MyFtpServer#SENDFILE},{@link MyFtpServer#RECEIVEFILE},{@link MyFtpServer#SENDDIRLIST})
	 */
	public ActiveChannelInitializer(ChannelHandlerContext ctx,FtpSessionHandler fs,int mode) 
	{
		this.fs=fs;
		this.ctx=ctx;
		this.mode=mode;
		this.user=fs.getUser();
		this.logger=fs.getLogger();
	}
	/**
	 * Initialize an active mode channel for directory listing transmission
	 * @param fs {@link FtpSessionHandler} FtpSessionHandler object
	 * @param fileNameList  {@link StringBuffer}  A StringBuffer object that contain directory listing 
	 */
	public ActiveChannelInitializer(ChannelHandlerContext ctx,FtpSessionHandler fs,StringBuffer fileNameList) 
	{
		this.fs=fs;
		this.ctx=ctx;
		this.user=fs.getUser();
		this.logger=fs.getLogger();
		this.fileNameList=fileNameList;
		this.mode=MyFtpServer.SENDDIRLIST;
	}	
	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		switch (mode)
		{
			case MyFtpServer.SENDFILE:	if (user.getDownloadSpeedLitmit()==0L)
											logger.info("File download speed is limited by connection speed");
										else
										{
											logger.info("File download speed limit:"+user.getDownloadSpeedLitmit()+" kB/s");
											ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(user.getDownloadSpeedLitmit()*1024,0L));
										}
										SendHandler sendFileHandler;
										if (fs.getDataType().equals("I"))
											sendFileHandler=new SendBinaryFileHandler(fs,ctx);
										else
											sendFileHandler=new SendTextFileHandler(fs,ctx);
										ch.closeFuture().addListener(sendFileHandler);
										ch.pipeline().addLast(sendFileHandler);
										break;
			case MyFtpServer.SENDDIRLIST:SendHandler sendFileNameListHandler=new SendFileNameListHandler(fileNameList, fs,ctx);
										 ch.closeFuture().addListener(sendFileNameListHandler);
										 ch.pipeline().addLast(sendFileNameListHandler);
										 break;
			case MyFtpServer.RECEIVEFILE:ReceiveFileHandler receiveFileHandler=new ReceiveFileHandler(fs,ctx);
										 ch.closeFuture().addListener(receiveFileHandler);
										 ch.pipeline().addLast(receiveFileHandler);
										 break;										 
		}		
	}
}
