package com.myftpserver.handler;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
public class SendFileNameListHandler extends SimpleChannelInboundHandler<ByteBuf> implements ChannelFutureListener 
{
	private Logger logger;
	private String remoteIp;
	private FtpSessionHandler fs;
	private StringBuffer fileNameList;
	/**
	 * Send file name list handler
	 * It also handle send file listing completed and channel close event.
	 * @param fileNameList  A StringBuffer object that contains file listing
	 * @param fs  FtpSessionHandler object 
	 */
	public SendFileNameListHandler(StringBuffer fileNameList,FtpSessionHandler fs) 
	{
		this.fs=fs;
		this.logger=fs.getLogger();
		this.remoteIp=fs.getClientIp();
		this.fileNameList=fileNameList;		
	}
	public void channelActive(ChannelHandlerContext ctx) throws IOException 
	{
		if (!fs.isPassiveModeTransfer)
			ctx.writeAndFlush(Unpooled.copiedBuffer(fileNameList.toString(),CharsetUtil.UTF_8)).addListener(this);
	}
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception
	{
		if (fs.isPassiveModeTransfer)
			ctx.writeAndFlush(Unpooled.copiedBuffer(fileNameList.toString(),CharsetUtil.UTF_8)).addListener(this);
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		if (cf.channel().isOpen())  //it handle file listing sent complete event
		{	
			if (fs.isPassiveModeTransfer)
			{
				PassiveServer passiveServer=fs.getPassiveServer();
				passiveServer.stop();
				passiveServer=null;
				fs.setPassiveServer(passiveServer);
			}
			else
				cf.channel().close();
		}
		else
		{	//it handle channel close event
			String message=fs.getFtpMessage("226_Transfer_Ok");
			Utility.sendMessageToClient(fs.getChannel(),logger, remoteIp,message);
		}
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
