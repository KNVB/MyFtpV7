package com.myftpserver.impl;

import com.myftpserver.User;
import com.myftpserver.exception.*;
import com.myftpserver.Configuration;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collections;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;

import org.apache.log4j.Logger;

public class MyFileManager extends FileManager 
{
	
	public MyFileManager(Configuration c) 
	{
		super(c);
	}
	public void getRealHomePath(FtpSessionHandler fs)throws AccessDeniedException,InvalidHomeDirectoryException 
	{
		try
		{
			getServerPath(fs,"/",FileManager.READ_PERMISSION);
		}
		catch (AccessDeniedException err)
		{
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
		}
		catch (PathNotFoundException err)
		{
			throw new InvalidHomeDirectoryException(config.getFtpMessage("530_Home_Dir_Not_Found"));
		}
	}
	public String getServerPath(FtpSessionHandler fs,String inPath, String requiredPermission)throws AccessDeniedException,PathNotFoundException
	{
		String serverPath=new String(),serverPathPerm=null,virtualPathPerm=null,finalPerm=null;
		String clientPath=FileUtil.normalizeClientPath(fs.getConfig().getLogger(), fs.getCurrentPath(), inPath);
		String serverPathAndPerm=FileUtil.getServerPathAndPermFromVirDir(fs,clientPath);
		if (serverPathAndPerm.isEmpty())
		{
			throw new PathNotFoundException(fs.getConfig().getFtpMessage("450_Directory_Not_Found"));
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
			if ((finalPerm==null) || finalPerm.indexOf(FileManager.NO_ACCESS)>-1||finalPerm.indexOf(requiredPermission)==-1)
			{
				throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
			}
			else
			{
				if (!Files.exists(Paths.get(serverPath),new LinkOption[]{ LinkOption.NOFOLLOW_LINKS}))
				{
					throw new PathNotFoundException(fs.getConfig().getFtpMessage("450_Directory_Not_Found"));
				}
			}
		}
		return serverPath;
	}
	@Override
	public long getPathSize(FtpSessionHandler fs, String clientPath)
			throws AccessDeniedException, PathNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void changeDirectory(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException 
	{
		getServerPath(fs,inPath,FileManager.READ_PERMISSION);
		fs.setCurrentPath(FileUtil.normalizeClientPath(logger, fs.getCurrentPath(), inPath));
	}
	@Override
	public StringBuffer getFullDirList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StringBuffer getFileNameList(FtpSessionHandler fs, String inPath)throws AccessDeniedException, PathNotFoundException,InterruptedException 
	{
		String serverPath=getServerPath(fs,inPath,FileManager.READ_PERMISSION);
		return null;
	}
	@Override
	public String getFile(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String putFile(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException, QuotaExceedException {
		// TODO Auto-generated method stub
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
	public void close() 
	{
		
	}		
}
