package com.util;
import java.io.IOException;
import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import org.apache.logging.log4j.Logger;
import java.util.PropertyResourceBundle;
import java.lang.reflect.InvocationTargetException;

import com.myftpserver.abstracts.ServerConfiguration;




public class ConfigurationFactory 
{
	
	private Logger logger=null;
	private FileInputStream fis=null;
	PropertyResourceBundle bundle = null;
	private String configFile = "conf/server-config";
	
	/**
	 * This is factory class for loading user specified class for UserManager, FileManager and Configuration abstract class   
	 * @param logger Logger for message logging
	 */	
	public ConfigurationFactory(Logger logger) 
	{
		this.logger=logger;
		try
		{
			fis=new FileInputStream(configFile);
			bundle = new PropertyResourceBundle(fis);
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
	public ServerConfiguration getServerConfiguration()
	{
		ServerConfiguration sc=null;
		try 
		{
			sc = (ServerConfiguration) getManager("serverConfiguration.classname").newInstance(this.logger);
		}
		catch (IllegalAccessException|InstantiationException|IllegalArgumentException|InvocationTargetException e) 
		{
			e.printStackTrace();
		} 
		return sc;

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