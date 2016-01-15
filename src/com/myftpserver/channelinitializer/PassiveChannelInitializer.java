package com.myftpserver.channelinitializer;

import com.myftpserver.*;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.handler.ReceiveFileHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;


public class PassiveChannelInitializer extends ChannelInitializer<Channel> 
{
	private FtpSessionHandler fs;
	private PassiveServer passiveServer;
	/**
	 * Initialize a passive server for passive mode operation
	 * @param fs FtpSessionHandler object
	 */
	public PassiveChannelInitializer(FtpSessionHandler fs) 
	{
		super();
		this.fs=fs;
		this.passiveServer=fs.getPassiveServer();
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		passiveServer.setChannel(ch);
		ch.pipeline().addLast("ReceiveHandler",new ReceiveFileHandler(fs));
	}

}
