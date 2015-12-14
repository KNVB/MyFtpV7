package com.myftpserver.listener;

import com.myftpserver.Configuration;
import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
public class ActiveChannelCloseListener  implements ChannelFutureListener 
{
	Logger logger;
	String remoteIp;
	FtpSessionHandler fs;
	Configuration config;
	ChannelHandlerContext responseCtx;
	public ActiveChannelCloseListener(FtpSessionHandler fs, ChannelHandlerContext responseCtx) 
	{
		this.fs=fs;
		this.config=fs.getConfig();
		this.responseCtx=responseCtx;
		this.remoteIp=fs.getClientIp();
		this.logger=fs.getConfig().getLogger();
	}

	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		logger.debug("Active Mode Transfer channel is closed");
		//Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, config.getFtpMessage("226_Transfer_Ok"));
	}
}