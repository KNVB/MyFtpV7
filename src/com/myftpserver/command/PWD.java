package com.myftpserver.command;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.myftpserver.handler.FtpSession;
import com.util.Utility;

public class PWD implements com.myftpserver.FtpCommandInterface {

	@Override
	public String helpMessage(FtpSession fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSession fs, ChannelHandlerContext ctx, String param,Logger logger)	
	{
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), fs.getConfig().getFtpMessage("257_PWD").replaceAll("%1", fs.getCurrentPath()));
	}
	
}
