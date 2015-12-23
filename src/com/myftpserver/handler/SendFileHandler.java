package com.myftpserver.handler; 
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.Logger;

import com.myftpserver.PassiveServer;
import com.myftpserver.listener.SendFileCompleteListener;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.channel.ChannelHandlerContext;
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
public class SendFileHandler extends SimpleChannelInboundHandler<ByteBuf> implements ChannelHandler 
{
	String fileName;
	ChannelHandlerContext responseCtx;
	PassiveServer passiveServer=null;
	FtpSessionHandler fs;
	/**
	 * Send file handler
	 * @param fileName A file to be sent to client 
	 * @param fs FtpSessionHandler object
	 * @param responseCtx A ChannelHandlerContext for sending file name list transfer result to client
	 * @param passiveServer PassiveServer object
	 */
	public SendFileHandler(String fileName,FtpSessionHandler fs,ChannelHandlerContext responseCtx, PassiveServer passiveServer)
	{
		this.fs=fs;
		this.fileName=fileName;
		this.passiveServer=passiveServer;
		this.responseCtx=responseCtx;
	}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)throws IOException
	{
		if (passiveServer!=null)
		{
			//ctx.writeAndFlush(new ChunkedFile(new File(this.fileName))).addListener(new SendFileCompleteListener(this.fileName,this.fs,this.responseCtx,passiveServer));
			try {
				sendFile(ctx);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException 
	{
		if (passiveServer==null)
		{	
			//ctx.writeAndFlush(new ChunkedFile(new File(this.fileName))).addListener(new SendFileCompleteListener(this.fileName,this.fs,this.responseCtx,this.passiveServer));
			try {
				sendFile(ctx);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
		Logger logger=fs.getConfig().getLogger();
		logger.debug("Data type="+fs.getDataType()+"|");
		if (fs.getDataType().equals("A"))
		{
			String line;
			ChannelFuture cf=null;
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(this.fileName),"ISO-8859-1"));
			
			while ((line = br.readLine()) != null) 
			{
				cf=ctx.writeAndFlush(Unpooled.copiedBuffer(line+"\r\n",CharsetUtil.ISO_8859_1));
			}
			br.close();
			SendFileCompleteListener qq=new SendFileCompleteListener(this.fileName,this.fs,this.responseCtx,this.passiveServer);
			qq.operationComplete(cf);
		}
		else
			ctx.writeAndFlush(new ChunkedFile(new File(this.fileName))).addListener(new SendFileCompleteListener(this.fileName,this.fs,this.responseCtx,this.passiveServer));
	}

}
