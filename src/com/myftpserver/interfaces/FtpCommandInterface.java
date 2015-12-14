package com.myftpserver.interfaces;

import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

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
public interface FtpCommandInterface 
{
 	/**
 	 * Display help message
 	 * @param fs FtpSessionHandler
 	 * @return Help message
 	 */
	public String helpMessage(FtpSessionHandler fs);
	/**
	 * Perform raw ftp command action
	 * @param fs FtpSessionHandler
	 * @param ctx  A ChannelHandlerContext for sending execution result to client
	 * @param param Parameter for the raw FTP command 
	 * @param logger  message logger 
	 */
	public void execute (FtpSessionHandler fs,ChannelHandlerContext ctx,String param,Logger logger); 
}