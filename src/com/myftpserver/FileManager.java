package com.myftpserver;

import org.apache.log4j.Logger;

import com.myftpserver.exception.*;
import com.myftpserver.handler.FtpSession;

import io.netty.channel.ChannelHandlerContext;


public abstract class FileManager 
{
	public static final int ACCESS_OK=0;
	public static final int EXCESS_QUOTA=1;
	public static final int ACCESS_DENIED=2;
	public static final int MULTI_HOME_DIR=3;
	public static final int INVALID_HOME_DIR=4;
	public static final int HOME_DIR_NOT_FOUND=5;
	public static final int INVALID_HOME_DIR_PERM=6;
	public static final int PATH_NOT_FOUND=7;
	
	public static final String NO_ACCESS="x";
	public static final String READ_PERMISSION="r";
	public static final String LIST_PERMISSION="l";
	public static final String WRITE_PERMISSION="w";
	
	public static final String TREE_WRITE_PERMISSION="t";
	public Logger logger;
	public Configuration config;
	public FileManager(Configuration c)
	{
		this.config=c;
		this.logger=config.getLogger();
	}
	 

	public abstract long getPathSize(FtpSession fs, String clientPath)throws AccessDeniedException, PathNotFoundException;
	public abstract void changeDirectory(FtpSession fs,String inPath) throws AccessDeniedException, PathNotFoundException;
	public abstract void showFullDirList(FtpSession fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException, InterruptedException;
	public abstract void showFileNameList(FtpSession fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException, InterruptedException;
	public abstract void downloadFile(FtpSession fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException,QuotaExceedException, InterruptedException;
	public abstract void close();

}
