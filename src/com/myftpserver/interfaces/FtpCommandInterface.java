package com.myftpserver.interfaces;

import org.apache.log4j.Logger;
import com.myftpserver.handler.FtpSessionHandler;
import io.netty.channel.ChannelHandlerContext;


public interface FtpCommandInterface 
{
 	public String helpMessage(FtpSessionHandler fs);
	public void execute (FtpSessionHandler fs,ChannelHandlerContext ctx,String param,Logger logger); 
}