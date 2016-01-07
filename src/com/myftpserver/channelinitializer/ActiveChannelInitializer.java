package com.myftpserver.channelinitializer;

import com.myftpserver.User;
import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.SendFileHandler;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.ReceiveFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;
import com.myftpserver.listener.ActiveChannelCloseListener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
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
	private String fileName;
	private FtpSessionHandler fs;
	private StringBuffer fileNameList;
	private PassiveServer txServer=null;
	private ChannelHandlerContext responseCtx;
	/**
	 * Initialize an active mode channel for file transmission
	 * @param fs FtpSessionHandler object
	 * @param responseCtx A ChannelHandlerContext for sending file transfer result to client
	 * @param mode Transfer mode
	 * @param fileName The file to be sent to client
	 */
	public ActiveChannelInitializer(FtpSessionHandler fs,ChannelHandlerContext responseCtx, int mode,String fileName) 
	{
		this.fs=fs;
		this.user=fs.getUser();
		this.mode=mode;
		this.fileName=fileName;
		this.responseCtx=responseCtx;
	}
	/**
	 * Initialize an active mode channel for file name list transmission
	 * @param fs FtpSessionHandler object
	 * @param responseCtx A ChannelHandlerContext for sending file name list transfer result to client
	 * @param fileNameList A StringBuffer object that contains file listing
	 */
	public ActiveChannelInitializer(FtpSessionHandler fs,ChannelHandlerContext responseCtx, StringBuffer fileNameList) 
	{
		this.fs=fs;
		this.user=fs.getUser();
		this.mode=MyFtpServer.SENDDIRLIST;
		this.responseCtx=responseCtx;
		this.fileNameList=fileNameList;
	}
	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		ch.closeFuture().addListener(new ActiveChannelCloseListener(fs,this.responseCtx));
		switch (mode)
		{
			case MyFtpServer.SENDFILE:ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(user.getDownloadSpeedLitmit()*1024,0L));
									  ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
									  ch.pipeline().addLast("handler",new SendFileHandler(fileName,fs,responseCtx, txServer));
									  break;
		    case MyFtpServer.RECEIVEFILE:ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(0L,user.getUploadSpeedLitmit()*1024));  
										 ch.pipeline().addLast(new ReceiveFileHandler(fs, this.fileName,responseCtx,null));
											break;
			case MyFtpServer.SENDDIRLIST:ch.pipeline().addLast(new SendFileNameListHandler(fileNameList,responseCtx, fs));
											break;
		}
	}
}
