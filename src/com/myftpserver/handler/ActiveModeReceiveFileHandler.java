package com.myftpserver.handler;
import java.io.*;

import org.apache.logging.log4j.Logger;

import com.util.Utility;

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
public class ActiveModeReceiveFileHandler extends ChannelInboundHandlerAdapter
{
	private Logger logger;
	private FtpSessionHandler fs;
	private BufferedOutputStream bos=null;
	/**
	 * Active Mode Receive file handler
	 * @param fs FtpSessionHandler object
	 */
	public ActiveModeReceiveFileHandler(FtpSessionHandler fs)
	{
		this.fs=fs;
		this.logger=fs.getLogger();
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception 
	{ 
		logger.debug("ActiveModeReceiveFileHandler channel active");
		try
		{
			bos=new BufferedOutputStream(new FileOutputStream(fs.getUploadFile()));
		}
		catch (FileNotFoundException err)
		{
			logger.debug(err.getMessage());
			ctx.channel().close();
			Utility.sendMessageToClient(fs.getChannel(),logger,fs.getClientIp(),fs.getFtpMessage("553_Cannot_Create_File").replace("%1", err.getMessage()));
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
				//fs.setUploadFile(null);
				logger.debug("ActiveModeReceiveFileHandler channel inactive");
			}
			catch (Exception err)
			{
				logger.debug(err.getMessage());
			}
		}
    }
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
	}	
}
