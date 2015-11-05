package com.myftpserver.listener;

import com.myftpserver.PassiveServer;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class PassiveChannelCloseListener implements ChannelFutureListener 
{
	private PassiveServer passiveServer;
	public PassiveChannelCloseListener(PassiveServer passiveServer) {
		// TODO Auto-generated constructor stub
		this.passiveServer=passiveServer;
	}

	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		// TODO Auto-generated method stub
		System.out.println("Passive Mode Transfer channel is closed");
		this.passiveServer.stop();
	}

}
