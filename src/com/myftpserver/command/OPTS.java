package com.myftpserver.command;
import com.myftpserver.*;
import com.myftpserver.handler.FtpSession;
import com.util.Utility;
import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;


public class OPTS implements FtpCommandInterface
{
	@Override
	public String helpMessage(FtpSession fs) {
		// TODO Auto-generated method stub
		return null;
	}
	public void execute(FtpSession fs,ChannelHandlerContext ctx,String param,Logger logger) 
	{
		// TODO Auto-generated method stub
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getConfig().getFtpMessage("200_Opt_Response"));
 	}

}
