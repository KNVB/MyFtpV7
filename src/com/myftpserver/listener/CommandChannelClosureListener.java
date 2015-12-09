package com.myftpserver.listener;

import com.myftpserver.MyFtpServer;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class CommandChannelClosureListener implements ChannelFutureListener
{
	MyFtpServer s;
	Logger logger; 
	String remoteIp,ftpMessage;
	public CommandChannelClosureListener(MyFtpServer t)
	{
		s=t;
	}
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		// TODO Auto-generated method stub
		s.sessionClose();
	}
}
