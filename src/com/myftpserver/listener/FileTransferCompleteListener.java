package com.myftpserver.listener;

import com.myftpserver.PassiveServer;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class FileTransferCompleteListener implements ChannelFutureListener 
{
	PassiveServer txServer=null;
	public FileTransferCompleteListener(PassiveServer txServer)
	{
		this.txServer=txServer;
	}
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception {
		// TODO Auto-generated method stub
		/*if (txServer==null)
			cf.channel().close().addListener(new ActiveChannelCloseListener());
		else*/
			cf.channel().close().addListener(new PassiveChannelCloseListener(txServer));
			
		System.out.println("File Transfer completed.");
	}

}
