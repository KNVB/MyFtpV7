package com.myftpserver.handler;
import java.io.*;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.PassiveServer;


import com.myftpserver.User;
import com.myftpserver.listener.ReceiveFilerCompleteListener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInboundHandlerAdapter;
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
public class ReceiveFileHandler extends ChannelInboundHandlerAdapter
{
	private Logger logger;
	private String fileName;
	private FtpSessionHandler fs;
	private ChannelHandlerContext responseCtx;
	private BufferedOutputStream bos=null;
	private PassiveServer passiveServer=null;
	/**
	 * Receive file handler
	 * @param fs FtpSessionHandler object
	 * @param fileName the location of the file to be resided.
	 * @param responseCtx A ChannelHandlerContext for sending file receive result to client
	 * @param passiveServer PassiveServer object
	 */
	public ReceiveFileHandler(FtpSessionHandler fs,String fileName, ChannelHandlerContext responseCtx, PassiveServer passiveServer)
	{
		this.fs=fs;
		this.fileName=fileName;
		this.responseCtx=responseCtx;
		this.passiveServer=passiveServer;
		this.logger=fs.getLogger();
	}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)throws IOException,Exception
	{
		if (passiveServer!=null)
			channelActive(ctx);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception 
	{ 
		User user=fs.getUser();
		logger.debug("ReceiveFileHandler channel active");
		if (user.getUploadSpeedLitmit()==0L)
			logger.info("File upload speed is limited by connection speed");
		else
			logger.info("File upload speed limit:"+user.getUploadSpeedLitmit()+" kB/s");
		try
		{
			bos=new BufferedOutputStream(new FileOutputStream(new File(fileName)));
		}
		catch (FileNotFoundException err)
		{
			logger.debug(err.getMessage());
			ctx.channel().close();
			Utility.sendMessageToClient(responseCtx.channel(),logger,fs.getClientIp(),fs.getFtpMessage("553_Cannot_Create_File").replace("%1", err.getMessage()));
		}
    }
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
	{ 
		ByteBuf in = (ByteBuf) msg;
		//logger.debug("ReceiveFileHandler channelRead buffer capacity="+in.capacity()+",readable byte count="+in.readableBytes());
	    try 
	    {
	        while (in.isReadable()) 
	        { 
	           //bos.write(in.readByte());
	        	in.readBytes(bos,in.readableBytes());
	        }
	        bos.flush();
	    } finally {
	        in.release();
	    }
    }
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception 
	{ 
		if (bos!=null)
		{
			try
			{
				bos.flush();
				bos.close();
				bos=null;
				logger.debug("ReceiveFileHandler channel inactive");
				ctx.channel().close().addListener(new ReceiveFilerCompleteListener(fs,passiveServer,responseCtx));
			}
			catch (Exception err)
			{
				logger.debug(err.getMessage());
				ctx.channel().close();
			}
		}
		else
		{
			ctx.channel().close();
		}
    }
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
	}
}