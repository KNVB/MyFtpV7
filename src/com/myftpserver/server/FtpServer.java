package com.myftpserver.server;

import com.myftpserver.abstracts.MyServer;

public class FtpServer<T> extends MyServer<T> 
{
	public FtpServer(String config_json) 
	{
		super(MyServer.ACCEPT_MULTI_CONNECTION);
	}
}
