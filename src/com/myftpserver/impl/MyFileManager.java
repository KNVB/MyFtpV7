package com.myftpserver.impl;

import com.myftpserver.User;
import com.myftpserver.exception.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Collections;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;

import org.apache.logging.log4j.Logger;
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
public class MyFileManager extends FileManager 
{
	/**
	 * An User Manager implementation
	 * @param logger Message logger
	 */
	public MyFileManager(Logger logger) 
	{
		super(logger);
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
	public String getServerPath(FtpSessionHandler fs,String inPath, String requiredPermission)throws AccessDeniedException,PathNotFoundException
	{
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
			logger.debug("virtualPath="+clientPath+",virtualPathPerm="+virtualPathPerm+",serverPath="+serverPath+",serverPathPerm="+serverPathPerm);
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
	@Override
	public long getPathSize(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException 
	{
		String serverPath=getServerPath(fs,inPath,FileManager.READ_PERMISSION);
		long pathSize=new File(serverPath).length();
		return pathSize;
	}
	@Override
	public void changeDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException 
	{
		getServerPath(fs,inPath,FileManager.EXECUTE_PERMISSION);
		fs.setCurrentPath(FileUtil.normalizeClientPath(logger, fs.getCurrentPath(), inPath));
	}
	@Override
	public StringBuffer getFullDirList(FtpSessionHandler fs, String inPath)	throws AccessDeniedException,NotADirectoryException,PathNotFoundException,InterruptedException 
	{
		int index;
		User user=fs.getUser();
		TreeMap<String,String> result=null;
		ArrayList <String>resultList=null;
		String virPath=new String(),parentDir;
		StringBuffer fileNameList=new StringBuffer();
		TreeMap<String, String> clientPathACL=user.getClientPathACL();
		String serverPath=getServerPath(fs,inPath,FileManager.READ_PERMISSION);
		String currentPath=FileUtil.normalizeClientPath(logger, fs.getCurrentPath(), inPath);
		if (File.separator.equals("\\"))
			result=new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
		else
			result=new TreeMap<String,String>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(serverPath))) 
		{
			for (Path path : directoryStream) //Loop all server path
            {
				try
				{
					if (currentPath.endsWith("/"))
						getServerPath(fs,currentPath+path.getFileName(),FileManager.READ_PERMISSION);
					else
						getServerPath(fs,currentPath+"/"+path.getFileName(),FileManager.READ_PERMISSION);
					result.put(path.getFileName().toString(),FileUtil.formatPathName(path));
				}
				catch (AccessDeniedException | PathNotFoundException err)
				{
					
				}
            }
			for(Entry<String, String> entry : clientPathACL.entrySet())  //Loop all virtual path
			{
				virPath=entry.getKey();
				index=virPath.lastIndexOf("/");
				parentDir=virPath.substring(0,index+1);
				if (parentDir.equals(currentPath)||parentDir.equals(currentPath+"/"))
				{
					try
					{
						serverPath=getServerPath(fs,virPath,FileManager.READ_PERMISSION);
						logger.debug("0 virPath="+virPath);
						virPath=virPath.replaceAll(currentPath, "");
						logger.debug("1 virPath="+virPath);
						index=virPath.indexOf("/");
						if (index==0)
							virPath=virPath.substring(index+1);
						virPath=virPath.trim();
						if (!virPath.equals("") && (!result.containsKey(virPath)))							
						{
							result.put(virPath,FileUtil.formatPathName(Paths.get(serverPath)));
							logger.debug("virPath="+virPath+" added to result");
						}
					}
					catch(AccessDeniedException | PathNotFoundException err)
					{
					}
				}
			}
			resultList=new ArrayList<String>(result.keySet());
			Collections.sort(resultList);
			for (String temp :resultList)
			{
				fileNameList.append(result.get(temp)+temp+"\r\n");
			}
		}
		catch (NotDirectoryException err)
		{
			String message=fs.getFtpMessage("550_Not_A_Directory");
			message=message.replaceAll("%1", inPath);
			throw new NotADirectoryException(message);
		}
		catch (NoSuchFileException ex)
		{
			throw new PathNotFoundException(fs.getFtpMessage("450_Directory_Not_Found"));
		}
        catch (Exception ex) 
    	{
        	ex.printStackTrace();
    	}
		return fileNameList;
	}
	@Override
	public StringBuffer getFileNameList(FtpSessionHandler fs, String inPath)throws AccessDeniedException, NotADirectoryException, PathNotFoundException,InterruptedException 
	{
		int index;
		User user=fs.getUser();
		Set <String> result=null;
		ArrayList <String>resultList=null;
		String virPath=new String(),parentDir;
		StringBuffer fileNameList=new StringBuffer();
		TreeMap<String, String> clientPathACL=user.getClientPathACL();
		String serverPath=getServerPath(fs,inPath,FileManager.READ_PERMISSION);
		String currentPath=FileUtil.normalizeClientPath(logger, fs.getCurrentPath(), inPath);
		if (File.separator.equals("\\"))
			result=new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		else
			result=new TreeSet<String>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(serverPath))) 
		{
			for (Path path : directoryStream) //Loop all server path
            {
				try
				{
					if (currentPath.endsWith("/"))
						getServerPath(fs,currentPath+path.getFileName(),FileManager.READ_PERMISSION);
					else
						getServerPath(fs,currentPath+"/"+path.getFileName(),FileManager.READ_PERMISSION);
					result.add(path.getFileName().toString());
				}
				catch (AccessDeniedException | PathNotFoundException err)
				{
					
				}
            }
			for(Entry<String, String> entry : clientPathACL.entrySet())  //Loop all virtual path
			{
				virPath=entry.getKey();
				index=virPath.lastIndexOf("/");
				parentDir=virPath.substring(0,index+1);
				logger.debug("currentPath="+currentPath+",parentDir="+parentDir);
				if (parentDir.equals(currentPath)||parentDir.equals(currentPath+"/"))
				{
					try
					{
						getServerPath(fs,virPath,FileManager.READ_PERMISSION);
						logger.debug("0 virPath="+virPath);
						virPath=virPath.replaceAll(currentPath, "");
						logger.debug("1 virPath="+virPath);
						index=virPath.indexOf("/");
						if (index==0)
							virPath=virPath.substring(index+1);
						virPath=virPath.trim();
						if (!virPath.equals("") && (!result.contains(virPath)))							
						{
							result.add(virPath);
							logger.debug("virPath="+virPath+" added to result");
						}
					}
					catch(AccessDeniedException | PathNotFoundException err)
					{
					}
				}
			}
			resultList=new ArrayList<String>(result);
			Collections.sort(resultList);
			for (String temp :result)
			{
				fileNameList.append(temp+"\r\n");
			}
		}
		catch (NotDirectoryException err)
		{
			String message=fs.getFtpMessage("550_Not_A_Directory");
			message=message.replaceAll("%1", inPath);
			throw new NotADirectoryException(message);
		}
		catch (NoSuchFileException ex)
		{
			throw new PathNotFoundException(fs.getFtpMessage("450_Directory_Not_Found"));
		}
        catch (Exception ex) 
    	{
        	ex.printStackTrace();
    	}
		return fileNameList;
	}
	@Override
	public String getFile(FtpSessionHandler fs, String inPath)throws AccessDeniedException,NotAFileException,PathNotFoundException,InterruptedException 
	{
		String message=fs.getFtpMessage("550_Not_A_File");
		String serverPath=getServerPath(fs,inPath,FileManager.READ_PERMISSION);
		if (Files.isDirectory(Paths.get(serverPath)))
		{
			message=message.replaceAll("%1", inPath);
			throw new NotAFileException(message);
		}
		return serverPath;
	}
	@Override
	public String putFile(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException,InterruptedException, QuotaExceedException 
	{
		int index;
		String fileName;
		String serverPath=new String(),clientPath=FileUtil.normalizeClientPath(logger, fs.getCurrentPath(), inPath);
		index=clientPath.lastIndexOf("/");
		fileName=clientPath.substring(index+1);
		clientPath=clientPath.substring(0,index);
		serverPath=getServerPath(fs,clientPath,FileManager.WRITE_PERMISSION);
		serverPath+=File.separator+fileName;
		return serverPath;
	}
	@Override
	public String deleteDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException
	{
		String serverPath=new String(),clientPath=FileUtil.normalizeClientPath(logger, fs.getCurrentPath(), inPath);
		serverPath=getServerPath(fs,clientPath,FileManager.WRITE_PERMISSION);
		return serverPath;
	}
	public void close() 
	{
		
	}	
}
