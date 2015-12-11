package com.myftpserver.command;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.myftpserver.Configuration;
import com.myftpserver.MyFtpServer;
import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.util.Utility;

public class EPSV implements FtpCommandInterface 
{

	@Override
	public String helpMessage(FtpSessionHandler fs) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, ChannelHandlerContext ctx,String param, Logger logger) 
	{
		int port;
		String message=new String(),localIp=new String();
		
		MyFtpServer server=fs.getServer();
		Configuration config=fs.getConfig();
		if (config.isSupportPassiveMode())
		{
			port=server.getNextPassivePort();
			if (port==-1)
				message=fs.getConfig().getFtpMessage("550_CANT_CONNECT_CLNT");
			else
			{	
				message=fs.getConfig().getFtpMessage("229_EPSV_Ok");
				message=message.replaceAll("%1", String.valueOf(port));
				localIp=((InetSocketAddress)ctx.channel().localAddress()).getAddress().getHostAddress();
				fs.isPassiveModeTransfer=true;						
				PassiveServer ps=new PassiveServer(fs,localIp,port);
				fs.setPassiveServer(ps);
			}
		}
		else
		{
			message=fs.getConfig().getFtpMessage("502_Command_Not_Implemeneted");
		}
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);	
		
	}

}
