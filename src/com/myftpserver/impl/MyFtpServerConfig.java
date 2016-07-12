package com.myftpserver.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.Logger;

import com.myftpserver.abstracts.FileManager;
import com.myftpserver.abstracts.FtpServerConfig;
import com.myftpserver.abstracts.UserManager;
import com.util.MessageBundle;
import com.util.Utility;
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
public class MyFtpServerConfig extends FtpServerConfig 
{
	private FileInputStream fis=null;
	private MessageBundle ftpMessageBundle;
	private PropertyResourceBundle bundle=null;
	private String configFile = "conf/server-config";
	public MyFtpServerConfig(Logger logger) 
	{
		super(logger);
		try
		{
			String start,end;
			int i,startPort,endPort;
			fis=new FileInputStream(configFile);
			bundle = new PropertyResourceBundle(fis);
			logger.debug("FTP Server Configuration is loaded successfully.");
			fis.close();
			if (bundle.containsKey("supportPassiveMode"))
			{	
				supportPassiveMode=Boolean.parseBoolean(bundle.getString("supportPassiveMode"));
			}
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
		if (bundle.containsKey("port"))
			serverPort=Integer.parseInt(bundle.getString("port"));
		return serverPort;
	}

	@Override
	public String getFtpMessage(String key) 
	{
		return ftpMessageBundle.getMessage(key);
	}

	@Override
	public int getMaxConnection() 
	{
		if (bundle.containsKey("maxConnection"))
			maxConnection=Integer.parseInt(bundle.getString("maxConnection"));
		return maxConnection;
	}

	@Override
	public int getCommandChannelConnectionTimeOut() 
	{
		if (bundle.containsKey("commandChannelConnectionTimeOut"))
			commandChannelConnectionTimeOut=Integer.parseInt(bundle.getString("commandChannelConnectionTimeOut"));
		return commandChannelConnectionTimeOut;
	}

	@Override
	public UserManager getUserManager() 
	{
		UserManager um=null;
		try 
		{
				um = (UserManager) Utility.getManager("userManager.classname",bundle).newInstance(this.logger);
		} 
		catch (InstantiationException | IllegalAccessException| IllegalArgumentException | InvocationTargetException e) 
		{
				logger.debug(e.getMessage());
		}
		return um;
	}

	@Override
	public FileManager getFileManager() 
	{
		FileManager fm=null;
		try 
		{
				fm = (FileManager) Utility.getManager("fileManager.classname",bundle).newInstance(this.logger);
		} 
		catch (InstantiationException | IllegalAccessException| IllegalArgumentException | InvocationTargetException e) 
		{
			logger.debug(e.getMessage());
		}
		return fm;
	}

	@Override
	public String getServerLocale() 
	{
		if (bundle.containsKey("serverLocale"))
			serverLocale=bundle.getString("serverLocale");
		ftpMessageBundle=new MessageBundle(new Locale(serverLocale));
		return serverLocale;
	}	

	@Override
	public String[] getAllBindAddress() 
	{
		String[] bindAddress=new String[]{};
		if (bundle.containsKey("bindAddress"))
			bindAddress=bundle.getString("bindAddress").split(",");
		return bindAddress; 
	}

}
