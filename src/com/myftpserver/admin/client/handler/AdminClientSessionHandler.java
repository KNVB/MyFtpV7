package com.myftpserver.admin.client.handler;

import org.apache.logging.log4j.Logger;

import com.myftpserver.admin.client.AdminClient;
import com.myftpserver.admin.client.util.Utility;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AdminClientSessionHandler extends SimpleChannelInboundHandler<String> 
{
	private Logger logger;
	private AdminClient adminClient;
	private String adminUserName = new String();
	private String adminPassword = new String();
	public AdminClientSessionHandler(String adminUserName,String adminPassword, Logger logger, AdminClient adminClient) 
	{
		this.logger=logger;
		this.adminClient=adminClient;
		this.adminUserName = adminUserName;
		this.adminPassword = adminPassword;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		Utility.sendMessageToServer(ctx.channel(), logger, adminUserName+"\n"+adminPassword);
	}
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, String arg1)
			throws Exception {
		
		
	}
	/**
	 * Calls ChannelHandlerContext.fireExceptionCaught(Throwable) to forward to the next ChannelHandler in the ChannelPipeline. Sub-classes may override this method to change behavior.
	 * @param ctx the channel that user input command
	 * @param cause the exception cause  
	 */
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) 
   {
		adminClient.showErrorMessage(cause.getMessage());
   }
}
