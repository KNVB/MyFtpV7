package com.myftpserver.channelinitializer;
import com.myftpserver.handler.*;
import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.stream.ChunkedWriteHandler;


public class PassiveChannelInitializer extends ChannelInitializer<Channel>
{
	private int mode;
	private String fileName;
	private FtpSessionHandler fs;
	private PassiveServer passiveServer;
	public PassiveChannelInitializer(FtpSessionHandler fs,PassiveServer passiveServer, int txMode,String fileName)
	{
		this.fs=fs;
		this.mode=txMode;
		this.fileName=fileName;
		this.passiveServer=passiveServer;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		if (this.mode==MyFtpServer.RECEIVEFILE)
		{
			 ch.pipeline().addLast(new ReceiveFileHandler(fs, this.fileName,null, passiveServer));
		}
		else
		{
			ch.pipeline().addLast("streamer", new ChunkedWriteHandler()); 
//			ch.pipeline().addLast("handler",new SendFileHandler(this.fileName,passiveServer));
		}
		
	}
}
