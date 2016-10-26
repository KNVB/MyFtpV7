package com.myftpserver.handler;

import org.apache.logging.log4j.Logger;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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
@Sharable
public class CommandChannelTimeoutHandler extends ChannelDuplexHandler
{
	Logger logger;
	String remoteIp;
	/**
	 * It is used to handle Command Channel event.
	 * @param l Message logger
	 * @param remoteIp The remote end of time out channel {@link io.netty.channel.Channel}
	 */
	public CommandChannelTimeoutHandler (Logger l, String remoteIp)
	{
		this.logger=l;
		this.remoteIp=remoteIp;
	}
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception 
	{
        if (evt instanceof IdleStateEvent) 
        {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) 
            {
                logger.debug("Command Channel (Remote IP addresss="+remoteIp+")  Timeout");
                ctx.close();
            } 
        }
    }
}
