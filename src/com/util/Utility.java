package com.util;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;

import org.apache.log4j.Logger;

import com.myftpserver.PassiveServer;
import com.myftpserver.handler.FtpSession;
import com.myftpserver.listener.SessionClosureListener;
import com.myftpserver.listener.CommandCompleteListener;
import com.myftpserver.listener.SendFileNameListCompleteListener;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

public class Utility
{
	public static void sendMessageToClient(Channel ch, Logger logger,String remoteIp,String ftpMessage) 
	{
		// TODO Auto-generated method stub
		//ch.writeAndFlush(Unpooled.copiedBuffer(ftpMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new SendResponseToUserListener(logger,remoteIp,ftpMessage));
		ch.writeAndFlush(Unpooled.copiedBuffer(ftpMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new CommandCompleteListener(logger,remoteIp,ftpMessage));
	}
	public static void sendFileNameList(Channel ch,ChannelHandlerContext responseCtx,StringBuilder fileNameList,FtpSession fs,PassiveServer passiveServer)
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(fileNameList.toString(),CharsetUtil.UTF_8)).addListener(new SendFileNameListCompleteListener(fs,responseCtx,passiveServer));
	}		
	public static void disconnectFromClient(FtpSession fs, Logger logger,String remoteIp,String goodByeMessage)
	{
		fs.getChannel().writeAndFlush(Unpooled.copiedBuffer(goodByeMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new SessionClosureListener(fs,null,logger,remoteIp,goodByeMessage));
	}
	public static void disconnectFromClient(Channel ch, Logger logger,String remoteIp,String goodByeMessage)
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(goodByeMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new SessionClosureListener(null,ch,logger,remoteIp,goodByeMessage));
	}
	public static final String formatPathName(Path path) throws IOException
	{
		int thisYear,node=1;
		Locale fileLocale=new Locale("en");
		String user="user",group="group";
		String dateString,permission;
		GregorianCalendar fileDate=new GregorianCalendar();
		thisYear=fileDate.get(Calendar.YEAR);
		if (Files.isDirectory(path))
        	permission="d---------";
        else
        	permission="----------";
        fileDate.setTimeInMillis(Files.getLastModifiedTime(path).toMillis());
        if (thisYear==fileDate.get(Calendar.YEAR))
        	dateString=(new SimpleDateFormat("MMM dd HH:mm",fileLocale).format(fileDate.getTime()));
        else
        	dateString=(new SimpleDateFormat("MMM dd yyyy",fileLocale).format(fileDate.getTime()));
        return String.format("%s%5d %-9s%-9s%10d %-12s ",permission,node,user,group,Files.size(path),dateString);
	}
	public static final String resolveClientPath(Logger logger,String currentPath,String inPath)
    {        
		Stack<String> pathStack=new Stack<String>();
        String result=new String();
        logger.debug("currentPath="+currentPath+",inPath="+inPath);
        if (!inPath.startsWith("/"))
        	inPath=currentPath+"/"+inPath;
        for (String temp:inPath.split("/"))
        {
                switch (temp)
                {
                        case "..":if (pathStack.size()>0)
                                                {
                                                        pathStack.pop();
                                                }
                                          break;
                        case "":                  
                        case ".":break;
                        default:if (temp.indexOf("...")==-1)
                                        {
                                                pathStack.push(temp);
                                        }
                }
                logger.debug("currentPath="+currentPath+",inPath="+inPath);
        }
        for (Enumeration<String> e=pathStack.elements();e.hasMoreElements();)
        {
                result=result+"/"+e.nextElement();
        }
        if (result.equals(""))
        	result="/";
        return result;
    }	
	public static final String getSystemType(Logger logger)
	{
		 String loc = System.getProperty("user.timezone");
	        final int p = loc.indexOf("/");
	        if (p > 0) {
	            loc = loc.substring(0, p);
	        }
	        loc = loc + "/"+ System.getProperty("user.language");
	        String result=System.getProperty("os.arch") + " "
	                + System.getProperty("os.name") + " "
	                + System.getProperty("os.version") + ", " + loc;
	        logger.debug("System type="+result);
	        return result;
	}
	
}