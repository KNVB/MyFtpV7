package com.myftpserver.command;

import com.myftpserver.handler.FtpSessionHandler;
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
public class STRU implements com.myftpserver.interfaces.FtpCommandInterface
{

	@Override
	public String helpMessage(FtpSessionHandler fs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FtpSessionHandler fs, String param) 
	{
		String message=fs.getFtpMessage("200_Structure_set_to");
		if (param==null)
		{
			message=fs.getFtpMessage("504_Command_Not_Support_This_Parameter");

		}
		else
		{
			param=param.trim();
			if (param.equalsIgnoreCase("F"))
			{
				message=fs.getFtpMessage("200_Structure_set_to");
				message=message.replace("%1", param);
			}
			else
				message=fs.getFtpMessage("504_Command_Not_Support_This_Parameter");
		}
		Utility.sendMessageToClient(fs.getChannel(),fs.getLogger(),fs.getClientIp(),message);
	}
	
}
