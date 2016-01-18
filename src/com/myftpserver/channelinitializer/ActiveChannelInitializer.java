package com.myftpserver.channelinitializer;

import org.apache.logging.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.MyFtpServer;
import com.myftpserver.handler.SendFileHandler;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.SendFileNameListHandler;
import com.myftpserver.listener.ActiveChannelCloseListener;
import com.myftpserver.handler.ActiveModeReceiveFileHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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
	/**
	 * Initialize an active mode channel for file transmission
	 * @param fs FtpSessionHandler object
	 * @param mode Transfer mode
	 * @param fileName The file to be sent to client
	 */
	public ActiveChannelInitializer(FtpSessionHandler fs,int mode,String fileName) 
	{
		this.fs=fs;
		this.user=fs.getUser();
		this.mode=mode;
		this.fileName=fileName;
	}
	/**
	 * Initialize an active mode channel for file name list transmission
	 * @param fs FtpSessionHandler object
	 * @param fileNameList A StringBuffer object that contains file listing
	 */
	public ActiveChannelInitializer(FtpSessionHandler fs, StringBuffer fileNameList) 
	{
		this.fs=fs;
		this.user=fs.getUser();
		this.mode=MyFtpServer.SENDDIRLIST;
		this.fileNameList=fileNameList;
	}
	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		Logger logger=fs.getLogger();
		ch.closeFuture().addListener(new ActiveChannelCloseListener(fs));
		switch (mode)
		{
			case MyFtpServer.SENDFILE:
										if (user.getDownloadSpeedLitmit()==0L)
											logger.info("File download speed is limited by connection speed");
										else
										{
											logger.info("File download speed limit:"+user.getDownloadSpeedLitmit()+" kB/s");
											ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(user.getDownloadSpeedLitmit()*1024,0L));
										}
										ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
										ch.pipeline().addLast("handler",new SendFileHandler(fileName,fs));
										break;
		    case MyFtpServer.RECEIVEFILE:
										if (user.getUploadSpeedLitmit()==0L)
											logger.info("File upload speed is limited by connection speed");
										else
										{	
											ch.pipeline().addFirst("TrafficShapingHandler",new ChannelTrafficShapingHandler(0L,user.getUploadSpeedLitmit()*1024));
										logger.info("File upload speed limit:"+user.getUploadSpeedLitmit()+" kB/s");
										}
										ch.pipeline().addLast(new ActiveModeReceiveFileHandler(fs,fileName));
										break;
			case MyFtpServer.SENDDIRLIST:ch.pipeline().addLast(new SendFileNameListHandler(fileNameList, fs));
											break;
		}
	}
}
