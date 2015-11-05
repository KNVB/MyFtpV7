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
package com.util;
import java.util.*;

import net.sf.j18n.J18n;
/**
 * @author Roy Tsang
 * origin from http://www.coderanch.com/t/525337/java/java/read-Franch-ResourceBundle
 */
public class FtpMessage 
{
	ResourceBundle  ftpMessage;
	public FtpMessage(Locale l)
	{
		try
		{ 	
			ftpMessage=J18n.getBundle("res.Messages",l);
		}	
		catch (MissingResourceException err)
		{
			//err.printStackTrace();
			System.out.println("cannot found message for locale:"+l);
			Locale defaultLocale=new Locale("en","US");
			ftpMessage= ResourceBundle.getBundle("res.Messages",defaultLocale);
		}
	}
	/**
	 * Get message text
	 * @param key
	 * @param value the corresponding message text
	 * @return
	 */
	public String getMessage(String key)
	{
		return ftpMessage.getString(key);
	}	
	/**
	 * Get message text
	 * @param key
	 * @param value the corresponding message text
	 * @param array of keyword
	 * @return
	 */
	public String getMessage(String key,int count,String keyword)
	{
		String result=ftpMessage.getString(key);
		
		try
		{
			result=result.replace("%"+count, keyword);
		}
		catch (Exception err)
		{
			System.out.println("getMessage error occur=%"+count+",result="+result+",keyword="+keyword);
		}
		return result;
	}	

}