package com.myftpserver.impl;

import com.myftpserver.User;
import com.myftpserver.Configuration;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Locale;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

public class Utility 
{
	/**
	 * Determine whether inPath writeadable for user
	 * @param logger
	 * @param serverPathACL
	 * @param inPath
	 * @return true if the inPath is writeable.
	 * @throws PathNotFoundException 
	 */
	/*protected static final boolean isWritableServerPath(FtpSessionHandler fs,TreeMap<String, String> serverPathACL,Path inPath) throws PathNotFoundException
	{
		boolean result=false;
		String pathPerm=new String();
		Logger logger=fs.getConfig().getLogger();
		if (Files.exists(inPath,new LinkOption[]{ LinkOption.NOFOLLOW_LINKS}))
		{
			pathPerm=getServerPathPerm(logger,serverPathACL,inPath);
			if (pathPerm.indexOf(FileManager.WRITE_PERMISSION)>-1)
				result=true;
			return result;
		}
		else
			throw new PathNotFoundException(fs.getConfig().getFtpMessage("450_Directory_Not_Found"));
	}*/
	/**
	 * Determine whether inPath readable for user
	 * @param logger
	 * @param serverPathACL
	 * @param inPath
	 * @return true if the inPath is readable.
	 * @throws PathNotFoundException 
	 */
	protected static final boolean isReadableServerPath(FtpSessionHandler fs,TreeMap<String, String> serverPathACL,Path inPath) throws PathNotFoundException
	{
		boolean result=true;
		String pathPerm=new String();
		Logger logger=fs.getConfig().getLogger();
		pathPerm=getServerPathPerm(logger,serverPathACL,inPath);
		if (Files.exists(inPath,new LinkOption[]{ LinkOption.NOFOLLOW_LINKS}))
		{
			if (pathPerm.indexOf(FileManager.NO_ACCESS)>-1)
				result=false;
			return result;
		}
		else
			throw new PathNotFoundException(fs.getConfig().getFtpMessage("450_Directory_Not_Found"));
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
			String separator="/",pathPerm=new String();
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
	/**
	 * Remove duplicate and normalize the inPath 
	 * @param logger
	 * @param currentPath
	 * @param inPath
	 * @return the normalized path
	 */
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
	/**
	 * Get the server path that corresponding to virtual path (i.e. inPath)
	 * @param fs
	 * @param inPath
	 * @param permission
	 * @return the server path that corresponding to virtual path (i.e. inPath)
	 * @throws AccessDeniedException
	 * @throws PathNotFoundException
	 */
	protected static final String getRealPath(FtpSessionHandler fs,String inPath,String permission) throws AccessDeniedException, PathNotFoundException
	{
		int i,resultCode=-1;
		User user=fs.getUser();
		Configuration config=fs.getConfig();
		Logger logger=fs.getConfig().getLogger();
		String clientPath=inPath;
		String result=null,restPath,paths[];
		String currentPath=fs.getCurrentPath(),tempResult,pathPerm;
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
		restPath="/";
		paths=clientPath.split("/");
		tempResult=clientPathACL.get(restPath);
		if (tempResult==null)
		{
			resultCode=FileManager.HOME_DIR_NOT_FOUND;
		}
		else
		{
			restPath="";
			pathPerm=tempResult.split("\t")[0].trim();
			result=tempResult.split("\t")[1].trim();
			for (i=1;i<paths.length;i++)
			{
				restPath+="/"+paths[i];
				tempResult=clientPathACL.get(restPath);
				logger.debug("restPath="+restPath+",temp["+i+"]="+paths[i]+",tempResult="+tempResult);
				if (tempResult==null)
				{
					result+=File.separator+paths[i];
				}
				else
				{
					if (tempResult.endsWith("\t")||tempResult.endsWith("\tnull"))
					{
						pathPerm=tempResult.substring(0,tempResult.indexOf("\t")).trim();
						result+=File.separator+paths[i];
					}
					else
					{
						pathPerm=tempResult.split("\t")[0].trim();
						result=tempResult.split("\t")[1].trim();
					}
				}
			}
			if (pathPerm.indexOf(FileManager.NO_ACCESS)>-1)
				resultCode=FileManager.ACCESS_DENIED; 
			else
			{
				if (permission.equals(FileManager.WRITE_PERMISSION) && (pathPerm.indexOf(FileManager.WRITE_PERMISSION)==-1))
					resultCode=FileManager.ACCESS_DENIED; 
				else
					resultCode=FileManager.ACCESS_OK;
			}
			logger.debug("clientPath="+clientPath+",pathPerm="+pathPerm+",result="+result);
		}
	if (resultCode==FileManager.ACCESS_DENIED)
		throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
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
