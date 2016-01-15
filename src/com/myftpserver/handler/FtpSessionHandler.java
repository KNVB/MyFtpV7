package com.myftpserver.handler;

import org.apache.logging.log4j.Logger;

import com.myftpserver.FtpCommandExecutor;
import com.myftpserver.MyFtpServer;
import com.myftpserver.ServerConfig;
import com.myftpserver.User;
import com.util.MessageBundle;
import com.util.Utility;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

public class FtpSessionHandler  extends SimpleChannelInboundHandler<String>
{
	private User user;
	private Channel ch;
	
	private MyFtpServer myFtpServer;
	private Logger logger;
	
	private int activeDataPortNo=-1,passiveDataPortNo=-1;
	private boolean isLogined=false;
	private ServerConfig serverConfig=null;
	private MessageBundle messageBundle=null;
	//private PassiveServer passiveServer=null;
	public boolean isPassiveModeTransfer=false;
	private FtpCommandExecutor ftpCommandHandler=null; 
	private String userName=new String(),dataType="A",currentPath=new String();
	private String clientIp=new String(),commandString=new String(),reNameFrom=new String();
	public FtpSessionHandler(Channel ch, MyFtpServer s, String remoteIp)
	{
		this.myFtpServer=s;
		this.ch=ch;
		this.currentPath="/";
		this.clientIp=remoteIp;
		this.logger=s.getLogger();
		this.isPassiveModeTransfer=false;
		this.serverConfig=s.getServerConfig();
		this.ftpCommandHandler=new FtpCommandExecutor(this);
		messageBundle=serverConfig.getMessageBundle();
	}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)
	{
		Utility.sendMessageToClient(ch,logger,clientIp,"220 "+serverConfig.getFtpMessage("Greeting_Message"));
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
	 * @return message logger 
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
	 * Set FTP message bundle
	 * @param messageBundle
	 */
	public void setMessageBundle(MessageBundle messageBundle) 
	{
		this.messageBundle=messageBundle;
		logger.debug("locale for this session ="+messageBundle.getLocale());
	}
	/**
	 * Get message text from a key
	 * @param key the message key
	 * @return value the corresponding message text
	 */
	public String getFtpMessage(String key) 
	{
		return messageBundle.getMessage(key);
	}
	/**
     * Get Server Configuration object
     * @return ServerConfig object
     */
	public ServerConfig getServerConfig() 
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
	 * Set User login name
	 * @param userName User login name
	 */
	public void setUserName(String userName) 
	{
		this.userName=userName;
	}
	/**
	 * Set Login status
	 * @param l Login status
	 */
	public void setIsLogined(boolean l)
	{
		isLogined=l;
	}
	/**
	 * Get User login name
	 * @return User login name
	 */
	public String getUserName()
	{
		return this.userName;
	}
	/**
	 * Set user current path
	 * @param cp current path
	 */
	public void setCurrentPath(String cp)
	{
		currentPath=cp;
	}
	/**
	 * Get user current path
	 * @return the user current path
	 */
	public String getCurrentPath()
	{
		return currentPath;
	}
	/**
	 * Get data type (e.g ASCII,bin)
	 * According to RFC959, default data type is 'A'
	 * @return data type (i.e. A,I) 
	 */
	public String getDataType()
	{
		return dataType;
	}
	/**
	 * Set data type (e.g ASCII,bin)
	 * @param type data type (i.e. A,I)
	 */
	public void setDataType(String type) 
	{
		dataType=type;
	}
	/**
     * Check login status
     * @return true if a user already login.
     */
	public boolean isLogined() 
	{
		return isLogined;
	}
	/**
	 * Set User object
	 * @param user User object
	 */
	public void setUser(User user) 
	{
		this.user=user;
	}
	/**
	 * Get User object
	 * @return User object
	 */
	public User getUser() 
	{
		return this.user;
	}
	/**
	 * Get server object
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
	 * Close the FTP session
	 */
	public void close()
	{
		ch.close();
		ch=null;		
	}
}
