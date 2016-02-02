package com.myftpserver.handler; 

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.PassiveServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelFuture;
import io.netty.handler.stream.ChunkedFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;

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
public class SendFileHandler extends SimpleChannelInboundHandler<ByteBuf> implements ChannelFutureListener 
{
	private Logger logger;
	private String remoteIp;
	private FtpSessionHandler fs;
	private PassiveServer passiveServer=null;
	/**
	 * Send file handler
	 * It also handle send file completed and channel close event.
	 * @param fs FtpSessionHandler object
	 */
	public SendFileHandler(FtpSessionHandler fs)
	{
		this.fs=fs;
		this.logger=fs.getLogger();
		this.remoteIp=fs.getClientIp();
		this.passiveServer=fs.getPassiveServer();
	}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)throws IOException
	{
		if (passiveServer!=null)
		{
			try {
				sendFile(ctx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException 
	{
		if (passiveServer==null)
		{	
			try {
				sendFile(ctx);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t)
			throws Exception {
		t.printStackTrace();
	}
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, ByteBuf arg1)
			throws Exception {
		// TODO Auto-generated method stub
	}
	private void sendFile(ChannelHandlerContext ctx) throws Exception
	{
		Logger logger=fs.getLogger();
		logger.debug("Data type="+fs.getDataType()+"|");
		if (fs.getDataType().equals("A"))
		{
			String line;
			ChannelFuture cf=null;
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(fs.getDownloadFile()),"ISO-8859-1"));
			
			while ((line = br.readLine()) != null) 
			{
				while (!ctx.channel().isWritable());
				cf=ctx.writeAndFlush(Unpooled.copiedBuffer(line+"\r\n",CharsetUtil.ISO_8859_1));
			}
			br.close();
			operationComplete(cf);
		}
		else
			ctx.writeAndFlush(new ChunkedFile(fs.getDownloadFile())).addListener(this);
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		if (cf.channel().isOpen())  //it handle file sent complete event
		{	
			if (fs.isPassiveModeTransfer)
			{
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
}
