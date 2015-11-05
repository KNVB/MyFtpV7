package com.myftpserver.command;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.handler.FtpSession;
import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

public class NOOP implements FtpCommandInterface {

	@Override
	public String helpMessage(FtpSession fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSession fs, ChannelHandlerContext ctx,String param, Logger logger) 
	{
		// TODO Auto-generated method stub
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getConfig().getFtpMessage("200_Ok"));
	}

}
