package com.myftpserver.impl;

import java.util.Stack;
import java.util.Locale;
import java.util.Calendar;
import java.util.TreeMap;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.exception.*;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.abstracts.FileManager;
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
	 * File Utility
	 */
	public FileUtil()
	{
		
	}
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
			path=path.replace(rootPath+File.separator, "");
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
		String serverPath=null,result=null,tempResult=null;
		String clientPath=inPath,currentPath=fs.getCurrentPath();
		TreeMap<String, String> clientPathACL=user.getClientPathACL();
		
		logger=fs.getLogger();
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
			throw new PathNotFoundException(fs.getFtpMessage("530_Home_Dir_Not_Found"));
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
	 * Delete directory recursively
	 * @param path the path will be deleted
	 * @return true if and only if the directory is successfully deleted; false otherwise 
	 */	
	public static boolean deleteDirectory(File path) 
	{
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return(path.delete());
	}
	public void getRealHomePath(FtpSessionHandler fs)throws AccessDeniedException,InvalidHomeDirectoryException 
	{
		try
		{
			getServerPath(fs,"/",FileManager.READ_PERMISSION);
		}
		catch (AccessDeniedException err)
		{
			throw new AccessDeniedException(fs.getFtpMessage("550_Permission_Denied"));
		}
		catch (PathNotFoundException err)
		{
			throw new InvalidHomeDirectoryException(fs.getFtpMessage("530_Home_Dir_Not_Found"));
		}
	}
	/**
	 * Get server path mapping for virtual path
	 * @param fs FtpSessionHandler
	 * @param inPath virtual path
	 * @param requiredPermission Required Permission
	 * @return the server path 
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 */
	public static final String getServerPath(FtpSessionHandler fs,String inPath, String requiredPermission)throws AccessDeniedException,PathNotFoundException
	{
		Logger logger=fs.getLogger();
		String serverPath=new String(),serverPathPerm=null,virtualPathPerm=null,finalPerm=null;
		String clientPath=FileUtil.normalizeClientPath(logger, fs.getCurrentPath(), inPath);
		String serverPathAndPerm=FileUtil.getServerPathAndPermFromVirDir(fs,clientPath);
		if (serverPathAndPerm.isEmpty())
		{
			throw new PathNotFoundException(fs.getFtpMessage("450_Directory_Not_Found"));
		}
		else
		{
			serverPath=serverPathAndPerm.split("\t")[0];
			virtualPathPerm=serverPathAndPerm.split("\t")[1];
			serverPathPerm=FileUtil.getServerPathPerm(logger,fs.getUser().getServerPathACL(),Paths.get(serverPath));
			//logger.debug("virtualPath="+clientPath+",virtualPathPerm="+virtualPathPerm+",serverPath="+serverPath+",serverPathPerm="+serverPathPerm);
			if (virtualPathPerm!=null)
				finalPerm=virtualPathPerm;
			if (serverPathPerm!=null)
				finalPerm+=serverPathPerm;
			logger.debug("virtualPath="+clientPath+",virtualPathPerm="+virtualPathPerm+",serverPath="+serverPath+",serverPathPerm="+serverPathPerm+",finalPerm="+finalPerm);
			
			if (!Files.exists(Paths.get(serverPath),new LinkOption[]{ LinkOption.NOFOLLOW_LINKS}))
			{
				throw new PathNotFoundException(fs.getFtpMessage("450_Directory_Not_Found"));
			}
			else
			{
				if ((finalPerm==null) || finalPerm.indexOf(FileManager.NO_ACCESS)>-1||finalPerm.indexOf(requiredPermission)==-1)
				{
					throw new AccessDeniedException(fs.getFtpMessage("550_Permission_Denied"));
				}
			}
		}
		return serverPath;
	}
	/**
	 * Get Parent Directory of given directory
	 * @param inPath directory
	 * @return the parent directory
	 */
	public static final String getParentDirectory(String inPath)
	{
		String parentDir;
		int index=inPath.lastIndexOf("/");
		parentDir=inPath.substring(0,index);
		return parentDir;
	}
	/**
	 * Generate an future server path from a virtual path
	 * @param fs FtpSessionHandler
	 * @param inPath virtual path
	 * @return an future server path
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 */
	public static final String getFutureServerPath(FtpSessionHandler fs,String inPath) throws AccessDeniedException, PathNotFoundException
	{
		String parentDir=FileUtil.getParentDirectory(inPath);
		String futureServerPath=FileUtil.getServerPath(fs,parentDir,FileManager.WRITE_PERMISSION);
		String newDirName=inPath.replace(parentDir,"");

		if (futureServerPath.endsWith(File.separator))
			futureServerPath=futureServerPath.substring(0, futureServerPath.length()-1);
		if (newDirName.startsWith("/"))
			newDirName=newDirName.substring(1);
		futureServerPath+=File.separator+newDirName;
		return futureServerPath;
	}
}
