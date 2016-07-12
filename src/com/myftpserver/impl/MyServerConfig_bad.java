package com.myftpserver.impl;

import com.util.Utility;
import com.util.MessageBundle;

import java.util.Locale;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.PropertyResourceBundle;
import java.util.MissingResourceException;
import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.Logger;

import com.myftpserver.abstracts.FileManager;
import com.myftpserver.abstracts.UserManager;
import com.myftpserver.abstracts.FtpServerConfig;

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
public class MyServerConfig_bad extends FtpServerConfig  
{
	private String start,end;
	private int i,startPort,endPort;
	private String encoding=null;
	private String serverLocale=null;
	private String bindingAddressString=null;
	
	private FileInputStream fis=null;
	private MessageBundle messageBundle;
	private PropertyResourceBundle bundle=null;
	
	private String configFile = "conf/server-config";
	public MyServerConfig_bad(Logger logger) 
	{
		super(logger);
		try
		{
			fis=new FileInputStream(configFile);
			bundle = new PropertyResourceBundle(fis);
			logger.debug("FTP Server Configuration is loaded successfully.");
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
		if (serverPort>-1)
			return serverPort;
		else
			return 21;
	}

	/*@Override
	public String getEncoding() 
	{
		return encoding;
	}*/

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
	/*public MessageBundle getMessageBundle() 
	{
		return this.messageBundle;
	}*/
	/**
	 * Get an file manager object
	 * @return an file manager object
	 */
	public FileManager getFileManager()
	{
		FileManager fm=null;
		try 
		{
			fm = (FileManager) Utility.getManager("fileManager.classname",bundle).newInstance(this.logger);
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
			um = (UserManager) Utility.getManager("userManager.classname",bundle).newInstance(this.logger);
		}
		catch (IllegalAccessException|InstantiationException|IllegalArgumentException|InvocationTargetException e) 
		{
			e.printStackTrace();
		} 
		return um;
	}
	public String[] getAllBindAddress()
	{
		String[] bindAddress = new String[]{};
		if (bindingAddressString!=null)
			bindAddress=bindingAddressString.split(",");
		return bindAddress;
	}
}
