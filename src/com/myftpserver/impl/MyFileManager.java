package com.myftpserver.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
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
import com.myftpserver.interfaces.FileManager;

public class MyFileManager extends FileManager 
{
	/**
	 * An File Manager implementation
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
	public void createDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException, IOException 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException, IOException 
	{
		// TODO Auto-generated method stub

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
	@Override
	public void downloadFile(FtpSessionHandler fs, String inPath)throws AccessDeniedException, NotAFileException,PathNotFoundException, InterruptedException 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void uploadFile(FtpSessionHandler fs, String inPath)	throws AccessDeniedException, NotAFileException,PathNotFoundException, InterruptedException, QuotaExceedException 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFile(FtpSessionHandler fs, String inPath)	throws AccessDeniedException, NotAFileException,PathNotFoundException, IOException 
	{
		// TODO Auto-generated method stub

	}
	@Override
	public void renameFile(FtpSessionHandler fs, String oldFileName,String newFileName) throws AccessDeniedException,NotAFileException, PathNotFoundException, IOException 
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void close() 
	{
	}	
}
