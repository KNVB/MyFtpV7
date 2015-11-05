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
	private PassiveServer passiveServer;
	public PassiveChannelInitializer(PassiveServer passiveServer, int txMode,String fileName)
	{
		this.mode=txMode;
		this.fileName=fileName;
		this.passiveServer=passiveServer;
		
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		if (this.mode==MyFtpServer.RECEIVEFILE)
		{
			 ch.pipeline().addLast(new ReceiveFileHandler(this.fileName,passiveServer));
		}
		else
		{
			ch.pipeline().addLast("streamer", new ChunkedWriteHandler()); 
//			ch.pipeline().addLast("handler",new SendFileHandler(this.fileName,passiveServer));
		}
		
	}
}
