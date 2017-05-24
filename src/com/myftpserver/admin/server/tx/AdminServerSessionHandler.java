package com.myftpserver.admin.server.tx;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myftpserver.Utility;
import com.myftpserver.admin.AdminFunction;
import com.myftpserver.admin.AdminObject;
import com.myftpserver.admin.object.AdminUser;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
public class AdminServerSessionHandler extends SimpleChannelInboundHandler<String>
{
	private AdminObject adminObject;
	private Logger logger;
	private String remoteIp;
	private ObjectMapper mapper = new ObjectMapper();
	public AdminServerSessionHandler(Logger logger) 
	{
		this.logger=logger;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		remoteIp=(((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress());
		logger.debug("remote IP address:"+remoteIp);
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String obj) throws Exception 
	{
		logger.debug("server receive:"+obj+"|");
		adminObject=mapper.readValue(obj,AdminObject.class);
		switch (adminObject.getAdminFunctionCode())
		{
			case AdminFunction.ADMIN_LOGIN:	AdminUser adminUser=mapper.readValue(adminObject.getJsonString(),AdminUser.class);
											logger.debug(adminUser.getName());
											break;
		}
		Utility.sendTextMessage(ctx.channel(),logger,new String("Login Success."),null);
	}
	/**
	 * Calls ChannelHandlerContext.fireExceptionCaught(Throwable) to forward to the next ChannelHandler in the ChannelPipeline. Sub-classes may override this method to change behavior.
	 * @param ctx the channel that user input command
	 * @param cause the exception cause  
	 */
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) 
    {
		logger.debug(cause.getMessage());
    }
	/**
	 * Close the admin session
	 */
	public void close()
	{
		/*ch.close();
		ch=null;*/		
	}
}
