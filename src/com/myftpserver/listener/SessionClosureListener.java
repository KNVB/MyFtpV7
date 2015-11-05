package com.myftpserver.listener;

import org.apache.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import com.myftpserver.handler.FtpSession;



public class SessionClosureListener implements ChannelFutureListener  
{
	FtpSession fs=null;
	Logger logger=null;
	Channel ch; 
	String remoteIp=new String(),ftpMessage=new String();
	
	public SessionClosureListener(FtpSession fs,Channel ch, Logger logger, String remoteIp,String goodByeMsg) 
	{
		// TODO Auto-generated constructor stub
		this.logger=logger;
		this.remoteIp=remoteIp;
		this.ftpMessage=goodByeMsg;
		this.fs=fs;
		this.ch=ch;
	}

	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		// TODO Auto-generated method stub
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
