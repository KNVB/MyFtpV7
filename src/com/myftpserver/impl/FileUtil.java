package com.myftpserver.impl;

import java.util.Stack;
import java.util.Locale;
import java.util.Calendar;
import java.util.TreeMap;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.exception.*;
import com.myftpserver.Configuration;
import com.myftpserver.handler.FtpSessionHandler;
/*
 * Copyright 2004-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 
 * @author SITO3
 *
 */
public class FileUtil 
{
	/**
	 * Get server path and it's permission
	 * @param logger message logger
	 * @param serverPathACL server path access control list
	 * @param inPath virtual path
	 * @return server path and it's permission
	 */
	public static String getServerPathPerm(Logger logger,TreeMap<String, String> serverPathACL, Path inPath) 
	{
		String tempResult=null,rootPath,restPath,pathPerm=new String(),path,paths[];
		rootPath=inPath.getRoot().toString();
		tempResult=serverPathACL.get(rootPath);
		if (tempResult!=null)
		{
			if (tempResult.endsWith("\t")||tempResult.endsWith("\tnull"))
			{
				pathPerm=tempResult.substring(0,tempResult.indexOf("\t")).trim();
			}
			else
			{
				pathPerm=tempResult.split("\t")[0].trim();
			}
		}
		
		restPath=rootPath;
		path=inPath.toString();
		if (rootPath.endsWith("\\"))
		{
			path=path.replaceAll(rootPath+File.separator, "");
			paths=path.split(File.separator+File.separator);
		}
		else
			paths=path.split("/"+File.separator);
		for (int i=0;i<paths.length;i++)
		{
			if (restPath.endsWith(File.separator))
				restPath+=paths[i];
			else
				restPath+=File.separator+paths[i];
			tempResult=serverPathACL.get(restPath);
			logger.debug("restPath="+restPath+",tempResult="+pathPerm);
			if (tempResult!=null)
			{
				if (tempResult.endsWith("\t")||tempResult.endsWith("\tnull"))
				{
					pathPerm=tempResult.substring(0,tempResult.indexOf("\t")).trim();
				}
				else
				{
					pathPerm=tempResult.split("\t")[0].trim();
				}
			}
		}
		logger.debug("inPath="+inPath.toString()+",pathPerm="+pathPerm);
		return pathPerm;
	}		
	/**
	 * Get server path and it's permission from specified virtual path
	 * @param fs FtpSessionHandler
	 * @param inPath virtual path
	 * @return server path and it's permission
	 * @throws PathNotFoundException
	 */
	public static String getServerPathAndPermFromVirDir(FtpSessionHandler fs,String inPath) throws PathNotFoundException 
	{
		Logger logger;
		User user=fs.getUser();
		String restPath,pathPerm,paths[];
		Configuration config=fs.getConfig();
		String serverPath=null,result=null,tempResult=null;
		String clientPath=inPath,currentPath=fs.getCurrentPath();
		TreeMap<String, String> clientPathACL=user.getClientPathACL();
		
		logger=config.getLogger();
		if (clientPath.indexOf("/")==-1)
		{	
			clientPath=currentPath+clientPath;
		}
		if (clientPath.endsWith("/") && (!clientPath.equals("/")))
		{
			clientPath=clientPath.substring(0,clientPath.length()-1);
		}
		logger.debug("clientPath="+clientPath);
		restPath="/";
		tempResult=clientPathACL.get(restPath);
		if (tempResult==null)
		{
			throw new PathNotFoundException(config.getFtpMessage("530_Home_Dir_Not_Found"));
		}
		else
		{
			restPath="";
			pathPerm=tempResult.split("\t")[0];
			serverPath=tempResult.split("\t")[1];
			paths=clientPath.split("/");
			for (int i=1;i<paths.length;i++)
			{
				restPath+="/"+paths[i];
				tempResult=clientPathACL.get(restPath);
				logger.debug("restPath="+restPath+",tempResult="+tempResult);
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
		logger.debug("clientPath="+clientPath+",inPath="+inPath+",serverPath="+serverPath+",pathPerm="+pathPerm);
		return result;
	}
	/**
	 * Format the file/diretory information as specified in RFC
	 * @param path server path
	 * @return formatted output
	 * @throws IOException
	 */
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
	/**
	 * Normalize virtual path
	 * @param logger message logger
	 * @param currentPath current virtual path
	 * @param inPath incoming virtual path
	 * @return normalized virtual path
	 */
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
