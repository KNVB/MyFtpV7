package com.myftpserver.command;
import org.apache.log4j.Logger;

import com.util.Utility;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;

import io.netty.channel.ChannelHandlerContext;

public class QUIT implements FtpCommandInterface
{

	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void execute(FtpSessionHandler fs,ChannelHandlerContext ctx, String param, Logger logger) 
	{
		// TODO Auto-generated method stub
		//Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),fs.getConfig().getFtpMessage("221_Logout_Ok"));
		String goodByeMsg=fs.getConfig().getFtpMessage("221_Logout_Ok");
		String remoteIp=fs.getClientIp();
		Utility.disconnectFromClient(fs, logger, remoteIp, goodByeMsg);
		//ctx.channel().writeAndFlush(Unpooled.copiedBuffer(goodByeMsg,CharsetUtil.UTF_8)).addListener(new SessionClosureListener(fs,logger,remoteIp,goodByeMsg));
		
	}
}