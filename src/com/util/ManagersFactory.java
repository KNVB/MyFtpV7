package com.util;

import com.myftpserver.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.UserManager;

import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.PropertyResourceBundle;
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
	Logger logger;
	PropertyResourceBundle bundle;
	ServerConfig serverConfig;
	public ManagersFactory(PropertyResourceBundle b,ServerConfig c,Logger logger)
	{
		bundle=b;
		this.logger=logger;
	}
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