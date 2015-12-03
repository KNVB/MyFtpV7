package com.myftpserver.impl;

import java.sql.ResultSet;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.nio.file.DirectoryStream;
import java.nio.file.NoSuchFileException;

import io.netty.channel.ChannelHandlerContext;

import com.myftpserver.Configuration;
import com.myftpserver.User;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;
import com.myftpserver.exception.QuotaExceedException;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FileManager;

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
		pathSize=new File(serverPath).length();
		return pathSize;
	}

	@Override
	public void changeDirectory(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException {
		// TODO Auto-generated method stub
		String clientPath=null,serverPath;
		User user=fs.getUser();
		clientPath=Utility.resolveClientPath(logger,fs.getCurrentPath(),inPath);
		
		serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		if (Utility.isReadableServerPath(user.getServerPathACL(),Paths.get(serverPath)))
			fs.setCurrentPath(clientPath);
		else
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
	}

	@Override
	public StringBuilder getFullDirList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		User user=fs.getUser();
		String serverPath=new String();
		StringBuilder fileNameList=new StringBuilder();
		TreeMap<String,String> result=new TreeMap<String,String>();
		serverPath=dbo.getRealPath(fs, inPath, FileManager.READ_PERMISSION);
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(serverPath))) 
		{
			for (Path path : directoryStream) 
            {
				if (Utility.isReadableServerPath(user.getServerPathACL(),path))
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

	@Override
	public StringBuilder getFileNameList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		User user=fs.getUser();
		String serverPath=new String();
		ArrayList<String> result=new ArrayList<String>();
		StringBuilder fileNameList=new StringBuilder();
		serverPath=dbo.getRealPath(fs, inPath, FileManager.READ_PERMISSION);
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(serverPath))) 
		{
			for (Path path : directoryStream) 
            {
				if (Utility.isReadableServerPath(user.getServerPathACL(),path))
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

	@Override
	public String getFile(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		User user=fs.getUser();
		String clientPath=Utility.resolveClientPath(logger,fs.getCurrentPath(), inPath);
		String serverPath=dbo.getRealPath(fs,clientPath,FileManager.READ_PERMISSION);
		logger.debug("serverPath="+serverPath);
		if (Utility.isReadableServerPath(user.getServerPathACL(),Paths.get(serverPath)))
		{	
			return serverPath;
		}
		else
		{	
			throw new AccessDeniedException(config.getFtpMessage("550_Permission_Denied"));
		}
	}

	@Override
	public void putFile(FtpSessionHandler fs, ChannelHandlerContext ctx,
			String inPath) throws AccessDeniedException, PathNotFoundException,
			InterruptedException, QuotaExceedException {
		// TODO Auto-generated method stub
		String serverPath=dbo.getRealPath(fs,inPath,FileManager.WRITE_PERMISSION);
		logger.debug("serverPath="+serverPath);
		com.util.Utility.sendMessageToClient(ctx.channel(),logger,fs.getClientIp(),config.getFtpMessage("502_Command_Not_Implemeneted"));
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
