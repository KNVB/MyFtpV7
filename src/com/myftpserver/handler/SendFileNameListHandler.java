package com.myftpserver.handler;

import java.io.IOException;

import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.listener.SendFileNameListCompleteListener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
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
public class SendFileNameListHandler extends SimpleChannelInboundHandler<ByteBuf> implements ChannelHandler
{
	private FtpSessionHandler fs;
	private StringBuffer fileNameList;
	private PassiveServer passiveServer=null;
	private ChannelHandlerContext responseCtx;
	/**
	 * Send file name list handler
	 * @param fileNameList  A StringBuffer object that contains file listing
	 * @param responseCtx A ChannelHandlerContext for sending file name list transfer result to client
	 * @param fs  FtpSessionHandler object
	 * @param passiveServer PassiveServer object
	 */
	public SendFileNameListHandler(StringBuffer fileNameList,ChannelHandlerContext responseCtx, FtpSessionHandler fs,PassiveServer passiveServer) 
	{
		this.fs=fs;
		this.responseCtx=responseCtx;
		this.passiveServer=passiveServer;
		this.fileNameList=fileNameList;
	}
	public void channelActive(ChannelHandlerContext ctx) throws IOException 
	{
		ctx.writeAndFlush(Unpooled.copiedBuffer(fileNameList.toString(),CharsetUtil.UTF_8)).addListener(new SendFileNameListCompleteListener(fs,responseCtx,passiveServer));
	}
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception
	{
		if (passiveServer!=null)
			ctx.writeAndFlush(Unpooled.copiedBuffer(fileNameList.toString(),CharsetUtil.UTF_8)).addListener(new SendFileNameListCompleteListener(fs,responseCtx,passiveServer));
	}
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, ByteBuf arg1)
			throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1)
			throws Exception {
		// TODO Auto-generated method stub
	}

}
