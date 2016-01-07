package com.myftpserver.listener;

import com.myftpserver.handler.FtpSessionHandler;

import org.apache.logging.log4j.Logger;

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
public class SendFileCompleteListener implements ChannelFutureListener
{
	Logger logger;
	String fileName;
	FtpSessionHandler fs;
	public SendFileCompleteListener(String fileName,FtpSessionHandler fs)
	{
		this.fs=fs;
		this.fileName=fileName;
		this.logger=fs.getLogger();
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		//Utility.sendMessageToClient(this.responseCtx.channel(),logger, remoteIp, fs.getFtpMessage("226_Transfer_Ok")); 
		cf.channel().close();
		logger.info("File "+fileName+" download completed.");	
	}

}
