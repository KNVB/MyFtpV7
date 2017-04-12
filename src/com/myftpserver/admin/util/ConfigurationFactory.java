package com.myftpserver.admin.util;
import com.myftpserver.admin.abstracts.AdminClientConfig;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.PropertyResourceBundle;
import java.lang.reflect.InvocationTargetException;

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
public class ConfigurationFactory 
{
	private Logger logger=null;
	private FileInputStream fis=null;
	PropertyResourceBundle bundle = null;
	private String configFile = "conf/adminClient-config";
	
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
	public AdminClientConfig getAdminClientConfiguration()
	{
		AdminClientConfig sc=null;
		try {
			sc = (AdminClientConfig) Utility.getObject("adminClientConfig.classname",bundle).newInstance(this.logger);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			
			logger.debug(e.getMessage());
		}
		return sc;
	}	
}
