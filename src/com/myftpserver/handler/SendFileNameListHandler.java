package com.myftpserver.handler;

import java.io.IOException;

//import org.apache.log4j.Logger;

import com.util.Utility;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class SendFileNameListHandler extends SimpleChannelInboundHandler<ByteBuf> implements ChannelHandler
{
	//private Logger logger;
	private FtpSessionHandler fs;
	private StringBuffer fileNameList;
	private PassiveServer passiveServer=null;
	private ChannelHandlerContext responseCtx;

	public SendFileNameListHandler(StringBuffer fileNameList2,ChannelHandlerContext ctx, FtpSessionHandler fs,PassiveServer txServer) 
	{
		this.fs=fs;
		this.responseCtx=ctx;
		this.passiveServer=txServer;
		this.fileNameList=fileNameList2;
		//this.logger=fs.getConfig().getLogger();
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
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception
	{
		if (passiveServer!=null)
			Utility.sendFileNameList(ctx.channel(),responseCtx,fileNameList,fs,passiveServer);
	}
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, ByteBuf arg1)
			throws Exception {
		// TODO Auto-generated method stub
	}
}
