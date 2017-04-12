package com.myftpserver.admin.client.impl;

import org.apache.logging.log4j.Logger;

import com.myftpserver.admin.client.abstracts.AdminClientConfig;
import com.myftpserver.admin.client.util.MessageBundle;


import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.PropertyResourceBundle;




public class MyAdminClientConfig extends AdminClientConfig 
{
	private FileInputStream fis=null;
	private MessageBundle ftpMessageBundle;
	private PropertyResourceBundle bundle=null;
	private String configFile = "conf/adminClient-config";
	/**
	 *  MyFtpServerConfig is a concrete subclass of {@link com.myftpserver.admin.client.abstracts.AdminClientConfig}
	 * @param logger
	 */
	public MyAdminClientConfig(Logger logger) {
		super(logger);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int load() 
	{
		int initResult=AdminClientConfig.LOAD_OK;
		try
		{
			fis=new FileInputStream(configFile);
			bundle = new PropertyResourceBundle(fis);
			logger.debug("Admin. Client Configuration is loaded successfully.");
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
	public String getConsoleHeading() 
	{
		if (bundle.containsKey("adminConsoleHeading"))
			return bundle.getString("adminConsoleHeading");
		else
			return null;
	}

}
