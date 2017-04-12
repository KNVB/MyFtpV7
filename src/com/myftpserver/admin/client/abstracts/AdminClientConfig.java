package com.myftpserver.admin.client.abstracts;

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
public abstract class AdminClientConfig 
{
	/**
	 * It indicated the FTP server configuration file is loaded successfully
	 */
	public static final int LOAD_OK=0;
	/**
	 * It indicated there are something wrong when loading  FTP server configuration file
	 */
	public static final int LOAD_FAIL=1;
	
	protected Logger logger;
	protected String serverLocale="en_us";
	
	protected int serverPort=21,maxConnection=1,commandChannelConnectionTimeOut=30000;
	protected boolean supportPassiveMode=false,havePassivePortSpecified=false;

	public Stack<Integer> passivePorts=new Stack<Integer>();
	/**
	 * The FtpServerConfig  class is an abstract class that provide method for FTP Server Configuration management
	 * @param logger Message logger
	 */
	public AdminClientConfig(Logger logger)
	{
		this.logger=logger;
	}
	/**
	 * Load all server configuration
	 * @return return whether load configuration {@link AdminClientConfig#LOAD_OK success} or {@link AdminClientConfig#LOAD_FAIL not} 
	 */
	public abstract int load();
	public abstract String getConsoleHeading();
}
