package com.myftpserver.impl;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;

import io.netty.channel.ChannelHandlerContext;

import com.myftpserver.Configuration;
import com.myftpserver.exception.AccessDeniedException;
import com.myftpserver.exception.PathNotFoundException;
import com.myftpserver.exception.QuotaExceedException;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.impl.DbOp;
import com.myftpserver.interfaces.FileManager;
import com.util.Utility;

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
		return 0;
	}

	@Override
	public void changeDirectory(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StringBuilder getFullDirList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringBuilder getFileNameList(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		String serverPath=new String();
		StringBuilder fileNameList=new StringBuilder();
		serverPath=dbo.getRealPath(fs.getUser(), fs.getCurrentPath(), inPath, FileManager.READ_PERMISSION);

		
		
		return fileNameList;
	}

	@Override
	public String getFile(FtpSessionHandler fs, String inPath)
			throws AccessDeniedException, PathNotFoundException,
			InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putFile(FtpSessionHandler fs, ChannelHandlerContext ctx,
			String inPath) throws AccessDeniedException, PathNotFoundException,
			InterruptedException, QuotaExceedException {
		// TODO Auto-generated method stub
		
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
