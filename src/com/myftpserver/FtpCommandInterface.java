package com.myftpserver;

import org.apache.log4j.Logger;
import com.myftpserver.handler.FtpSession;
import io.netty.channel.ChannelHandlerContext;


public interface FtpCommandInterface 
{
 	public String helpMessage(FtpSession fs);
	public void execute (FtpSession fs,ChannelHandlerContext ctx,String param,Logger logger); 
}