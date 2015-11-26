package com.myftpserver.command;

import com.util.Utility;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

public class TYPE implements FtpCommandInterface 
{

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param, Logger logger) 
	{
		if (fs.isLogined())
		{
			param=param.trim().toUpperCase();
			switch (param)
			{
				case "I":
				case "A":fs.setTransferMode(param);
						 Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), fs.getConfig().getFtpMessage("200_Transfer_Set"));
						 break;
				default:
					Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), fs.getConfig().getFtpMessage("504_Command_Not_Support_This_Parameter"));
					break;
			}
		}
	}

}

