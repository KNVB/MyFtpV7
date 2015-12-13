package com.myftpserver.channelinitializer;
import com.myftpserver.handler.*;
import com.myftpserver.PassiveServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class PassiveChannelInitializer extends ChannelInitializer<Channel>
{
	private PassiveServer passiveServer;
	public PassiveChannelInitializer(FtpSessionHandler fs,PassiveServer passiveServer)
	{
		this.passiveServer=passiveServer;
	}
	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		passiveServer.setChannel(ch);
		/*switch (mode)
		{
			case MyFtpServer.SENDFILE:ch.pipeline().addLast("streamer", new ChunkedWriteHandler());
									  ch.pipeline().addLast("handler",new SendFileHandler(fileName,fs,responseCtx, passiveServer));
									  break;
			case MyFtpServer.RECEIVEFILE:ch.pipeline().addLast(new ReceiveFileHandler(fs, this.fileName,responseCtx,null));
											break;
			case MyFtpServer.SENDDIRLIST:ch.pipeline().addLast(new SendFileNameListHandler(fileNameList,responseCtx, fs,passiveServer));
											break;
		}*/		
		
	}
}
