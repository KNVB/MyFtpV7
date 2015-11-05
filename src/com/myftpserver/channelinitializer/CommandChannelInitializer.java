package com.myftpserver.channelinitializer;

import java.net.InetSocketAddress;

import com.util.Utility;
import com.myftpserver.MyFtpServer;
import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.listener.CommandChannelClosureListener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

public class CommandChannelInitializer extends ChannelInitializer<Channel>
{
	MyFtpServer s;
	
	public CommandChannelInitializer(MyFtpServer t)
	{
		s=t;
	}
	@Override
	protected void initChannel(Channel ch) throws Exception 
	{
		// TODO Auto-generated method stub
		String remoteIp=(((InetSocketAddress) ch.remoteAddress()).getAddress().getHostAddress());
		if (s.isOverConnectionLimit())
		{
			//Utility.sendMessageToClient(ch, s.getLogger(),remoteIp,s.getConfig().getFtpMessage("330_Connection_Full"));
			String msg=s.getConfig().getFtpMessage("330_Connection_Full");
			Utility.disconnectFromClient(ch,s.getLogger(),remoteIp,msg);
		}
		else
		{
			ch.closeFuture().addListener(new CommandChannelClosureListener(s));
			Utility.sendMessageToClient(ch,s.getLogger(),remoteIp,"220 "+s.getConfig().getFtpMessage("Greeting_Message"));
			ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(s.getConfig().getCommandChannelConnectionTimeOut(), 30, 0));

			ch.pipeline().addLast("decoder",new StringDecoder(CharsetUtil.UTF_8));
			ch.pipeline().addLast("MyHandler",new FtpSessionHandler(ch,s,remoteIp));
		}
		
	}

}
