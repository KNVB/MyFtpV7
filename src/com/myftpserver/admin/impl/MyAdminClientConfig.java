package com.myftpserver.admin.impl;

import org.apache.logging.log4j.Logger;

import com.util.MessageBundle;
import com.myftpserver.admin.abstracts.AdminClientConfig;

import java.util.Locale;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.PropertyResourceBundle;
import java.util.MissingResourceException;
import java.lang.reflect.InvocationTargetException;


public class MyAdminClientConfig extends AdminClientConfig 
{
	private FileInputStream fis=null;
	private MessageBundle ftpMessageBundle;
	private PropertyResourceBundle bundle=null;
	private String configFile = "conf/server-config";
	/**
	 *  MyFtpServerConfig is a concrete subclass of {@link com.myftpserver.admin.abstracts.AdminClientConfig}
	 * @param logger
	 */
	public MyAdminClientConfig(Logger logger) {
		super(logger);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int load() 
	{
		String start,end;
		int i,startPort,endPort;
		int initResult=AdminClientConfig.LOAD_OK;
		try
		{
			fis=new FileInputStream(configFile);
			bundle = new PropertyResourceBundle(fis);
			logger.debug("FTP Server Configuration is loaded successfully.");
			fis.close();
		}
		catch (FileNotFoundException e) 
		{
			initResult=AdminClientConfig.LOAD_FAIL;
			logger.info("Config. file not found.");
		} 
		catch (IOException e) 
		{
			initResult=AdminClientConfig.LOAD_FAIL;
			logger.info("An exception occur when loading config. file.");
		} 
		finally
		{
			fis=null;
		}
		return initResult;
	}

	@Override
	public boolean isSupportPassiveMode() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPassivePortSpecified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getServerPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getFtpMessage(String key) 
	{
		return ftpMessageBundle.getMessage(key);
	}

	@Override
	public int getMaxConnection() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCommandChannelConnectionTimeOut() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getServerLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAllBindAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConsoleHeading() 
	{
		if (bundle.containsKey("ftpServerConsoleHeading"))
			return bundle.getString("ftpServerConsoleHeading");
		else
			return null;
	}

}
