package com.myftpserver.listener;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
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
public class SendFileNameListCompleteListener implements ChannelFutureListener  
{
	Logger logger;
	String remoteIp;
	FtpSessionHandler fs;
	Configuration config;
	PassiveServer passiveServer=null;
	ChannelHandlerContext responseCtx;
	public SendFileNameListCompleteListener(FtpSessionHandler fs,ChannelHandlerContext rCtx,PassiveServer txServer) 
	{
		this.fs=fs;
		this.remoteIp=fs.getClientIp();
		this.logger=fs.getConfig().getLogger();
		this.responseCtx=rCtx;
		this.config=fs.getConfig();
		this.passiveServer=txServer;
	}

	@Override
	public void operationComplete(ChannelFuture ch) throws Exception 
	{
		fs.getConfig().getLogger().debug("File name list transfered to "+remoteIp+" Completed.");
		if (passiveServer!=null)
			ch.addListener(new PassiveChannelCloseListener(fs, responseCtx, passiveServer));
		ch.channel().close();
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, config.getFtpMessage("226_Transfer_Ok"));
		
	}
}
