package com.myftpserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import org.apache.log4j.Logger;

import com.myftpserver.handler.FtpSession;
import com.util.Utility;

public class FtpCommandHandler 
{
	FtpSession thisSession=null;
	Configuration config=null;
	org.apache.log4j.Logger logger=null;
	Channel ch=null;
	public FtpCommandHandler (FtpSession fs)
	{
		this.thisSession=fs;
		this.config=fs.getConfig();
		this.logger=config.getLogger();	 
	}
	
	public void doCommand(ChannelHandlerContext ctx, String inString, Logger logger) 
	{
		// TODO Auto-generated method stub
		if (inString==null)
		{
			Utility.sendMessageToClient(ctx.channel(),logger,thisSession.getClientIp(),config.getFtpMessage("500_NULL_Command"));
		}
		else
		{
			String command=new String();
			String parameters=new String();
			int i=inString.indexOf(" ");
			if (i==-1)
			{	
				command=inString;
			}
			else
			{	
				command=inString.substring(0,i).trim().toUpperCase();
				parameters=inString.substring(i+1).trim();
			}
			logger.debug("Command="+command+",p="+parameters+",isLoggined="+thisSession.isLogined());
			if (this.thisSession.isLogined())
			{
				executeCommand(ctx,command,parameters);
			}
			else
			{
				//logger.debug(command.equals("QUIT"));
				switch (command)
				{
					case "USER":
					case "OPTS":
					case "QUIT":
					case "PASS":executeCommand(ctx,command,parameters);
								break;
					default:
							//Utility.sendMessageToClient(ctx.channel(),logger,thisSession.getClientIp(),config.getFtpMessage("530_Not_Login"));
							Utility.disconnectFromClient(ctx.channel(),logger,thisSession.getClientIp(),config.getFtpMessage("530_Not_Login"));
							break;	
				}
			}
		}		
	}
	private void executeCommand(ChannelHandlerContext ctx,String cmdString,String parameters)
	{
		FtpCommandInterface cmd;
		try
		{
			cmd=(FtpCommandInterface) Class.forName("com.myftpserver.command."+cmdString.toUpperCase()).newInstance();
			cmd.execute(thisSession,ctx,parameters,logger);
		}
		catch (InstantiationException | IllegalAccessException| ClassNotFoundException e) 
		{
			// TODO Auto-generated catch block
			logger.info(cmdString.toUpperCase()+" command not implemented");
			Utility.sendMessageToClient(ctx.channel(),logger,thisSession.getClientIp(),config.getFtpMessage("502_Command_Not_Implemeneted"));
		}
		catch (Exception err)
		{
			logger.debug(err.getMessage());
			err.printStackTrace();
		}
	}	
}
