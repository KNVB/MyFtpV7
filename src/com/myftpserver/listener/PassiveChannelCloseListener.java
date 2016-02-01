package com.myftpserver.listener;

import com.myftpserver.PassiveServer;
import com.myftpserver.User;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.UserManager;
import com.util.Utility;

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
public class PassiveChannelCloseListener implements ChannelFutureListener 
{
	private Logger logger;
	private String remoteIp;
	private FtpSessionHandler fs;
	/**
	 * It is triggered when a passive mode channel is closed.  
	 * @param fs FTP session 
	 */
	public PassiveChannelCloseListener(FtpSessionHandler fs) 
	{
		this.fs=fs;
		this.remoteIp=fs.getClientIp();
		this.logger=fs.getLogger();
	}

	@Override
	public void operationComplete(ChannelFuture arg0) throws Exception 
	{
		String message=fs.getFtpMessage("226_Transfer_Ok");
		logger.debug("Active Mode Transfer channel is closed");
		PassiveServer passiveServer=fs.getPassiveServer();
		if (passiveServer!=null)
		{
			passiveServer.stop();
			passiveServer=null;
			fs.setPassiveServer(passiveServer);
		}
		User user=fs.getUser();
		if ((fs.getUploadFile()!=null) &&(fs.getUploadFile().exists()))
		{	
			logger.debug("Uploaded file ="+fs.getUploadFile().getName()+",size="+fs.getUploadFile().length());
			if (user.getQuota()>-1.0)
			{
				double quota=user.getQuota()*1024.0,diskSpaceUsed=user.getDiskSpaceUsed()*1024;
				diskSpaceUsed+=fs.getUploadFile().length();
				if (diskSpaceUsed<=quota)
				{
					UserManager um=fs.getServerConfig().getUserManager();
					user.setDiskSpaceUsed(diskSpaceUsed/1024.0);
					um.upDateUserInfo(user);
				}
				else
				{
					logger.debug("File name="+fs.getUploadFile().getAbsolutePath());
					message=fs.getFtpMessage("550_Quota_Exceed");
					message=message.replace("%1",String.valueOf(quota/1024.0));
					message=message.replace("%2",String.valueOf(diskSpaceUsed/1024.0));
				}
			}
			fs.setUploadFile(null);
		}
		Utility.sendMessageToClient(fs.getChannel(),logger, remoteIp,message);
	}
}
