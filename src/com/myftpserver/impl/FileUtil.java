package com.myftpserver.impl;

import com.myftpserver.User;
import com.myftpserver.Configuration;
import com.myftpserver.handler.FtpSessionHandler;

import java.io.File;
import java.util.Stack;
import java.util.Locale;
import java.util.TreeMap;
import java.util.Calendar;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

public class FileUtil 
{
	/**
	 * Remove duplicate and normalize the inPath 
	 * @param logger
	 * @param currentPath
	 * @param inPath
	 * @return the normalized path
	 */
	protected static final String normalizeClientPath(Logger logger,String currentPath,String inPath)
    {        
		Stack<String> pathStack=new Stack<String>();
        String result=new String();
        
        if (!inPath.startsWith("/"))
        	inPath=currentPath+"/"+inPath;
        logger.debug("currentPath="+currentPath+",inPath="+inPath);
        String temp[]=inPath.split("/");
        for (int i=1;i<temp.length;i++)
        {
                switch (temp[i])
                {
                        case "..":if (pathStack.size()>0)
                                                {
                                                        pathStack.pop();
                                                }
                                          break;
                        case "":                  
                        case ".":break;
                        default:if (temp[i].indexOf("...")==-1)
                                        {
                                                pathStack.push(temp[i]);
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
	/**
	 * Generate a directory list in FTP client accepted format
	 * @param path
	 * @return directory list
	 * @throws IOException
	 */
	protected static final String formatPathName(Path path) throws IOException
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
	protected static final String getServerPathAndPermFromVirDir(FtpSessionHandler fs, String inPath) 
	{
		User user=fs.getUser();
		Configuration config=fs.getConfig();
		Logger logger=config.getLogger();
		String clientPath=inPath;
		String restPath,paths[],serverPath,result=null;
		String currentPath=fs.getCurrentPath(),tempResult,pathPerm;
		TreeMap<String, String> clientPathACL=user.getClientPathACL();
		
		if (clientPath.indexOf("/")==-1)
		{
			clientPath=currentPath+clientPath;
		}
		else	
		{
			if (clientPath.endsWith("/") && (!clientPath.equals("/")))
			{
				clientPath=clientPath.substring(0,clientPath.length()-1);
			}
		}
		logger.debug("0 clientPath="+clientPath);
		restPath="/";
		paths=clientPath.split("/");
		tempResult=clientPathACL.get(restPath);
		if (tempResult!=null)
		{
			restPath="";
			pathPerm=tempResult.split("\t")[0].trim();
			serverPath=tempResult.split("\t")[1].trim();
			for (int i=1;i<paths.length;i++)
			{
				restPath+="/"+paths[i];
				tempResult=clientPathACL.get(restPath);
				logger.debug("restPath="+restPath+",temp["+i+"]="+paths[i]+",tempResult="+tempResult);
				if (tempResult==null)
				{
					serverPath+=File.separator+paths[i];
				}
				else
				{
					if (tempResult.endsWith("\t")||tempResult.endsWith("\tnull"))
					{
						pathPerm=tempResult.substring(0,tempResult.indexOf("\t")).trim();
						serverPath+=File.separator+paths[i];
					}
					else
					{
						pathPerm=tempResult.split("\t")[0].trim();
						serverPath=tempResult.split("\t")[1].trim();
					}
				}
			}
			result=serverPath+"\t"+pathPerm;
		}
		return result;
	}

	/**
	 * Get Server Path permission
	 * @param logger
	 * @param serverPathACL The specified server path access control list 
	 * @param inPath The server path
	 * @return 
	 */
	protected static final String getServerPathPerm(Logger logger,TreeMap<String, String> serverPathACL,Path inPath)
	{
			String pathStrings[];
			String separator="/",pathPerm=null;
			String pathString,pathKey=new String(),tempPerm;
			
			pathString=inPath.toString();
			if (File.separator.equals(separator))
				pathStrings=pathString.split(separator);
			else
				pathStrings=pathString.split(File.separator+File.separator);
			
			for (int i=0;i<pathStrings.length;i++)
			{	
				switch (i) 
				{
					case 0:pathKey+=pathStrings[i]+File.separator;
							break;
					case 1:pathKey+=pathStrings[i];
							break;	
					default:pathKey+=File.separator+pathStrings[i];
							break;
				}
				tempPerm=serverPathACL.get(pathKey);
				logger.debug("pathKey="+pathKey+",tempPerm="+tempPerm);
				if (tempPerm!=null)
				{
					pathPerm=tempPerm.split("\t")[0];
				}
			}
			logger.debug("pathKey="+pathKey+",pathPerm="+pathPerm);
			return pathPerm;
	}	
}
