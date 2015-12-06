package com.myftpserver.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.myftpserver.Configuration;
import com.myftpserver.User;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;

public class Utility 
{
	protected static final boolean isReadableServerPath(Logger logger,Hashtable<Path, String> serverPathACL,Path p)
	{
		String root;
		boolean result=true;
		String permission=null;
		if (Files.isDirectory(p))
		{
			root=p.getRoot().toString();
			logger.debug("root="+root);
		}
		else
		{
			if (serverPathACL.containsKey(p))
			{
				permission=serverPathACL.get(p);
				if (permission.indexOf(FileManager.NO_ACCESS)>-1)
					result=false;
			}
		}
		return result;
	}
	/*protected static final boolean isReadableServerPath(Logger logger,Hashtable<Path, String> serverPathACL,Path p)
	{
		Path aclPath;
		boolean result=true;
		String permission=null;
		if (Files.isDirectory(p))
		{
			Enumeration<Path> serverPaths=serverPathACL.keys();
			while (serverPaths.hasMoreElements())
			{
				aclPath=serverPaths.nextElement();
				if (p.startsWith(aclPath))
				{
					permission=serverPathACL.get(aclPath);
					logger.debug("aclPath="+aclPath+",server path permission="+permission);
					if (permission.indexOf(FileManager.NO_ACCESS)>-1)
						result=false;
				}
			}
		}
		else
		{
			if (serverPathACL.containsKey(p))
			{
				permission=serverPathACL.get(p);
				if (permission.indexOf(FileManager.NO_ACCESS)>-1)
					result=false;
			}
		}
		return result;
	}*/
	protected static final String resolveClientPath(Logger logger,String currentPath,String inPath)
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
	protected static final String getRealPath(FtpSessionHandler fs,String inPath,String permission) throws AccessDeniedException, PathNotFoundException
	{
		int resultCode=-1,i;
		
		User user=fs.getUser();
		Configuration config=fs.getConfig();		
		String clientPath=inPath;
		String temp[]=clientPath.split("/");
		String currentPath=fs.getCurrentPath(),tempResult;
		String result=null,pathPerm=new String(),restPath=new String();
		Logger logger=config.getLogger();
		
				
		Hashtable<String, String> clientPathACL=user.getClientPathACL();
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
		switch (permission)
		{
			case FileManager.WRITE_PERMISSION: result=clientPathACL.get(clientPath);
											   if (result==null)	
												   resultCode=FileManager.ACCESS_DENIED;	
											   break;	
			case FileManager.READ_PERMISSION: 
											  restPath="/";
											  tempResult=clientPathACL.get(restPath);
											  if (tempResult==null)
											  {
												  resultCode=FileManager.HOME_DIR_NOT_FOUND;
											  }
											  else
											  {
												  pathPerm=tempResult.split("\t")[0];
												  result=tempResult.split("\t")[1];
												  restPath="";
												  for (i=1;i<temp.length;i++)
												  {
													  restPath+="/"+temp[i];
													  tempResult=clientPathACL.get(restPath);
													  logger.debug("restPath="+restPath+",temp["+i+"]="+temp[i]+",tempResult="+tempResult);
													  if (tempResult==null)
													  {
														  result+=File.separator+temp[i];
													  }
													  else
													  {
														  if (tempResult.endsWith("\t")||tempResult.endsWith("\tnull"))
														  {
															  pathPerm=tempResult.substring(0,tempResult.indexOf("\t")).trim();
															  result+=File.separator+temp[i]; 
														  }
														  else
														  {  
															pathPerm=tempResult.split("\t")[0].trim();
														  	result=tempResult.split("\t")[1].trim();
														  }
													  }
												  }
												  if ((pathPerm.indexOf(FileManager.NO_ACCESS)>-1)||(pathPerm.indexOf(FileManager.READ_PERMISSION)==-1))
													  resultCode=FileManager.ACCESS_DENIED; 
												  else
												  {
													  if (result==null)
													  {
														  resultCode=FileManager.PATH_NOT_FOUND;
													  }
													  else
														  resultCode=FileManager.ACCESS_OK;
												  }
												  logger.debug("clientPath="+clientPath+",pathPerm="+pathPerm+",result="+result);
											  }
		}
		switch (resultCode)
		{
			case  FileManager.ACCESS_DENIED:throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
			case  FileManager.PATH_NOT_FOUND:throw new PathNotFoundException(config.getFtpMessage("450_Directory_Not_Found"));
		}
		return result;
	}
	protected static void addVirtualDirectoryName(Logger logger,String currentPath,Hashtable<String, String> clientPathACL, ArrayList<String> nameList) 
	{
		int index;
		String virDir,parentDir,pathPerm;
		Enumeration<String> clientPaths=clientPathACL.keys();
		while (clientPaths.hasMoreElements())
		{
			virDir=clientPaths.nextElement();
			if (virDir.equals("")||virDir.equals(currentPath))
				continue;
			else
			{
				index=virDir.lastIndexOf("/");
				parentDir=virDir.substring(0,index+1);
				pathPerm=clientPathACL.get(virDir);
				index=pathPerm.indexOf("\t");
				pathPerm=pathPerm.substring(0,index).trim();
				logger.debug("Current Path="+currentPath+",Parent folder="+parentDir+",pathPerm="+pathPerm);
				if ((pathPerm.indexOf(FileManager.NO_ACCESS)==-1) && parentDir.equals(currentPath)||parentDir.equals(currentPath+"/"))
				{
					virDir=virDir.replaceAll(currentPath, "");
					index=virDir.indexOf("/");
					if (index==0)
						virDir=virDir.substring(index+1);
					logger.debug("Current Path="+currentPath+",Virtual Client Path="+virDir+",Parent folder="+parentDir+",index="+index);
					if (!nameList.contains(virDir))
					{	
						nameList.add(virDir);
						logger.debug("Current Path="+currentPath+",Virtual Client Path="+virDir+",Parent folder="+parentDir+" added to dir name list");
					}
				}
			}
			
		}
	}
	protected static void addVirtualDirectoryList(FtpSessionHandler fs,Hashtable<String, String> clientPathACL,DbOp dbo,TreeMap<String,String> nameList)  
	{
		int index;
		String virDir,parentDir,currentPath=fs.getCurrentPath(),serverPath,pathPerm;
		Enumeration<String> clientPaths=clientPathACL.keys();
		while (clientPaths.hasMoreElements())
		{
			virDir=clientPaths.nextElement();
			if (virDir.equals("")||virDir.equals(currentPath))
				continue;
			else
			{
				index=virDir.lastIndexOf("/");
				parentDir=virDir.substring(0,index+1);
				pathPerm=clientPathACL.get(virDir);
				index=pathPerm.indexOf("\t");
				pathPerm=pathPerm.substring(0,index).trim();
				if ((pathPerm.indexOf(FileManager.NO_ACCESS)==-1) && parentDir.equals(currentPath)||parentDir.equals(currentPath+"/"))
				{
					try
					{
						serverPath=dbo.getRealPath(fs, virDir, FileManager.READ_PERMISSION);
						virDir=virDir.replaceAll(currentPath, "");
						index=virDir.indexOf("/");
						if (index==0)
							virDir=virDir.substring(index+1);
						fs.getConfig().getLogger().debug("Current Path="+currentPath+",Virtual Client Path="+virDir+",Parent folder="+parentDir+",serverPath="+serverPath+",index="+index);
						nameList.put(virDir,Utility.formatPathName(Paths.get(serverPath)));
					}
					catch (AccessDeniedException |PathNotFoundException| IOException ex)
					{
						
					}
				}
			}
			
		}
		
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
	
}
