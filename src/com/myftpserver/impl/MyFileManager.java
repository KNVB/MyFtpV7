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
	public boolean isAuthorized(FtpSessionHandler fs,String inPath, String readPermission)throws AccessDeniedException, PathNotFoundException,InvalidHomeDirectoryException
	{
		boolean result=false;
		String clientPath=FileUtil.normalizeClientPath(fs.getConfig().getLogger(), fs.getCurrentPath(), inPath);
		String permission=FileUtil.getVirtualDirectoryPermission(fs,clientPath);
		
		return result;
	}
	@Override
	public long getPathSize(FtpSessionHandler fs, String clientPath)
			throws AccessDeniedException, PathNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void changeDirectory(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public StringBuffer getFullDirList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public StringBuffer getFileNameList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
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
