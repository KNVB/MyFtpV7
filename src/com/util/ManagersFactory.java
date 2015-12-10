package com.util;

import com.myftpserver.*;
import com.myftpserver.interfaces.FileManager;
import com.myftpserver.interfaces.UserManager;

import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.PropertyResourceBundle;

public class ManagersFactory 
{
	Logger logger;
	PropertyResourceBundle bundle;
	Configuration config;
	public ManagersFactory(PropertyResourceBundle b,Configuration c)
	{
		bundle=b;
		logger=c.getLogger();
		config=c;
	}
	public FileManager getFileManager()
	{
		FileManager fm=null;
		try 
		{
			fm = (FileManager) getManager("fileManager.classname").newInstance(this.config);
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
			um = (UserManager) getManager("userManager.classname").newInstance(this.config);
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
			c=Class.forName(bundle.getString(key)).getConstructor(Configuration.class);
			return c;
		}
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) 
		{
			System.out.println(e.getMessage()+" not found.");
		}
		return null;				
	}
}
