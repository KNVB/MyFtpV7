package com.myftpserver.channelinitializer;

import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.ReceiveFileHandler;
import com.myftpserver.handler.SendFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ActiveChannelInitializer extends ChannelInitializer<Channel>
{
	
	private int mode;
	private String fileName;
	private FtpSessionHandler fs;
	private boolean isSendFile=false;
	private StringBuilder fileNameList;
	private PassiveServer txServer=null;
	private ChannelHandlerContext responseCtx;
	public ActiveChannelInitializer(FtpSessionHandler fs,ChannelHandlerContext responseCtx, int mode,String fileName) 
	{
		if (mode==MyFtpServer.SENDFILE)
			isSendFile=true;
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
		if (this.mode==MyFtpServer.RECEIVEFILE)
		{
			 ch.pipeline().addLast(new ReceiveFileHandler(fs, this.fileName,responseCtx,null));
		}
		else
		{
			if (isSendFile)
			{
				ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
				ch.pipeline().addLast("handler",new SendFileHandler(fileName,fs,responseCtx, txServer));
			}
			else
				ch.pipeline().addLast(new SendFileNameListHandler(fileNameList,responseCtx, fs,txServer));
		}
	}
}
