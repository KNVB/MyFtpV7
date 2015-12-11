package com.myftpserver.channelinitializer;
import org.apache.log4j.Logger;

import com.myftpserver.handler.*;
import com.myftpserver.PassiveServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class PassiveChannelInitializer extends ChannelInitializer<Channel>
{
	private Logger logger;
	private FtpSessionHandler fs;
	private PassiveServer passiveServer;
	public PassiveChannelInitializer(FtpSessionHandler fs,PassiveServer passiveServer)
	{
		this.fs=fs;
		this.passiveServer=passiveServer;
		this.logger=fs.getConfig().getLogger();
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		logger.debug("Passive server channel initialized.");
		passiveServer.setChannel(ch);
	}
}
