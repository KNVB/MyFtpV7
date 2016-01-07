package com.myftpserver.listener;

import org.apache.logging.log4j.Logger;

import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

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
public class TransferExceptionListener  implements ChannelFutureListener 
{
	Logger logger;
	String message;
	FtpSessionHandler fs;
	PassiveServer passiveServer;
	public TransferExceptionListener(FtpSessionHandler fs, String message)
	{
		this.fs=fs;
		this.message=message;
		this.logger=fs.getLogger();
		this.passiveServer=fs.getPassiveServer();
	}
	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		logger.info("Message:"+message+" sent to "+fs.getClientIp());
		if (passiveServer!=null)
		{
			passiveServer.stop();
			passiveServer=null;
			fs.setPassiveServer(passiveServer);
		}
	}

}
