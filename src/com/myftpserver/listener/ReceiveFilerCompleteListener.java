package com.myftpserver.listener;

import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.util.Utility;

import org.apache.logging.log4j.Logger;

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
public class ReceiveFilerCompleteListener implements ChannelFutureListener 
{
	Logger logger;
	Configuration config;
	FtpSessionHandler fs;
	PassiveServer txServer=null;
	ChannelHandlerContext responseCtx;
	
	public ReceiveFilerCompleteListener(FtpSessionHandler fs,PassiveServer txServer, ChannelHandlerContext responseCtx)
	{
		this.fs=fs;
		this.config=fs.getConfig();
		this.txServer=txServer;
		this.responseCtx=responseCtx;
		this.logger=fs.getConfig().getLogger();
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, fs.getClientIp(), config.getFtpMessage("226_Transfer_Ok"));
		if (txServer==null)
			cf.channel().close().addListener(new ActiveChannelCloseListener(fs,responseCtx));
		else
			cf.channel().close().addListener(new PassiveChannelCloseListener(fs,responseCtx,txServer));
		logger.info("File upload completed.");		
	}
}