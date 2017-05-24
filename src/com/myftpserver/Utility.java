package com.myftpserver;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.apache.logging.log4j.Logger;

public class Utility 
{
	public static void sendTextMessage(Channel ch, Logger logger,String message,GenericFutureListener<? extends Future<? super Void>> listener)
	{
		if (listener==null)
			ch.writeAndFlush(Unpooled.copiedBuffer(message,CharsetUtil.UTF_8));
		else
			ch.writeAndFlush(Unpooled.copiedBuffer(message,CharsetUtil.UTF_8)).addListener(listener);
	}
}
