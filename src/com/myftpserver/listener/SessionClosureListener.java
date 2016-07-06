package com.myftpserver.listener;

import com.myftpserver.handler.FtpSessionHandler;

import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
public class SessionClosureListener implements ChannelFutureListener  
{
	private Channel ch; 
	private Logger logger=null;
	private FtpSessionHandler fs=null;
	private String remoteIp=new String(),ftpMessage=new String();
	/**
	 * It is triggered when an user close connection<br> 
	 * It will sent a good bye message to client and then close the connection 
	 * @param fs FTP session
	 * @param ch The channel that the FTP session resided
	 * @param logger Message logger
	 * @param remoteIp Client IP Address
	 * @param goodByeMsg Good bye message
	 */
	public SessionClosureListener(FtpSessionHandler fs,Channel ch, Logger logger, String remoteIp,String goodByeMsg) 
	{
		this.logger=logger;
		this.remoteIp=remoteIp;
		this.ftpMessage=goodByeMsg;
		this.fs=fs;
		this.ch=ch;
	}

	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		logger.info("Message:"+ftpMessage+" sent to:"+remoteIp);
		if (fs!=null)
		{
			fs.close();
			fs=null;
		}
		else
		{	
			this.ch.close();
			this.ch=null;
		}		
	}

}
