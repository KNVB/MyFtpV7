package com.myftpserver.command;

import com.myftpserver.User;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.interfaces.FtpCommandInterface;
import com.util.Utility;

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
public class STAT implements FtpCommandInterface 
{
	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, String param) 
	{
		User user=fs.getUser();
		String bandWidthInfo=new String();
		String message=fs.getFtpMessage("211_Server_Stat");
		message=message.replace("%1", fs.getClientIp());
		message=message.replace("%2", fs.getUserName());
		switch(fs.getDataType())
		{
			case "A":message=message.replace("%3", "ASCII");
					 break;
			case "I":message=message.replace("%3", "Binary");
			 break;
		}
		message=message.replace("%4",String.valueOf(fs.getSessionTimeOut()));
		if (user.getDownloadSpeedLitmit()==0)
			bandWidthInfo=fs.getFtpMessage("No_Download_BW_Limit");
		else
		{	
			bandWidthInfo=fs.getFtpMessage("Download_BW_Limit");
			bandWidthInfo=bandWidthInfo.replace("%1",String.valueOf(user.getDownloadSpeedLitmit()));
		}
		if (user.getUploadSpeedLitmit()==0)
			bandWidthInfo+=" "+fs.getFtpMessage("No_Upload_BW_Limit");
		else
		{
			bandWidthInfo+=" "+fs.getFtpMessage("Upload_BW_Limit");
			bandWidthInfo=bandWidthInfo.replace("%1",String.valueOf(user.getUploadSpeedLitmit()));
		}
		message=message.replace("%5",bandWidthInfo);
		Utility.sendMessageToClient(fs.getChannel(),fs.getLogger(),fs.getClientIp(),message);
	}
}
