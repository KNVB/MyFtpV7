package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.handler.FtpSessionHandler;
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
public class PWD implements com.myftpserver.interfaces.FtpCommandInterface {

	/**
	 *This command causes the name of the current working directory to be returned in the reply.<br>
     *For detail information about PWD command,please refer  <a href="https://tools.ietf.org/html/rfc959">RFC 959</a>
	 */
	public PWD()
	{
		
	}
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(ChannelHandlerContext ctx,FtpSessionHandler fs, String param)	
	{
		Utility.sendMessageToClient(ctx.channel(),fs.getLogger(),fs.getClientIp(), fs.getFtpMessage("257_PWD").replace("%1", fs.getCurrentPath()));
	}
	
}
