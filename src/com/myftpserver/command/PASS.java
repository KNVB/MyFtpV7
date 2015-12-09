package com.myftpserver.command;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.handler.*;
import com.myftpserver.exception.*;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.myftpserver.interfaces.UserManager;

public class PASS implements FtpCommandInterface 
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
		// TODO Auto-generated method stub
		Configuration config=fs.getConfig();
		String message=new String();
		if ((param==null) || (param.isEmpty()))
		{
			message=config.getFtpMessage("500_Null_Command");
		}
		else
		{
			UserManager um=config.getUserManager();
			try 
			{
				logger.debug("User name=" +fs.getUserName()+",param="+param+",(um==null)"+(um==null));
				User user=um.login(fs, param);
				message=config.getFtpMessage("230_Login_Ok").replaceAll("%1", fs.getUserName());
				fs.setUser(user);
				fs.setIsLogined(true);
				fs.setCurrentPath("/");
			} 
			catch (AccessDeniedException | InvalidHomeDirectoryException | LoginFailureException e) 
			{
				// TODO Auto-generated catch block
				message=e.getMessage();
			}
		}
		Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(), message);
	}
}
