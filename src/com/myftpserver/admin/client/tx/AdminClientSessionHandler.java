package com.myftpserver.admin.client.tx;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.Logger;

import com.myftpserver.admin.AdminFunction;
import com.myftpserver.admin.AdminObject;
import com.myftpserver.admin.object.AdminUser;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AdminClientSessionHandler extends SimpleChannelInboundHandler<Object> 
{
	private AdminObject adminObject=new AdminObject(); 
	private final BlockingQueue<Object> answer = new LinkedBlockingQueue<Object>();
	private Logger logger;
	private Object serverAnswer;
	private volatile Channel ch=null;
	public AdminClientSessionHandler(Logger logger) 
	{
		this.logger=logger;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		logger.debug("client channel activated.");
	}
	@Override
	public void channelRegistered(ChannelHandlerContext ctx)
            throws Exception
	{
		logger.debug("client channel registered.");
		ch=ctx.channel();
	}
	public void login(AdminUser adminUser)
	{
		logger.debug("client login server");
		
		adminObject.setAdminFunctionCode(AdminFunction.ADMIN_LOGIN);
		adminObject.setAdminObject(adminUser);
		ch.writeAndFlush(adminObject);
		boolean interrupted = false;
		for (;;) {
		    try {
		    	serverAnswer = answer.take();
		        break;
		    } catch (InterruptedException ignore) {
		        interrupted = true;
		    }
		}
		if (interrupted) 
		{
			Thread.currentThread().interrupt();
		}
		logger.debug("Login result:"+serverAnswer);
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		logger.debug("client receive:"+msg+"|");
		answer.add(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
