package com.myftpserver.command;
import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.myftpserver.*;
import com.myftpserver.handler.FtpSessionHandler;
import com.util.Utility;
public class USER implements com.myftpserver.interfaces.FtpCommandInterface
{
	@Override
	public void execute(FtpSessionHandler fs,ChannelHandlerContext ctx, String param, Logger logger) 
	{
		// TODO Auto-generated method stub
		Configuration config=fs.getConfig();
		String message=new String();
		if (param ==null)
		{
			message=config.getFtpMessage("500_Null_Command");
		}
		else
		{
			message=config.getFtpMessage("331_Password_Required");
			message=message.replaceAll("%1", param);
			fs.setUserName(param);
		}
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);
	}
	@Override
	public String helpMessage(com.myftpserver.handler.FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

}