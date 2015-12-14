package com.myftpserver.listener;

import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

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
	FtpSessionHandler fs=null;
	Logger logger=null;
	Channel ch; 
	String remoteIp=new String(),ftpMessage=new String();
	
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
