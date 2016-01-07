package com.util;

import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.UserManager;

import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.PropertyResourceBundle;
import java.lang.reflect.InvocationTargetException;
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
public class ManagersFactory 
{
	private Logger logger;
	private PropertyResourceBundle bundle;
	
	/**
	 * Factory class for instantiate an Manager object  
	 * @param b PropertyResourceBundle object
	 * @param logger Message logger
	 */
	public ManagersFactory(PropertyResourceBundle b,Logger logger)
	{
		bundle=b;
		this.logger=logger;
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
		catch (IllegalArgumentException | InvocationTargetException |InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) {
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
		catch (InstantiationException |IllegalArgumentException | InvocationTargetException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
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