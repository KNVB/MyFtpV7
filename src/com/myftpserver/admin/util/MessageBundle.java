package com.myftpserver.admin.util;
import java.util.*;

import net.sf.j18n.J18n;
/**
 * origin from http://www.coderanch.com/t/525337/java/java/read-Franch-ResourceBundle
 */
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
public class MessageBundle 
{
	private Locale defaultLocale;
	private ResourceBundle ftpMessage;
	/**
	 * Message bundle object for localized message
	 * @param locale an Locale object
	 */
	public MessageBundle(Locale locale)
	{
		defaultLocale=locale;
		try
		{ 	
			ftpMessage=J18n.getBundle("res.Messages",defaultLocale);
		}	
		catch (MissingResourceException err)
		{
			//err.printStackTrace();
			System.out.println("cannot found message for locale:"+locale);
			defaultLocale=new Locale("en","US");
			ftpMessage= ResourceBundle.getBundle("res.Messages",defaultLocale);
		}
	}
	/**
	 * Get message text from a key
	 * @param key the message key
	 * @return value the corresponding message text
	 */
	public String getMessage(String key)
	{
		return ftpMessage.getString(key);
	}	
	/**
	 * Get message text from key and replace the nth parameter with replacement
	 * @param key the message key
	 * @param n the nth parameter will be replaced
	 * @param keyword the replacement
	 * @return the final result
	 */
	public String getMessage(String key,int n,String keyword)
	{
		String result=ftpMessage.getString(key);
		
		try
		{
			result=result.replace("%"+n, keyword);
		}
		catch (Exception err)
		{
			System.out.println("getMessage error occur=%"+n+",result="+result+",keyword="+keyword);
		}
		return result;
	}	
	public String getLocale()
	{
		return defaultLocale.toString();
	}
}