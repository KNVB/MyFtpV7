package com.myftpserver.handler;
import com.myftpserver.*;
import com.util.MessageBundle;

import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleState;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.channel.ChannelHandler.Sharable;
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
@Sharable
public class FtpSessionHandler  extends SimpleChannelInboundHandler<String>
{
	private User user;
	private Channel ch;
	
	private MyFtpServer s;
	private Logger logger;
	
	private int clientDataPortNo=-1;
	private boolean isLogined=false;
	private ServerConfig serverConfig=null;
	private MessageBundle messageBundle=null;
	private PassiveServer passiveServer=null;
	public boolean isPassiveModeTransfer=false;
	private FtpCommandExecutor ftpCommandHandler=null; 
	private String userName=new String(),dataType="A",currentPath=new String();
	private String clientIp=new String(),commandString=new String(),reNameFrom=new String();
	/**
	 * FTP Session Handler
	 * @param ch a channel for user interaction
	 * @param s MyFtpServer object
	 * @param remoteIp remote IP address
	 */
	public FtpSessionHandler(Channel ch, MyFtpServer s, String remoteIp) 
	{
		super();
		this.s=s;
		this.ch=ch;
		this.currentPath="/";
		this.clientIp=remoteIp;
		this.logger=s.getLogger();
		this.isPassiveModeTransfer=false;
		this.serverConfig=s.getServerConfig();
		this.ftpCommandHandler=new FtpCommandExecutor(this);
		messageBundle=serverConfig.getMessageBundle();
	}
	/**
	 * User input command event handler
	 * @param ctx the channel that user input command
	 * @param msg the command that user inputted
	 */
	public void channelRead0(ChannelHandlerContext ctx, String msg) 
	{
		commandString=msg.trim();
		String commands[]=commandString.split("\n");
		for (String command:commands)
		{
			logger.info("Command:"+command+" received from "+this.clientIp);
			ftpCommandHandler.doCommand(ctx,command.trim(), logger);
		}
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
	 * Get user interaction channel
	 * @return io.netty.channel.Channel object
	 */
	public Channel getChannel() 
	{
		return ch;
	}
	/**
	 * Calls ChannelHandlerContext.fireExceptionCaught(Throwable) to forward to the next ChannelHandler in the ChannelPipeline. Sub-classes may override this method to change behavior.
	 * @param ctx the channel that user input command
	 * @param cause the exception cause  
	 */
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) 
    {
        s.getLogger().debug(FtpSessionHandler.class.getName()+" exception occur:");
        s.getLogger().debug(cause.getMessage());
        //cause.printStackTrace();
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
     * Check login status
     * @return true if a user already login.
     */
	public boolean isLogined() 
	{
		return isLogined;
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
	 * Set client data port no. (valid in active mode operation) 
	 * @param portNo client data port no.
	 */
	public void setClientDataPortNo(int portNo) 
	{
		clientDataPortNo=portNo;
	}
	/**
	 * Get client data port no. (valid in active mode operation) 
	 * @return client data port no.
	 */
	public int getClientDataPortNo()
	{
		return clientDataPortNo;
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
     * Get Server Configuration object
     * @return ServerConfig object
     */
	public ServerConfig getServerConfig() 
	{
		return serverConfig;
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
	 * Get server object
	 * @return MyFtpServer object
	 */
	public MyFtpServer getServer() 
	{
		return s;
	}
	/**
	 * It is used to handle time out issue
	 * @param ctx the channel that user input command
	 * @param evt the event object
	 */
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception 
	{
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) 
            {
                //String goodByeMessage=s.getConfig().getFtpMessage("421_Idle_Timeout");
                //goodByeMessage=goodByeMessage.replaceAll("%1",String.valueOf(s.getConfig().getCommandChannelConnectionTimeOut()));
            	//ctx.writeAndFlush(Unpooled.copiedBuffer(goodByeMessage+"\r\n",CharsetUtil.UTF_8));
            	close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
               // ctx.writeAndFlush(new PingMessage());
            }
        }
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