package com.myftpserver.listener;

import com.myftpserver.handler.FtpSessionHandler;

import org.apache.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class SessionClosureListener implements ChannelFutureListener  
{
	FtpSessionHandler fs=null;
	Logger logger=null;
	Channel ch; 
	String remoteIp=new String(),ftpMessage=new String();
	
	public SessionClosureListener(FtpSessionHandler fs,Channel ch, Logger logger, String remoteIp,String goodByeMsg) 
	{
		this.logger=logger;
		this.remoteIp=remoteIp;
		this.ftpMessage=goodByeMsg;
		this.fs=fs;
		this.ch=ch;
	}

	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		logger.info("Message:"+ftpMessage+" sent to:"+remoteIp);
		if (fs!=null)
		{
			fs.close();
			fs=null;
		}
		else
		{	
			this.ch.close();
			this.ch=null;
		}		
	}

}
