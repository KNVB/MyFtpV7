package com.myftpserver.channelinitializer;

import com.myftpserver.*;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.ReceiveFileHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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
public class PassiveChannelInitializer extends ChannelInitializer<Channel> 
{
	private FtpSessionHandler fs;
	private ChannelHandlerContext ctx;
	private PassiveServer passiveServer;
	/**
	 * Initialize a passive server for passive mode operation
	 * @param fs FtpSessionHandler object
	 */
	public PassiveChannelInitializer(ChannelHandlerContext ctx,FtpSessionHandler fs) 
	{
		super();
		this.fs=fs;
		this.ctx=ctx;
		this.passiveServer=fs.getPassiveServer();
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		passiveServer.setChannel(ch);
		//ch.pipeline().addLast("ReceiveHandler",new PassiveModeReceiveFileHandler(fs));
		ch.pipeline().addLast("ReceiveHandler",new ReceiveFileHandler(fs,ctx));
	}

}
