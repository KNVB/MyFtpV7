package com.myftpserver.listener;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

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
public class SendFileCompleteListener implements ChannelFutureListener
{
	FtpSessionHandler fs;
	String fileName;
	ChannelHandlerContext responseCtx;
	PassiveServer passiveServer=null;
	Logger logger;
	String remoteIp;
	Configuration config;

	public SendFileCompleteListener(String fileName,FtpSessionHandler fs,ChannelHandlerContext responseCtx, PassiveServer passiveServer)
	{
		this.fs=fs;
		this.fileName=fileName;
		this.passiveServer=passiveServer;
		this.responseCtx=responseCtx;
		this.remoteIp=fs.getClientIp();
		this.logger=fs.getConfig().getLogger();
		this.config=fs.getConfig();
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, config.getFtpMessage("226_Transfer_Ok")); 
		if (passiveServer==null)
			cf.channel().close().addListener(new ActiveChannelCloseListener(fs,this.responseCtx));
		else
			cf.channel().close().addListener(new PassiveChannelCloseListener(fs,this.responseCtx, passiveServer));
		logger.info("File download completed.");	
	}

}
