package com.myftpserver.admin.client.listener;
import com.myftpserver.admin.client.AdminClient;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
public class AdminChannelCloseListner implements ChannelFutureListener
{
	private AdminClient adminClient;
	public AdminChannelCloseListner(AdminClient adminClient) 
	{
		this.adminClient=adminClient;
	}

	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		this.adminClient.channelClosed();
	}

}
