package com.myftpserver.abstracts;

import java.util.Stack;
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
/**
 * 
 * @author SITO3
 *
 */
public abstract class FtpServerConfig 
{
	/**
	 * Message logger
	 */
	protected Logger logger;
	protected String serverLocale="en_us";
	protected int serverPort=21,maxConnection=1,commandChannelConnectionTimeOut=30000;
	protected boolean supportPassiveMode=false,havePassivePortSpecified=false;

	public Stack<Integer> passivePorts=new Stack<Integer>();
	/**
	 * FTP server configuration interface
	 * @param logger Message logger
	 */
	public FtpServerConfig(Logger logger)
	{
		this.logger=logger;
	}
	/**
	 * Does the server support passive mode transfer or not? 
	 * @return return true when the server is support passive mode transfer
	 */
	public abstract boolean isSupportPassiveMode(); 
	/**
	 * Is there any passive port specified?
	 * @return return true when passive port is specified.
	 */
	public abstract boolean isPassivePortSpecified();
	/**
	 * Get FTP server port 
	 * @return the port no. that server is listening
	 */
	public abstract int getServerPort();
	/**
	 *Get properly message text for specified return code 
	 * @param key the message key
	 * @return value the corresponding message text
	 */
	public abstract String getFtpMessage(String key);
	/**
	 * Get FTP server maximum current connection 
	 * @return FTP server maximum current connection
	 */	
	public abstract int getMaxConnection();
	/**
	 * Get FTP command channel time out in second
	 * @return FTP command channel time out in second
	 */		
	public abstract int getCommandChannelConnectionTimeOut();
	/**
	 * Get User Manager object
	 * @return UserManager object
	 */
	public abstract UserManager getUserManager();
	/**
	 * Get File Manager object
	 * @return FileManager object
	 */
	public abstract FileManager getFileManager();
	/**
	 * Get Server default locale
	 * @return Server default locale
	 */
	public abstract String getServerLocale();
	/**
	 * Get all binding address(es)
	 * @return Array of IP address
	 */
	public abstract String[] getAllBindAddress();
}
