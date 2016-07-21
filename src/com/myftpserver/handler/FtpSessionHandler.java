package com.myftpserver.handler;

import java.io.File;
import java.util.Locale;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.*;
import com.myftpserver.abstracts.FtpServerConfig;
import com.util.MessageBundle;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
public class FtpSessionHandler extends SimpleChannelInboundHandler<String>
{
	private User user;
	private Channel ch;
	
	private Logger logger;
	private boolean isLogined=false;
	private int activeDataPortNo=-1;
	private MyFtpServer myFtpServer=null;
	private FtpServerConfig serverConfig=null;
	private MessageBundle messageBundle=null;
	private PassiveServer passiveServer=null;
	public boolean isPassiveModeTransfer=false;
	private FtpCommandExecutor ftpCommandHandler=null; 
	private File downloadFile=null,uploadTempFile=null, uploadFile=null;
	private String userName=new String(),dataType="I",currentPath=new String();
	private String clientIp=new String(),commandString=new String(),reNameFrom=new String();
	/**
	 * FTP session handler
	 * @param ch {@link io.netty.channel.Channel}
	 * @param myFtpServer {@link com.myftpserver.MyFtpServer}
	 * @param remoteIp {@link String Client IP address} 
	 */
	public FtpSessionHandler(Channel ch, MyFtpServer myFtpServer, String remoteIp)
	{
		this.ch=ch;
		this.currentPath="/";
		this.clientIp=remoteIp;
		
		this.myFtpServer=myFtpServer;
		this.isPassiveModeTransfer=false;
		this.logger=myFtpServer.getLogger();

		this.serverConfig=myFtpServer.getServerConfig();
		this.ftpCommandHandler=new FtpCommandExecutor(this);
		messageBundle=new MessageBundle(new Locale(serverConfig.getServerLocale()));
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		Utility.sendMessageToClient(ch,logger,clientIp,"220 "+messageBundle.getMessage("Greeting_Message"));
	}
	/**
	 * User input command event handler
	 * @param ctx the channel that user input command
	 * @param msg the command that user inputted
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception 
	{ 
		commandString=msg.trim();
		this.ch=ctx.channel();
		logger.info("Command:"+commandString+" received from "+this.clientIp);
		ftpCommandHandler.doCommand(ctx,commandString, logger);
	}
	/**
	 * Calls ChannelHandlerContext.fireExceptionCaught(Throwable) to forward to the next ChannelHandler in the ChannelPipeline. Sub-classes may override this method to change behavior.
	 * @param ctx the channel that user input command
	 * @param cause the exception cause  
	 */
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) 
    {
		myFtpServer.getLogger().debug(FtpSessionHandler.class.getName()+" exception occur:");
		myFtpServer.getLogger().debug(cause.getMessage());
        //cause.printStackTrace();
    }
	/**
	 * Get message logger
	 * @return {@link Logger} message logger 
	 */	
	public Logger getLogger()
	{
		return logger;
	}
	/**
	 * Get client IP address
	 * @return client IP address
	 */
    public String getClientIp()
    {
    	return clientIp;
    }
    /**
	 * Register FTP message bundle object to FTP session for properly message return to user. 
	 * @param messageBundle
	 */
	public void setMessageBundle(MessageBundle messageBundle) 
	{
		this.messageBundle=messageBundle;
		logger.debug("locale for this session ="+messageBundle.getLocale());
	}
	/**
	 * Get properly message text from return code 
	 * @param key the message key
	 * @return value the corresponding message text
	 */
	public String getFtpMessage(String key) 
	{
		return messageBundle.getMessage(key);
	}
	/**
     * Get Server Configuration object for retrieving server configuration setting
     * @return ServerConfig object
     */
	public FtpServerConfig getServerConfig() 
	{
		return serverConfig;
	}
	/**
	 * Get user interaction channel
	 * @return io.netty.channel.Channel object
	 */
	public Channel getChannel() 
	{
		return ch;
	}
	/**
	 * Set User login name for current ftp session
	 * @param userName User login name
	 */
	public void setUserName(String userName) 
	{
		this.userName=userName;
	}
	/**
	 * Register Login status for this FTP session
	 * @param l Login status
	 */
	public void setIsLogined(boolean l)
	{
		isLogined=l;
	}
	/**
	 * Get User login name this FTP session
	 * @return User login name
	 */
	public String getUserName()
	{
		return this.userName;
	}
	/**
	 * Set user current path for this FTP session
	 * @param cp current path
	 */
	public void setCurrentPath(String cp)
	{
		currentPath=cp;
	}
	/**
	 * Get user current path for this FTP session
	 * @return the user current path
	 */
	public String getCurrentPath()
	{
		return currentPath;
	}
	/**
	 * Get data type (e.g ASCII,bin) for this FTP session
	 * According to RFC959, default data type is 'A'
	 * @return data type (i.e. A,I) 
	 */
	public String getDataType()
	{
		return dataType;
	}
	/**
	 * Set data type (e.g ASCII,bin) for this FTP session
	 * @param type data type (i.e. A,I)
	 */
	public void setDataType(String type) 
	{
		dataType=type;
	}
	/**
     * Check login status for this FTP session
     * @return true if a user already login.
     */
	public boolean isLogined() 
	{
		return isLogined;
	}
	/**
	 * Set User object for this FTP session
	 * @param user User object
	 */
	public void setUser(User user) 
	{
		this.user=user;
	}
	/**
	 * Get User object for this FTP session
	 * @return User object
	 */
	public User getUser() 
	{
		return this.user;
	}
	/**
	 * Get server object for this FTP session
	 * @return MyFtpServer object
	 */
	public MyFtpServer getServer() 
	{
		return myFtpServer;
	}
	/**
	 * Reinitialize Command Session
	 */
	public void reinitialize() 
	{
		myFtpServer.reinitializeSession(this.ch,this.clientIp);
	}
	/**
	 * Set original file name for rename
	 * @param reNameFrom
	 */
	public void setReNameFrom(String reNameFrom) 
	{
		this.reNameFrom=reNameFrom;		
	}
	/**
	 * Get original file name for rename
	 * @return original file name for rename
	 */
	public String getReNameFrom() 
	{
		return this.reNameFrom;		
	}
	/**
	 * Get FTP command channel time out in second
	 * @return FTP command channel time out in second
	 */	
	public int getSessionTimeOut()
	{
		return myFtpServer.getServerConfig().getCommandChannelConnectionTimeOut();
	}	
	/**
	 * Get passive server for passive mode operation 
	 * @return PassiveServer object
	 */
	public PassiveServer getPassiveServer() 
	{
		return this.passiveServer;
	}
	/**
	 * Set passive server for passive mode operation 
	 * @param passiveServer PassiveServer object
	 */
	public void setPassiveServer(PassiveServer passiveServer) 
	{
		this.passiveServer=passiveServer;
	}
	/**
	 * Get temporary upload file object
	 * @return Upload Temp File object
	 */
	public File getUploadTempFile() 
	{
		return this.uploadTempFile;
	}
	/**
	 * Set temporary upload file object
	 * @param uploadTempFile
	 */
	public void setUploadTempFile(File uploadTempFile) 
	{
		this.uploadTempFile=uploadTempFile;
	}	
	/**
	 * Get active data port no. (valid in active mode operation) 
	 * @return client data port no.
	 */
	public int getActiveDataPortNo()
	{
		return activeDataPortNo;
	}
	/**
	 * Set active data port no. (valid in active mode operation) 
	 * @param portNo client data port no.
	 */
	public void setActiveDataPortNo(int portNo) 
	{
		activeDataPortNo=portNo;
	}
	/**
	 * Set the download file object
	 * @param downloadFile
	 */
	public void setDownloadFile(File downloadFile) 
	{
		this.downloadFile=downloadFile;
	}
	/**
	 * Get the download file object
	 * @return the download file object
	 */
	public File getDownloadFile() 
	{
		return this.downloadFile;		
	}	
	/**
	 * Set the upload file object
	 * @param uploadFile
	 */
	public void setUploadFile(File uploadFile) 
	{
		this.uploadFile = uploadFile;
	}
	/**
	 * Get the upload file object
	 * @return the upload file object
	 */
	public File getUploadFile() 
	{
		return uploadFile;
	}
	/**
	 * Close the FTP session
	 */
	public void close()
	{
		ch.close();
		ch=null;		
	}
}