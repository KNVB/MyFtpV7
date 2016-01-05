package com.myftpserver.channelinitializer;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.MyFtpServer;
import com.myftpserver.ServerConfig;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.listener.CommandChannelClosureListener;

import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
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
public class CommandChannelInitializer extends ChannelInitializer<Channel>
{
	Logger logger;
	MyFtpServer s;
	/**
	 * Initialize a command channel for user interaction
	 * @param t MyFtpServer object
	 * @param logger 
	 */
	public CommandChannelInitializer(MyFtpServer t, Logger logger)
	{
		s=t;
		this.logger=logger;
	}
	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		String remoteIp=(((InetSocketAddress) ch.remoteAddress()).getAddress().getHostAddress());
		if (s.isOverConnectionLimit())
		{
			String msg=s.getServerConfig().getFtpMessage("330_Connection_Full");
			Utility.disconnectFromClient(ch,logger,remoteIp,msg);
		}
		else
		{
			ServerConfig serverConfig=s.getServerConfig();
			ch.closeFuture().addListener(new CommandChannelClosureListener(s,remoteIp));
			Utility.sendMessageToClient(ch,logger,remoteIp,"220 "+serverConfig.getFtpMessage("Greeting_Message"));
			ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(s.getServerConfig().getCommandChannelConnectionTimeOut(), 30, 0));

			ch.pipeline().addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
			ch.pipeline().addLast("frameDecoder",new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast("MyHandler",new FtpSessionHandler(ch,s,remoteIp));
		}
	}

}
