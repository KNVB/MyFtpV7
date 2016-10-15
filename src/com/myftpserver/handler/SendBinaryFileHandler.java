package com.myftpserver.handler;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.util.Utility;
import com.myftpserver.PassiveServer;
import com.myftpserver.interfaces.SendHandler;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

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

public class SendBinaryFileHandler extends SendHandler
{
	private Logger logger;
	private int byteRead=0;
	private FileChannel fc;
	private String remoteIp;
	private FtpSessionHandler fs;
	private ChannelHandlerContext ctx;
	private boolean isCompleted=false;
	private RandomAccessFile downloadFile;
	private ByteBuffer buffer=ByteBuffer.allocate(1024);
	/**
	 * Send binary file handler
	 * It send file listing to client and then close the channel.
	 * @param fs  {@link FtpSessionHandler} FtpSessionHandler object
	 * @param ctx ChannelHandlerContext
	 */
	public SendBinaryFileHandler(FtpSessionHandler fs,ChannelHandlerContext ctx) 
	{
		this.fs=fs;
		this.ctx=ctx;
		this.logger=fs.getLogger();
		this.remoteIp=fs.getClientIp();
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
		if (fs.getDownloadFile().length()>0L)
		{
			downloadFile=new RandomAccessFile(fs.getDownloadFile(),"r");
			fc=downloadFile.getChannel();
			byteRead=fc.read(buffer);
			ctx.writeAndFlush(Unpooled.wrappedBuffer(buffer.array(),0,byteRead));
		}
		else
			closeChannel(ctx);
	}
	
	@Override
	public void channelWritabilityChanged(ChannelHandlerContext ctx)throws Exception 
	{
		if (isCompleted)
		{
			fc.close();
			downloadFile.close();
			closeChannel(ctx);
		}
		else
		{
			if (fc.position()==downloadFile.length())
			{
				 isCompleted=true;
			}
			else
			{
				 if (ctx.channel().isWritable())
				 {
					 buffer.clear();
					 byteRead=fc.read(buffer);
					 if (byteRead>0)
					 {
						 ctx.writeAndFlush(Unpooled.wrappedBuffer(buffer.array(),0,byteRead));
					 }
					 else
						 isCompleted=true;
				 }				
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
		Utility.sendMessageToClient(this.ctx.channel(),logger, remoteIp,message);
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
