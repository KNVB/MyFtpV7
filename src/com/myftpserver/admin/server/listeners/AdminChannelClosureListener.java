package com.myftpserver.admin.server.listeners;

import com.myftpserver.admin.server.AdminServer;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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
public class AdminChannelClosureListener implements ChannelFutureListener
{
	private AdminServer<?> adminServer;
	private Logger logger; 
	private String remoteIp=null;
	/**
	 * It is triggered when an user close an ftp session 
	 * @param t FTP server object
	 * @param remoteIp Client ip address
	 */
	public AdminChannelClosureListener(AdminServer<?> t,String remoteIp)
	{
		adminServer=t;
		this.remoteIp=remoteIp;
		this.logger=adminServer.getLogger();
	}
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		logger.info("admin. Channel (Remote IP:"+remoteIp+") is closed.");
		adminServer.sessionClose();
	}
}