package com.myftpserver.handler;

import java.io.File;
import java.io.IOException;

import com.myftpserver.PassiveServer;
import com.myftpserver.listener.SendFileCompleteListener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class SendFileHandler extends SimpleChannelInboundHandler<ByteBuf> implements ChannelHandler 
{
	String fileName;
	ChannelHandlerContext responseCtx;
	PassiveServer passiveServer=null;
	FtpSessionHandler fs;
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
			ctx.writeAndFlush(new ChunkedFile(new File(this.fileName))).addListener(new SendFileCompleteListener(this.fileName,this.fs,this.responseCtx,passiveServer));	
	}
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException 
	{
		if (passiveServer==null)
			ctx.writeAndFlush(new ChunkedFile(new File(this.fileName))).addListener(new SendFileCompleteListener(this.fileName,this.fs,this.responseCtx,this.passiveServer));
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