package com.myftpserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.Stack;

import com.util.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.UserManager;

import org.apache.logging.log4j.Logger;

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
public class ServerConfig 
{
	private Logger logger=null;
	private String encoding=null;
	private ManagersFactory mf=null;
	private String serverLocale=null;
	private FileInputStream fis=null;
	private MessageBundle messageBundle;
	private UserManager userManager=null;
	private FileManager fileManager=null;
	
	private String configFile = "conf/server-config";
	Stack<Integer> passivePorts=new Stack<Integer>();
	private int serverPort,maxConnection=0,commandChannelConnectionTimeOut=30000;
	private boolean supportPassiveMode=false,havePassivePortSpecified=false;
	/**
	 * This object contains all default server configuration setting  
	 * @param logger Logger for message logging
	 */	
	public ServerConfig(Logger logger) 
	{
		this.logger=logger;
	}
	/**
	 * Load configuration to memory
	 * @param myFtpServer MyFtpServer object
	 * @return load configuration success or not
	 */
	public boolean load(MyFtpServer myFtpServer)
	{
		boolean result=false;
		int i,startPort,endPort;
		String start,end;
		try
		{
			fis=new FileInputStream(configFile);
			PropertyResourceBundle bundle = new PropertyResourceBundle(fis);
			logger.debug("Server Configuration is loaded successfully.");
			serverLocale=bundle.getString("serverLocale");
			messageBundle=new MessageBundle(new Locale(serverLocale));
			serverPort=Integer.parseInt(bundle.getString("port"));
			maxConnection=Integer.parseInt(bundle.getString("maxConnection"));
			commandChannelConnectionTimeOut=Integer.parseInt(bundle.getString("commandChannelConnectionTimeOut"));
			supportPassiveMode=Boolean.parseBoolean(bundle.getString("supportPassiveMode"));
			if (supportPassiveMode)
			{
				if (bundle.containsKey("passivePortRange"))
				{
					for(String tempStr:bundle.getString("passivePortRange").split(","))
					{
						try
						{   i=tempStr.indexOf("-");
							if (i>-1)
							{
								start=tempStr.substring(0,i);
								end=tempStr.substring(i+1);
								//logger.debug("Start port="+start+",end port="+end);
								startPort=Integer.parseInt(start);
								endPort=Integer.parseInt(end);
								if (startPort<endPort)
								{
									for (i=startPort;i<=endPort;i++)
									{
										passivePorts.add(i);
									}
								}
							}	
							else	
								passivePorts.add(Integer.parseInt(tempStr));
						}
						catch (NumberFormatException ne)
						{
							
						}
					}
					havePassivePortSpecified=(passivePorts.size()>0);
				}
			}
			mf=new ManagersFactory(bundle,logger);
			userManager=mf.getUserManager();
			logger.info("User Manager class is loaded.");
			fileManager=mf.getFileManager();
			logger.info("File Manager class is loaded.");

			fis.close();
			result=true;			
		}
		catch (FileNotFoundException e) 
		{
			logger.info("Config. file not found.");
		} 
		catch (IOException e) 
		{
			logger.info("An exception occur when loading config. file.");
		}
		finally
		{
			fis=null;
		}		
		return result;
	}
	/**
	 * Does the server support passive mode transfer or not? 
	 * @return return true when the server is support passive mode transfer
	 */
	public boolean isSupportPassiveMode() 
	{
		return supportPassiveMode;
	}
	/**
	 * Is there any passive port specified?
	 * @return return true when passive port is specified.
	 */
	public boolean isPassivePortSpecified()
	{
		return havePassivePortSpecified;
	}
	/**
	 * Get FTP server port 
	 * @return the port no. that server is listening
	 */
	public int getServerPort() 
	{
		return serverPort;
	}
	/**
	 * Get Default FTP server encoding setting 
	 * @return FTP server encoding
	 */	
	public String getEncoding()
	{
		return encoding;
	}
	/**
	 *Get properly message text for specified return code 
	 * @param key the message key
	 * @return value the corresponding message text
	 */
	public String getFtpMessage(String key)
	{
		return messageBundle.getMessage(key);
	}	
	/**
	 * Get FTP server maximum current connection 
	 * @return FTP server maximum current connection
	 */	
	public int getMaxConnection() 
	{
		return maxConnection;
	}
	/**
	 * Get FTP command channel time out in second
	 * @return FTP command channel time out in second
	 */		
	public int getCommandChannelConnectionTimeOut() 
	{
		return this.commandChannelConnectionTimeOut;
	}
	/**
	 * Get User Manager object
	 * @return UserManager object
	 */
	public UserManager getUserManager() 
	{
		return userManager;
	}
	/**
	 * Get File Manager object
	 * @return FileManager object
	 */
	public FileManager getFileManager()
	{
		return fileManager;
	}
	/**
	 * Get Server default locale
	 * @return Server default locale
	 */
	public String getServerLocale() 
	{
		return serverLocale;
	}
	/**
	 * Get FTP message bundle object to FTP session for properly message return to user. 
	 * @return MessageBundle object
	 */
	public MessageBundle getMessageBundle()
	{
		return this.messageBundle;
	}
}
