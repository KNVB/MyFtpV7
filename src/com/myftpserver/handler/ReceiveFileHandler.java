package com.myftpserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

import java.io.File;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import org.apache.logging.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.listener.PassiveChannelCloseListener;
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
public class ReceiveFileHandler extends ChannelInboundHandlerAdapter implements ChannelHandler 
{
	private Logger logger;
	private File tempFile=null;
	private FtpSessionHandler fs;
	private BufferedOutputStream bos=null;
	public ReceiveFileHandler(FtpSessionHandler fs) 
	{
		this.fs=fs;
		this.logger=fs.getLogger();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
	{
		if (bos==null)
		{
			User user=fs.getUser();
			ctx.channel().closeFuture().addListener(new PassiveChannelCloseListener(fs));
			ctx.channel().pipeline().addFirst("TrafficShapingHandler",new ChannelTrafficShapingHandler(0L,user.getUploadSpeedLitmit()*1024));
			if (user.getUploadSpeedLitmit()==0L)
				logger.info("File upload speed is limited by connection speed");
			else
				logger.info("File upload speed limit:"+user.getUploadSpeedLitmit()+" kB/s");
			tempFile=File.createTempFile("temp-file-name", ".tmp");
			fs.setUploadTempFile(tempFile);
			bos=new BufferedOutputStream(new FileOutputStream(tempFile));
		}
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
				logger.debug("RecevieHandler channel inactive");
				
				if (fs.getUploadFileName()!=null)
				{
					tempFile.renameTo(new File(fs.getUploadFileName()));
					logger.info("File "+fs.getUploadFileName()+" uploaded successfully");
				}
				fs.setUploadTempFile(tempFile);
				tempFile.delete();
				logger.debug("temp File "+tempFile.getAbsolutePath()+" is deleted.");
				tempFile=null;
			}
			catch (Exception err)
			{
				//logger.debug(err.getMessage());
				err.printStackTrace();
			}
		}
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable t)throws Exception 
	{
		if (tempFile!=null)
		{
			tempFile.delete();
			logger.debug("temp file="+tempFile.getAbsolutePath()+" is deleted.");
		}
		logger.debug("RecevieHandler:"+t.getMessage());
	}
	@Override
	public void handlerAdded(ChannelHandlerContext arg0) throws Exception 
	{
		logger.info("ReceiverHandler:Channel Active");
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception 
	{
		logger.info("RecevieHandler:Handler Removed");
	}

}
