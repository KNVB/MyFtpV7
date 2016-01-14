package com.myftpserver.channelinitializer;

import org.apache.logging.log4j.Logger;

import com.myftpserver.DummyServer;
import com.myftpserver.handler.FtpSessionHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class DummyChannelInitializer extends ChannelInitializer<Channel> 
{
	private Logger logger;
	private FtpSessionHandler fs;
	private DummyServer dummyServer;
	
	public DummyChannelInitializer(FtpSessionHandler fs,DummyServer dummyServer) 
	{
		this.dummyServer=dummyServer;
		this.logger=fs.getLogger();
	}

	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		ch.close();
		dummyServer.stop();
		this.dummyServer=null;
		logger.info("Dummy channel is close.");
	}

}
