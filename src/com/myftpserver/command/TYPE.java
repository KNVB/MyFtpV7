package com.myftpserver.command;

import com.util.Utility;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;

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
public class TYPE implements FtpCommandInterface 
{
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param) 
	{
		if (fs.isLogined())
		{
			param=param.trim().toUpperCase();
			switch (param)
			{
				case "I":
				case "A":fs.setDataType(param);
						 Utility.sendMessageToClient(ctx.channel(),fs.getLogger(),fs.getClientIp(), fs.getFtpMessage("200_Transfer_Set"));
						 break;
				default:
					Utility.sendMessageToClient(ctx.channel(),fs.getLogger(),fs.getClientIp(), fs.getFtpMessage("504_Command_Not_Support_This_Parameter"));
					break;
			}
		}
	}
}