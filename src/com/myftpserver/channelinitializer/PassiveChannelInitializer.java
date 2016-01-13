package com.myftpserver.channelinitializer;

import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;
import com.myftpserver.User;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.ReceiveFileHandler;
import com.myftpserver.handler.SendFileHandler;
import com.myftpserver.handler.SendFileNameListHandler;
import com.myftpserver.listener.PassiveChannelCloseListener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

public class PassiveChannelInitializer extends ChannelInitializer<Channel> 
{
	private int mode;
	private User user;
	private String fileName;
	private FtpSessionHandler fs;
	private StringBuffer fileNameList;
	private PassiveServer passiveServer=null;
	private ChannelHandlerContext responseCtx;

	/**
	 * Initialize an active mode channel for file transmission
	 * @param fs FtpSessionHandler object
	 * @param responseCtx A ChannelHandlerContext for sending file transfer result to client
	 * @param mode Transfer mode
	 * @param fileName The file to be sent to client
	 */
	public PassiveChannelInitializer(FtpSessionHandler fs,ChannelHandlerContext responseCtx, PassiveServer passiveServer,int mode,String fileName) 
	{
		this.fs=fs;
		this.mode=mode;
		this.user=fs.getUser();
		this.fileName=fileName;
		this.responseCtx=responseCtx;
		this.passiveServer=passiveServer;
	}
	
	/**
	 * Initialize an passive mode channel for file name list transmission
	 * @param fs FtpSessionHandler object
	 * @param responseCtx A ChannelHandlerContext for sending file name list transfer result to client
	 * @param fileNameList A StringBuffer object that contains file listing
	 */
	public PassiveChannelInitializer(FtpSessionHandler fs,ChannelHandlerContext responseCtx,PassiveServer passiveServer,StringBuffer fileNameList) 
	{
		this.fs=fs;
		this.user=fs.getUser();
		this.responseCtx=responseCtx;
		this.fileNameList=fileNameList;
		this.passiveServer=passiveServer;
		this.mode=MyFtpServer.SENDDIRLIST;
	}
	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		ch.closeFuture().addListener(new PassiveChannelCloseListener(fs,this.responseCtx,passiveServer));
		switch (mode)
		{
			case MyFtpServer.SENDFILE:ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(user.getDownloadSpeedLitmit()*1024,0L));
									  ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
									  ch.pipeline().addLast("handler",new SendFileHandler(fileName,fs, passiveServer));
									  break;
			case MyFtpServer.RECEIVEFILE:ch.pipeline().addLast("TrafficShapingHandler",new ChannelTrafficShapingHandler(0L,user.getUploadSpeedLitmit()*1024));  
			 							 ch.pipeline().addLast(new ReceiveFileHandler(fs, this.fileName,responseCtx,passiveServer));
			 							 break;
			case MyFtpServer.SENDDIRLIST:ch.pipeline().addLast(new SendFileNameListHandler(fileNameList,responseCtx, fs));
										 break;
		}
	}

}
