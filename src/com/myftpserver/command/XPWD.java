package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.myftpserver.FtpCommandInterface;
import com.myftpserver.handler.FtpSession;

public class XPWD extends PWD implements FtpCommandInterface  {

	@Override
	public String helpMessage(FtpSession fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSession fs, ChannelHandlerContext ctx,String param, Logger logger) 
	{
		// TODO Auto-generated method stub
		super.execute(fs, ctx, param, logger);
	}

}
