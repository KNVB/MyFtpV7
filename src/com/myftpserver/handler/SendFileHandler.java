package com.myftpserver.handler;

import java.io.File;
import java.io.IOException;

import com.myftpserver.PassiveServer;
import com.myftpserver.listener.SendFileCompleteListener;

import io.netty.buffer.ByteBuf;
import io.netty.handler.stream.ChunkedFile;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class SendFileHandler extends SimpleChannelInboundHandler <ByteBuf> implements ChannelHandler 
{
	String fileName;
	ChannelHandlerContext responseCtx;
	PassiveServer txServer=null;
	FtpSessionHandler fs;
	public SendFileHandler(String fileName,FtpSessionHandler fs,ChannelHandlerContext responseCtx, PassiveServer txServer)
	{
		this.fs=fs;
		this.fileName=fileName;
		this.txServer=txServer;
		this.responseCtx=responseCtx;
	}
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException 
	{
		ctx.writeAndFlush(new ChunkedFile(new File(this.fileName))).addListener(new SendFileCompleteListener(this.fileName,this.fs,this.responseCtx,this.txServer));
    }
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception
	{
		ctx.writeAndFlush(new ChunkedFile(new File(this.fileName))).addListener(new SendFileCompleteListener(this.fileName,this.fs,this.responseCtx,this.txServer));
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
	
	
}