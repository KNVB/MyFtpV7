package com.myftpserver.channelinitializer;
import com.myftpserver.handler.*;
import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.stream.ChunkedWriteHandler;

public class PassiveChannelInitializer_old extends ChannelInitializer<Channel>
{
	private int mode;
	private String fileName;
	private FtpSessionHandler fs;
	private StringBuffer fileNameList;
	private PassiveServer passiveServer;
	private ChannelHandlerContext responseCtx;
	public PassiveChannelInitializer_old(FtpSessionHandler fs,PassiveServer passiveServer, ChannelHandlerContext responseCtx,int txMode,StringBuffer fileNameList)
	{
		this.fs=fs;
		this.mode=txMode;
		this.fileNameList=fileNameList;
		this.passiveServer=passiveServer;
		this.responseCtx=responseCtx;
	}

	public PassiveChannelInitializer_old(FtpSessionHandler fs,	PassiveServer passiveServer, ChannelHandlerContext responseCtx,int txMode, String fileName) 
	{
		// TODO Auto-generated constructor stub
		this.fs=fs;
		this.mode=txMode;
		this.fileName=fileName;
		this.passiveServer=passiveServer;
		this.responseCtx=responseCtx;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		switch (mode)
		{
			case MyFtpServer.SENDFILE:ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
									  ch.pipeline().addLast("handler",new SendFileHandler(fileName,fs,responseCtx, passiveServer));
									  break;
			case MyFtpServer.RECEIVEFILE:ch.pipeline().addLast(new ReceiveFileHandler(fs, this.fileName,responseCtx,null));
											break;
			case MyFtpServer.SENDDIRLIST:ch.pipeline().addLast(new SendFileNameListHandler(fileNameList,responseCtx, fs,passiveServer));
											break;
		}		
		
	}
}
