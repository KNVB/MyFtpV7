package com.myftpserver.impl;

import java.util.Stack;
import java.util.Locale;
import java.util.Calendar;
import java.util.TreeMap;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.handler.FtpSessionHandler;

public class FileUtil 
{
	public static String getServerPathPerm(Logger logger,TreeMap<String, String> serverPathACL, Path path) 
	{
		// TODO Auto-generated method stub
		return null;
	}		

	public static String getServerPathAndPermFromVirDir(FtpSessionHandler fs,String inPath) 
	{
		User user=fs.getUser();
		Logger logger=fs.getConfig().getLogger();
		String clientPath=inPath,currentPath=fs.getCurrentPath();
		TreeMap<String, String> clientPathACL=user.getClientPathACL();
		if (clientPath.indexOf("/")==-1)
		{	
			clientPath=currentPath+clientPath;
		}
		if (clientPath.endsWith("/") && (!clientPath.equals("/")))
		{
			clientPath=clientPath.substring(0,clientPath.length()-1);
		}
		logger.debug("clientPath="+clientPath);
		return null;
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
	public static final String normalizeClientPath(Logger logger,String currentPath,String inPath)
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
}
