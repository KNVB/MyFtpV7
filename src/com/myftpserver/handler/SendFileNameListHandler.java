package com.myftpserver.handler;

import java.io.IOException;

import com.util.Utility;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


public class SendFileNameListHandler extends SimpleChannelInboundHandler<ByteBuf> 
{
	private PassiveServer passiveServer=null;
	private FtpSessionHandler fs;
	private StringBuilder fileNameList;
	private ChannelHandlerContext responseCtx;

	public SendFileNameListHandler(StringBuilder fileNameList,ChannelHandlerContext ctx, FtpSessionHandler fs,PassiveServer txServer) 
	{
		// TODO Auto-generated constructor stub
		this.fs=fs;
		this.fileNameList=fileNameList;
		this.responseCtx=ctx;
		this.passiveServer=txServer;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public void channelActive(ChannelHandlerContext ctx) throws IOException 
	{
		Utility.sendFileNameList(ctx.channel(),responseCtx,fileNameList,fs,passiveServer);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, ByteBuf arg1)
			throws Exception {
		// TODO Auto-generated method stub
		//fs.getConfig().getLogger().debug("channelRead0 is called.");
	}
}
