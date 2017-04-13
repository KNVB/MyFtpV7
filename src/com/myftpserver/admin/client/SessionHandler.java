package com.myftpserver.admin.client;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SessionHandler extends SimpleChannelInboundHandler<String>
{
	private Logger logger;
	private AdminConsole adminConsole;
	public SessionHandler(Logger logger,AdminConsole adminConsole) 
	{
		this.logger=logger;
		this.adminConsole=adminConsole;
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg)
			throws Exception {
		logger.debug("Server response:"+msg);
		adminConsole.processServerResponse(ctx,msg);
	}
	/**
	 * Calls ChannelHandlerContext.fireExceptionCaught(Throwable) to forward to the next ChannelHandler in the ChannelPipeline. Sub-classes may override this method to change behavior.
	 * @param ctx the channel that user input command
	 * @param cause the exception cause  
	 */
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) 
    {
		adminConsole.showErrorMessage(cause.getMessage());
    }
}
