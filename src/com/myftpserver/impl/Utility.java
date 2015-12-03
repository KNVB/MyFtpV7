package com.myftpserver.impl;

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

import com.myftpserver.User;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;

public class Utility 
{
	protected static final boolean isReadableServerPath(Hashtable<Path, String> serverPathACL,Path p)
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
	}
	protected static final String resolveClientPath(Logger logger,String currentPath,String inPath)
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
	protected static final String getRealPath(FtpSessionHandler fs,String inPath,String permission) throws AccessDeniedException
	{
		int resultCode=-1,i;
		Logger logger=fs.getConfig().getLogger();
		User user=fs.getUser();
		String result=null,pathPerm=new String();
		String clientPath=inPath,restPath=new String();
		String currentPath=fs.getCurrentPath();
		
				
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
			case FileManager.READ_PERMISSION: boolean finished=false;
											  while (!finished)
											  {
												  result=clientPathACL.get(clientPath);
												  logger.debug("0.5 result="+result+",clientPath="+clientPath);
												  if (result==null)
												  {
													  logger.debug("1 clientPath="+clientPath);
													  if (clientPath.equals("/"))
														{
															resultCode=FileManager.ACCESS_DENIED;
															finished=true; 
														}
														else
														{
															i=clientPath.lastIndexOf("/");
															if (i==-1)
															{	
																clientPath=currentPath;
																logger.debug("2 clientPath="+clientPath);
															}
															else	
															{
																restPath=clientPath.substring(i)+restPath;
																if (i==0)
																	i=1;
																clientPath=clientPath.substring(0,i);
																logger.debug("3 clientPath="+clientPath+",restPath="+restPath);
															}
														}													  
											  	  }
												  else	  
												  {
													  pathPerm=result.split("\t")[0];
													  result=result.split("\t")[1];
													  if (pathPerm.indexOf(FileManager.NO_ACCESS)>-1)
													  {
														  resultCode=FileManager.ACCESS_DENIED;
														  finished=true;
													  }
													  else
													  {
														  if (result.equals("null"))
														  {
															  logger.debug("4 clientPath="+clientPath);
															  if (clientPath.equals("/"))
																{
																	resultCode=FileManager.ACCESS_DENIED;
																	finished=true; 
																}
																else
																{
																	i=clientPath.lastIndexOf("/");
																	if (i==-1)
																	{	
																		clientPath=currentPath;
																		logger.debug("5 clientPath="+clientPath);
																	}
																	else	
																	{
																		restPath=clientPath.substring(i)+restPath;
																		if (i==0)
																			i=1;
																		clientPath=clientPath.substring(0,i);
																		logger.debug("6 clientPath="+clientPath+",restPath="+restPath);
																	}
																}
														  }
														  else  
															  finished=true; 
													  }
												  }
											  }
			   								  break;
								   	
		}
		switch (resultCode)
		{
			case FileManager.ACCESS_DENIED:throw new AccessDeniedException(fs.getConfig().getFtpMessage("550_Permission_Denied"));
			default:
					logger.debug("result="+result+",pathPerm="+pathPerm+",restPath="+restPath);
					if (pathPerm.indexOf(FileManager.NO_ACCESS)>-1)
						throw new AccessDeniedException(fs.getConfig().getFtpMessage("550_Permission_Denied"));
					else
					{
						if (!restPath.equals(""))
							result+=restPath;
					}
		}
		
		logger.debug("result="+result+",restPath="+restPath);
		return result;
	}
	protected static void addVirtualDirectoryName(Logger logger,String currentPath,Hashtable<String, String> clientPathACL, ArrayList<String> nameList) 
	{
		int index;
		String virDir,parentDir;
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
				logger.debug("Current Path="+currentPath+",Parent folder="+parentDir);
				if (parentDir.equals(currentPath)||parentDir.equals(currentPath+"/"))
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
		String virDir,parentDir,currentPath=fs.getCurrentPath(),serverPath;
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
				if (parentDir.equals(currentPath)||parentDir.equals(currentPath+"/"))
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
