package com.myftpserver.interfaces;

import org.apache.log4j.Logger;

import com.myftpserver.Configuration;
import com.myftpserver.exception.*;
import com.myftpserver.handler.FtpSessionHandler;

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
	 

	public abstract long getPathSize(FtpSessionHandler fs, String clientPath)throws AccessDeniedException, PathNotFoundException;
	public abstract void changeDirectory(FtpSessionHandler fs,String inPath) throws AccessDeniedException, PathNotFoundException;
	public abstract void showFullDirList(FtpSessionHandler fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException, InterruptedException;
	public abstract void showFileNameList(FtpSessionHandler fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException, InterruptedException;
	public abstract void getFile(FtpSessionHandler fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException,InterruptedException;
	public abstract void putFile(FtpSessionHandler fs, ChannelHandlerContext ctx,String inPath) throws AccessDeniedException, PathNotFoundException,InterruptedException,QuotaExceedException;
	public abstract void close();

}
