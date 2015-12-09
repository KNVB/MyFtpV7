package com.myftpserver.command;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.handler.*;
import com.myftpserver.interfaces.FtpCommandInterface;


public class PASV implements FtpCommandInterface 
{

	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx, String param,	Logger logger) 
	{
		int port;
		Configuration config=fs.getConfig();
		MyFtpServer server=fs.getServer();
		String message=new String(),localIP=((InetSocketAddress)ctx.channel().localAddress()).getAddress().getHostAddress();
		if (config.isSupportPassiveMode())
		{
			port=server.getNextPassivePort();
			if (port==-1)
				message=fs.getConfig().getFtpMessage("550_CANT_CONNECT_CLNT");
			else
			{	
				logger.debug("Port "+port+" is assigned.");
				fs.isPassiveModeTransfer=true;
				message=fs.getConfig().getFtpMessage("227_Enter_Passive_Mode");
				message=message.replaceAll("%1", localIP.replaceAll("\\.", ","));
				message=message.replaceAll("%2", String.valueOf(port/256));
				message=message.replaceAll("%3", String.valueOf(port % 256));
				PassiveServer ps=new PassiveServer(fs,localIP,port);
				fs.setPassiveServer(ps);
			}				
		}
		else
		{
			fs.isPassiveModeTransfer=false;
			message=fs.getConfig().getFtpMessage("502_Command_Not_Implemeneted");
		}
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);
	}
}
