package com.myftpserver.impl;
import java.util.Stack;
import java.util.Locale;
import java.io.IOException;

import com.util.MessageBundle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;

import java.util.PropertyResourceBundle;

import com.myftpserver.abstracts.FileManager;
import com.myftpserver.abstracts.UserManager;
import com.myftpserver.abstracts.ServerConfiguration;

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
public class MyServerConfig extends ServerConfiguration  
{
	private String encoding=null;
	private String serverLocale=null;
	
	private FileInputStream fis=null;
	private MessageBundle messageBundle;
	
	
	private PropertyResourceBundle bundle=null;
	private String configFile = "conf/server-config";
	private Stack<Integer> passivePorts=new Stack<Integer>();
	private int serverPort,maxConnection=0,commandChannelConnectionTimeOut=30000;
	private boolean supportPassiveMode=false,havePassivePortSpecified=false;
	private int i,startPort,endPort;
	private String start,end;
	public MyServerConfig(Logger logger) 
	{
		super(logger);
		try
		{
			fis=new FileInputStream(configFile);
			bundle = new PropertyResourceBundle(fis);
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
			fis.close();
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
	}

	@Override
	public boolean isSupportPassiveMode() 
	{
		return supportPassiveMode;
	}

	@Override
	public boolean isPassivePortSpecified() 
	{
		return havePassivePortSpecified;
	}

	@Override
	public int getServerPort() 
	{
		return serverPort;
	}

	@Override
	public String getEncoding() 
	{
		return encoding;
	}

	@Override
	public String getFtpMessage(String key) 
	{
		return messageBundle.getMessage(key);
	}

	@Override
	public int getMaxConnection() 
	{
		return maxConnection;
	}

	@Override
	public int getCommandChannelConnectionTimeOut() 
	{
		return commandChannelConnectionTimeOut;
	}
	
	@Override
	public String getServerLocale() 
	{
		return serverLocale;
	}

	@Override
	public MessageBundle getMessageBundle() 
	{
		return this.messageBundle;
	}
	/**
	 * Get an file manager object
	 * @return an file manager object
	 */
	public FileManager getFileManager()
	{
		FileManager fm=null;
		try 
		{
			fm = (FileManager) getManager("fileManager.classname").newInstance(this.logger);
		}
		catch (IllegalAccessException|InstantiationException|IllegalArgumentException|InvocationTargetException e) 
		{
			e.printStackTrace();
		} 
		return fm;
	}
	/**
	 * Get an user manager object
	 * @return an user manager object
	 */
	public UserManager getUserManager()
	{
		UserManager um=null;
		try 
		{
			um = (UserManager) getManager("userManager.classname").newInstance(this.logger);
		}
		catch (IllegalAccessException|InstantiationException|IllegalArgumentException|InvocationTargetException e) 
		{
			e.printStackTrace();
		} 
		return um;
	}
	private Constructor<?> getManager(String key)
	{
		@SuppressWarnings("rawtypes")
		Constructor c=null;
		try
		{
			c=Class.forName(bundle.getString(key)).getConstructor(Logger.class);
			return c;
		}
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) 
		{
			System.out.println(e.getMessage()+" not found.");
		}
		return null;				
	}
}
