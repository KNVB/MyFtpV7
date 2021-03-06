package com.myftpserver.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.logging.log4j.Logger;

import com.myftpserver.User;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.InvalidHomeDirectoryException;
import com.myftpserver.exception.NotADirectoryException;
import com.myftpserver.exception.NotAFileException;
import com.myftpserver.exception.PathNotFoundException;
import com.myftpserver.exception.QuotaExceedException;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.abstracts.FileManager;
import com.myftpserver.abstracts.UserManager;
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
	 * MyFileManager is a concrete subclass of {@link com.myftpserver.abstracts.FileManager}
	 * @param logger Message logger
	 */
	public MyFileManager(Logger logger) 
	{
		super(logger);
	}

	@Override
	public long getPathSize(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException 
	{
		String serverPath=FileUtil.getServerPath(fs,inPath,FileManager.READ_PERMISSION);
		long pathSize=new File(serverPath).length();
		return pathSize;	
	}
	public void getRealHomePath(FtpSessionHandler fs)throws AccessDeniedException,InvalidHomeDirectoryException 
	{
		try
		{
			FileUtil.getServerPath(fs,"/",FileManager.READ_PERMISSION);
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
	@Override
	public void changeDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException 
	{
		FileUtil.getServerPath(fs,inPath,FileManager.EXECUTE_PERMISSION);
		fs.setCurrentPath(FileUtil.normalizeClientPath(logger, fs.getCurrentPath(), inPath));
	}

	@Override
	public void makeDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException, IOException 
	{
		String serverPath=FileUtil.getFutureServerPath(fs, inPath);
		logger.debug("Server path="+serverPath);
		Files.createDirectories(Paths.get(serverPath));
	}

	@Override
	public void deleteDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException, IOException,NotADirectoryException,InvalidPathException 
	{
		boolean result;
		File serverFolder;
		String serverPath=new String(),newPathName,message,clientPath;
		newPathName=inPath;
		if (newPathName.indexOf("/")==-1)
			newPathName=fs.getCurrentPath()+"/"+newPathName;
		clientPath=FileUtil.normalizeClientPath(logger, fs.getCurrentPath(),newPathName);
		serverPath=FileUtil.getServerPath(fs,clientPath,FileManager.WRITE_PERMISSION);
		serverFolder=new File(serverPath);
		if (serverFolder.isDirectory())
		{	
			result=FileUtil.deleteDirectory(serverFolder);
			if (!result)
				throw new PathNotFoundException("");
		}
		else
		{	
			message=fs.getFtpMessage("550_Not_A_Directory");
			message=message.replace("%1", inPath);
			throw new NotADirectoryException(message);
		}		
	}

	@Override
	public StringBuffer getFullDirList(FtpSessionHandler fs, String inPath)	throws AccessDeniedException, NotADirectoryException,PathNotFoundException, InterruptedException 
	{
		int index;
		User user=fs.getUser();
		TreeMap<String,String> result=null;
		ArrayList <String>resultList=null;
		String virPath=new String(),parentDir;
		StringBuffer fileNameList=new StringBuffer();
		TreeMap<String, String> clientPathACL=user.getClientPathACL();
		String serverPath=FileUtil.getServerPath(fs,inPath,FileManager.READ_PERMISSION);
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
						FileUtil.getServerPath(fs,currentPath+path.getFileName(),FileManager.READ_PERMISSION);
					else
						FileUtil.getServerPath(fs,currentPath+"/"+path.getFileName(),FileManager.READ_PERMISSION);
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
						serverPath=FileUtil.getServerPath(fs,virPath,FileManager.READ_PERMISSION);
						logger.debug("0 virPath="+virPath);
						virPath=virPath.replace(currentPath, "");
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
			message=message.replace("%1", inPath);
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
	public StringBuffer getFileNameList(FtpSessionHandler fs, String inPath)throws AccessDeniedException, NotADirectoryException,PathNotFoundException, InterruptedException 
	{
		int index;
		User user=fs.getUser();
		Set <String> result=null;
		ArrayList <String>resultList=null;
		String virPath=new String(),parentDir;
		StringBuffer fileNameList=new StringBuffer();
		TreeMap<String, String> clientPathACL=user.getClientPathACL();
		String serverPath=FileUtil.getServerPath(fs,inPath,FileManager.READ_PERMISSION);
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
						FileUtil.getServerPath(fs,currentPath+path.getFileName(),FileManager.READ_PERMISSION);
					else
						FileUtil.getServerPath(fs,currentPath+"/"+path.getFileName(),FileManager.READ_PERMISSION);
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
						FileUtil.getServerPath(fs,virPath,FileManager.READ_PERMISSION);
						logger.debug("0 virPath="+virPath);
						virPath=virPath.replace(currentPath, "");
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
			message=message.replace("%1", inPath);
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
	public File getDownloadFileObject(FtpSessionHandler fs, String inPath)throws AccessDeniedException, NotAFileException,PathNotFoundException, InterruptedException,IOException  
	{
		String message=fs.getFtpMessage("550_Not_A_File");
		String serverPath=FileUtil.getServerPath(fs,inPath,FileManager.READ_PERMISSION);
		if (Files.isDirectory(Paths.get(serverPath)))
		{
			message=message.replace("%1", inPath);
			throw new NotAFileException(message);
		}
		return new File(serverPath);
	}

	@Override
	public File getUploadFileObject(FtpSessionHandler fs, String inPath)throws AccessDeniedException, NotAFileException,PathNotFoundException, InterruptedException, QuotaExceedException,IOException  
	{
		String message=fs.getFtpMessage("550_Not_A_File");
		User user=fs.getUser();
		if ((user.getQuota()>-1.0) && (user.getDiskSpaceUsed()>=user.getQuota()))
		{
			message=fs.getFtpMessage("550_Quota_Exceed");
			message=message.replace("%1",String.valueOf(user.getQuota()));
			message=message.replace("%2",String.valueOf(user.getDiskSpaceUsed()));
			throw new QuotaExceedException(message);
		}
		else
		{
			String serverPath=FileUtil.getFutureServerPath(fs, inPath);
			if (Files.isDirectory(Paths.get(serverPath)))
			{
				message=message.replace("%1", inPath);
				throw new NotAFileException(message);
			}
			return new File(serverPath);
		}
	}

	@Override
	public void deleteFile(FtpSessionHandler fs, String inPath)	throws AccessDeniedException, NotAFileException,PathNotFoundException, IOException 
	{
		String serverPath=FileUtil.getServerPath(fs, inPath, FileManager.WRITE_PERMISSION);
		if (Files.isDirectory(Paths.get(serverPath)))
		{
			String message=fs.getFtpMessage("550_Not_A_File");
			message=message.replace("%1", inPath);
			throw new NotAFileException(message);
		}
		else	
		{	
			User user=fs.getUser();
			if (user.getQuota()>-1.0)
			{
				double diskSpaceUsed=user.getDiskSpaceUsed()*1024.0;
				diskSpaceUsed=diskSpaceUsed-(new Long(Files.size(Paths.get(serverPath)))).doubleValue();
				if (diskSpaceUsed<0)
				{
					diskSpaceUsed=0.0;
				}
				user.setDiskSpaceUsed(diskSpaceUsed/1024.0);
				UserManager um=fs.getServerConfig().getUserManager();
				um.upDateUserInfo(user);
			}
			Files.delete(Paths.get(serverPath));
		}

	}
	@Override
	public void renameFrom(FtpSessionHandler fs, String oldFileName) throws AccessDeniedException,NotAFileException, PathNotFoundException, IOException 
	{
		String message=fs.getFtpMessage("550_Not_A_File"); 
		String serverPath=FileUtil.getServerPath(fs, oldFileName, FileManager.WRITE_PERMISSION);
		if (Files.isDirectory(Paths.get(serverPath)))
		{
			message=message.replace("%1", oldFileName);
			throw new NotAFileException(message);
		}
		else
		{
			fs.setReNameFrom(serverPath);
		}
	}
	@Override
	public void renameTo(FtpSessionHandler fs, String newFileName) throws AccessDeniedException, NotAFileException, PathNotFoundException, IOException
	{
		String destPath=FileUtil.getFutureServerPath(fs, newFileName);
		Path renameFrom=Paths.get(fs.getReNameFrom());
		Path renameTo=Paths.get(destPath);
		Files.move(renameFrom, renameTo);
	}
	@Override
	public void close() 
	{
	}	
}
