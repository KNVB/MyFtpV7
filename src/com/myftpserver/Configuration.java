package com.myftpserver;

import com.util.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.UserManager;

import java.util.Stack;
import java.util.Locale;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.PropertyResourceBundle;

public class Configuration 
{
	
	private String encoding=null;
	private FtpMessage ftpMessage;
	private ManagersFactory mf=null;
	private FileInputStream fis=null;
	private MyFtpServer ftpServer=null;
	private UserManager userManager=null;
	private FileManager fileManager=null;

	private org.apache.log4j.Logger logger=null;
	private String ftpMessageLocale=new String();
	private String configFile = "conf/server-config";
	Stack<Integer> passivePorts=new Stack<Integer>();
	private int serverPort,maxConnection=0,commandChannelConnectionTimeOut=30000;
	public boolean supportPassiveMode=false,havePassivePortSpecified=false;
		
	public Configuration(org.apache.log4j.Logger l)
	{
		logger=l;
	}
	public boolean load(MyFtpServer fs)
	{
		boolean result=false;
		int i,startPort,endPort;
		String start,end;
		try 
		{
			ftpServer=fs;
			fis=new FileInputStream(configFile);
			PropertyResourceBundle bundle = new PropertyResourceBundle(fis);
			logger.info("Configuration file is loaded");
			serverPort=Integer.parseInt(bundle.getString("port"));
			maxConnection=Integer.parseInt(bundle.getString("maxConnection"));
			encoding=bundle.getString("encoding");
			ftpMessageLocale=bundle.getString("ftpMessageLocale");
			ftpMessage=new FtpMessage(new Locale(ftpMessageLocale));
			commandChannelConnectionTimeOut=Integer.parseInt(bundle.getString("commandChannelConnectionTimeOut"));
			supportPassiveMode=Boolean.parseBoolean(bundle.getString("supportPassiveMode"));
			logger.debug("supportPassiveMode="+supportPassiveMode);
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
			mf=new ManagersFactory(bundle,this);
			userManager=mf.getUserManager();
			logger.info("User Manager class is loaded.");
			fileManager=mf.getFileManager();
			logger.info("File Manager class is loaded.");

			fis.close();
			result=true;
		} 
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			logger.info("Config. file not found.");
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			logger.info("An exception occur when loading config. file.");
		}
		finally
		{
			fis=null;
		}
		return result;
	}
	protected boolean isSupportPassiveMode() 
	{
		return supportPassiveMode;
	}
	public org.apache.log4j.Logger getLogger()
	{
		return logger;
	}
	public int getServerPort() 
	{
		return serverPort;
	}
	public String getEncoding()
	{
		return encoding;
	}
	public int getMaxConnection() 
	{
		return maxConnection;
	}
	public int getCommandChannelConnectionTimeOut() 
	{
		return this.commandChannelConnectionTimeOut;
	}	
	public String getFtpMessage(String key)
	{
		return ftpMessage.getMessage(key);
	}
	public UserManager getUserManager() 
	{
		// TODO Auto-generated method stub
		return userManager;
	}	
	public FileManager getFileManager()
	{
		return fileManager;
	}
	public MyFtpServer getFtpServer() 
	{
		return ftpServer;
	}
}
