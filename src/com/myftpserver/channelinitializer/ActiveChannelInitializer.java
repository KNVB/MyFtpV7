package com.myftpserver.channelinitializer;

import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.SendFileHandler;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.ReceiveFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ActiveChannelInitializer extends ChannelInitializer<Channel>
{
	
	private int mode;
	private String fileName;
	private FtpSessionHandler fs;
		private StringBuilder fileNameList;
	private PassiveServer txServer=null;
	private ChannelHandlerContext responseCtx;
	public ActiveChannelInitializer(FtpSessionHandler fs,ChannelHandlerContext responseCtx, int mode,String fileName) 
	{
		// TODO Auto-generated constructor stub
		this.fs=fs;
		this.mode=mode;
		this.fileName=fileName;
		this.responseCtx=responseCtx;
	}
	public ActiveChannelInitializer(FtpSessionHandler fs,ChannelHandlerContext responseCtx, StringBuilder fileNameList) 
	{
		// TODO Auto-generated constructor stub
		this.fs=fs;
		this.mode=MyFtpServer.SENDDIRLIST;
		this.responseCtx=responseCtx;
		this.fileNameList=fileNameList;
	}
	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		// TODO Auto-generated method stub
		switch (mode)
		{
			case MyFtpServer.SENDFILE:ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
									  ch.pipeline().addLast("handler",new SendFileHandler(fileName,fs,responseCtx, txServer));
									  break;
			case MyFtpServer.RECEIVEFILE:ch.pipeline().addLast(new ReceiveFileHandler(fs, this.fileName,responseCtx,null));
											break;
			case MyFtpServer.SENDDIRLIST:ch.pipeline().addLast(new SendFileNameListHandler(fileNameList,responseCtx, fs,txServer));
											break;
		}
	}
}
