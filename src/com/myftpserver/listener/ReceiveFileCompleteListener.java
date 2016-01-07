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
public class ReceiveFileCompleteListener implements ChannelFutureListener 
{
	private Logger logger;
	private String fileName;
	@SuppressWarnings("unused")
	private FtpSessionHandler fs;
	/**
	 * It is triggered when a file is uploaded to server successfully by client
	 * @param fs FTP session
	 * @param fileName The full path name for the uploaded file
	 */
	public ReceiveFileCompleteListener(FtpSessionHandler fs, String fileName)
	{
		this.fs=fs;
		this.fileName=fileName;
		this.logger=fs.getLogger();
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		cf.channel().close();
		logger.info("File "+fileName+" upload completed.");		
	}
}