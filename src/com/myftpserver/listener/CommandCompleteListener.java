package com.myftpserver.listener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import org.apache.logging.log4j.Logger;
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
public class CommandCompleteListener implements ChannelFutureListener
{
	private Logger logger; 
	private String remoteIp,ftpMessage;
	/**
	 * It is triggered when a ftp command is executed successfully 
	 * @param logger Message logger
	 * @param remoteIp client IP Address
	 * @param ftpMessage return message for the action perform 
	 */
	public CommandCompleteListener(Logger logger, String remoteIp,String ftpMessage) 
	{
		this.logger=logger;
		this.remoteIp=remoteIp;
		this.ftpMessage=ftpMessage;
	}

	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		logger.info("Message:"+ftpMessage+" sent to:"+remoteIp);
	}
}
