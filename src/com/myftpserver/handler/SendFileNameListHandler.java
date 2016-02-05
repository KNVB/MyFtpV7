package com.myftpserver.handler;

import com.util.Utility;
import com.myftpserver.PassiveServer;
import com.myftpserver.interfaces.SendHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

import org.apache.logging.log4j.Logger;
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
public class SendFileNameListHandler extends SendHandler
{
	private int index=0;
	private Logger logger;
	private String remoteIp;
	private FtpSessionHandler fs;
	//private StringBuffer fileNameList;
	private String[] fileNameList;
	/**
	 * Send file name list handler
	 * It send file listing to client and then close the channel.
	 * @param fileNameList  A StringBuffer object that contains file listing
	 * @param fs  FtpSessionHandler object 
	 */
	public SendFileNameListHandler(StringBuffer fileNameList,FtpSessionHandler fs) 
	{
		this.fs=fs;
		this.logger=fs.getLogger();
		this.remoteIp=fs.getClientIp();
		//this.fileNameList=fileNameList;
		this.fileNameList=fileNameList.toString().split("\r\n");
		
	}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception 
	{
		if (fs.isPassiveModeTransfer)
			startToSend(ctx);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception 
	{
		if (!fs.isPassiveModeTransfer)
			startToSend(ctx);
	}
	@Override
	public void startToSend(ChannelHandlerContext ctx) throws Exception
	{
		if(fileNameList[index].equals("")) //The directory is empty
		{
			closeChannel(ctx);
		}
		else
			ctx.writeAndFlush(Unpooled.copiedBuffer(fileNameList[index]+"\r\n",CharsetUtil.UTF_8));
	}
	
	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)throws Exception 
	{
		if (ctx.channel().isWritable())
		{
			try
			{
				ctx.writeAndFlush(Unpooled.copiedBuffer(fileNameList[++index]+"\r\n",CharsetUtil.UTF_8));
			}
			catch (Exception err)
			{
				closeChannel(ctx);
			}
		}
	}
	@Override
	public void closeChannel(ChannelHandlerContext ctx) throws Exception
	{
		if (fs.isPassiveModeTransfer)
		{
			PassiveServer passiveServer=fs.getPassiveServer();
			if (passiveServer!=null)
			{
				passiveServer.stop();
				passiveServer=null;
				fs.setPassiveServer(passiveServer);
			}
		}
		else
			ctx.channel().close();
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception
	{
		String message=fs.getFtpMessage("226_Transfer_Ok");
		Utility.sendMessageToClient(fs.getChannel(),logger, remoteIp,message);
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)	throws Exception 
	{
		cause.printStackTrace();
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception 
	{
		// TODO Auto-generated method stub
	}


	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception 
	{
		// TODO Auto-generated method stub
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)throws Exception 
	{
		// TODO Auto-generated method stub
	}
}
