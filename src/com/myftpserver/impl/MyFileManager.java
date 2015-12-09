package com.myftpserver.impl;

import com.myftpserver.User;
import com.myftpserver.exception.*;
import com.myftpserver.Configuration;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.handler.FtpSessionHandler;

import java.io.File;
import java.sql.ResultSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
	DbOp dbo=null;
	ResultSet rs=null;
	String strSql=new String();
	public MyFileManager(Configuration c) 
	{
		super(c);
		try 
		{
			dbo=new DbOp(c);
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			dbo=null;
		}
	}

	@Override
	public long getPathSize(FtpSessionHandler fs, String clientPath)
			throws AccessDeniedException, PathNotFoundException {
		// TODO Auto-generated method stub
		long pathSize=0;
		String serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		if (Utility.isReadableServerPath(fs,fs.getUser().getServerPathACL(),Paths.get(serverPath)))
		{	
			pathSize=new File(serverPath).length();
			return pathSize;
		}
		else
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
		
	}

	@Override
	public void changeDirectory(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException {
		// TODO Auto-generated method stub
		String clientPath=null,serverPath;
		User user=fs.getUser();
		clientPath=Utility.resolveClientPath(logger,fs.getCurrentPath(),inPath);
		serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		if (Files.exists(Paths.get(serverPath),new LinkOption[]{ LinkOption.NOFOLLOW_LINKS}))
		{
			if (Utility.isReadableServerPath(fs,user.getServerPathACL(),Paths.get(serverPath)))
				fs.setCurrentPath(clientPath);
			else
				throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
		}
		else
			throw new PathNotFoundException(fs.getConfig().getFtpMessage("450_Directory_Not_Found"));
	}

	@Override
	public StringBuffer getFullDirList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		Boolean isVirDirOk;
		User user=fs.getUser();
		String serverPath=new String(),currentPath,pathPerm;
		StringBuffer fileNameList=new StringBuffer();
		Hashtable<String, String> clientPathACL=user.getClientPathACL();
		TreeMap<String,String> result=new TreeMap<String,String>();
		currentPath=Utility.resolveClientPath(logger, fs.getCurrentPath(), inPath);
		serverPath=dbo.getRealPath(fs, inPath, FileManager.READ_PERMISSION);
		logger.debug("serverPath="+serverPath);
		if (Utility.isReadableServerPath(fs,user.getServerPathACL(),Paths.get(serverPath)))
		{	try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(serverPath))) 
			{
				for (Path path : directoryStream) 
	            {
					pathPerm=clientPathACL.get(currentPath+path.getFileName());
					isVirDirOk=true;
					if (pathPerm!=null)
					{
						int i=pathPerm.indexOf("\t");
						pathPerm=pathPerm.substring(0,i).trim();
						logger.debug(currentPath+","+path.getFileName()+","+pathPerm);
						if (pathPerm.indexOf(FileManager.NO_ACCESS)>-1)
							isVirDirOk=false;
					}
					if (isVirDirOk && Utility.isReadableServerPath(fs,user.getServerPathACL(),path))
		            	result.put((path.getFileName().toString()),Utility.formatPathName(path));
	            }
				logger.debug("Client Path ACL size="+user.getClientPathACL().size());
				Utility.addVirtualDirectoryList(fs,user.getClientPathACL(),dbo,result);
				Set<?> set = result.entrySet();
				Iterator<?> i = set.iterator();
				while(i.hasNext()) 
			    {
			        @SuppressWarnings("unchecked")
					Map.Entry<String,String> me = (Map.Entry<String, String>)i.next();
			        fileNameList.append(me.getValue()+me.getKey()+"\r\n");
			    }
				return fileNameList;
			}
			catch (NoSuchFileException ex)
			{
				throw new PathNotFoundException(fs.getConfig().getFtpMessage("450_Directory_Not_Found"));
			}
	        catch (Exception ex) 
	    	{
	        	ex.printStackTrace();
	        	return null;
	    	}
		}
		else
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
	}

	@Override
	public StringBuffer getFileNameList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		User user=fs.getUser();
		Boolean isVirDirOk;
		String serverPath=new String(),currentPath,pathPerm;
		Logger logger=fs.getConfig().getLogger();
		ArrayList<String> result=new ArrayList<String>();
		StringBuffer fileNameList=new StringBuffer();
		Hashtable<String, String> clientPathACL=user.getClientPathACL();
		currentPath=Utility.resolveClientPath(logger, fs.getCurrentPath(), inPath);
		serverPath=dbo.getRealPath(fs, inPath, FileManager.READ_PERMISSION);
		logger.debug("inPath="+inPath+",serverPath="+serverPath+",per="+Utility.isReadableServerPath(fs,user.getServerPathACL(),Paths.get(serverPath)));
		if (Utility.isReadableServerPath(fs,user.getServerPathACL(),Paths.get(serverPath)))
		{	try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(serverPath))) 
			{
				for (Path path : directoryStream) 
	            {
					//logger.debug(currentPath+","+path.getFileName());
					pathPerm=clientPathACL.get(currentPath+path.getFileName());
					isVirDirOk=true;
					if (pathPerm!=null)
					{
						int i=pathPerm.indexOf("\t");
						pathPerm=pathPerm.substring(0,i).trim();
						logger.debug(currentPath+","+path.getFileName()+","+pathPerm);
						if (pathPerm.indexOf(FileManager.NO_ACCESS)>-1)
							isVirDirOk=false;
					}
					if (isVirDirOk && Utility.isReadableServerPath(fs,user.getServerPathACL(),path))
						result.add(path.getFileName().toString());
	            }
				logger.debug("Client Path ACL size="+user.getClientPathACL().size());
				if (inPath.equals(""))
					Utility.addVirtualDirectoryName(fs.getConfig().getLogger(),fs.getCurrentPath(),user.getClientPathACL(),result);
				else
					Utility.addVirtualDirectoryName(fs.getConfig().getLogger(),inPath,user.getClientPathACL(),result);
				logger.debug("result1="+result.toString());
				Collections.sort(result);
				logger.debug("result2="+result.toString());
				for (String temp :result)
				{
					fileNameList.append(temp+"\r\n");
				}
				return fileNameList;
			}
			catch (NoSuchFileException ex)
			{
				throw new PathNotFoundException(fs.getConfig().getFtpMessage("450_Directory_Not_Found"));
			}
	        catch (Exception ex) 
	    	{
	        	ex.printStackTrace();
	        	return null;
	    	}
		}
		else
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
	}

	@Override
	public String getFile(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		User user=fs.getUser();
		String clientPath=Utility.resolveClientPath(logger,fs.getCurrentPath(), inPath);
		String serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		logger.debug("serverPath="+serverPath);
		if (Utility.isReadableServerPath(fs,user.getServerPathACL(),Paths.get(serverPath)))
		{	
			return serverPath;
		}
		else
		{	
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
		}
	}

	@Override
	public String putFile(FtpSessionHandler fs, String inPath) 
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException, QuotaExceedException {
		// TODO Auto-generated method stub
		User user=fs.getUser();
		String pathPerm=new String(),dirName;
		String serverPath=dbo.getRealPath(fs,inPath,FileManager.WRITE_PERMISSION);
		Logger logger=fs.getConfig().getLogger();
		if (serverPath.indexOf(File.separator)>-1)
			dirName=serverPath.substring(0,serverPath.lastIndexOf(File.separator));
		else
			dirName=fs.getCurrentPath();
		logger.debug("serverPath="+serverPath+",dirName="+dirName);
		if (Files.exists(Paths.get(dirName),new LinkOption[]{ LinkOption.NOFOLLOW_LINKS}))
		{	
			pathPerm=Utility.getServerPathPerm(fs.getConfig().getLogger(),user.getServerPathACL(),Paths.get(serverPath));
			if (pathPerm.indexOf(FileManager.NO_ACCESS)==-1)
			{	
				return serverPath;
			}
			else
				throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
		}
		else
			throw new PathNotFoundException(fs.getConfig().getFtpMessage("450_Directory_Not_Found"));
		//return serverPath;
	}

	@Override
	public void close() 
	{
		if (dbo!=null)
		{
			try 
			{
				dbo.close();
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dbo=null;
	}

}
