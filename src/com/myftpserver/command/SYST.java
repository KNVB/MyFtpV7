package com.myftpserver.command;
import io.netty.channel.ChannelHandlerContext;
import com.myftpserver.handler.FtpSessionHandler;
import com.util.Utility;

import org.apache.log4j.Logger;
 
public class SYST implements com.myftpserver.interfaces.FtpCommandInterface
{
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param,Logger logger) 
	{
		// TODO Auto-generated method stub
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), fs.getConfig().getFtpMessage("215_System_Type")+" "+ Utility.getSystemType(logger));
	}



	

}
