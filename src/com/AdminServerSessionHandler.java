package com;

import org.apache.logging.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AdminServerSessionHandler extends SimpleChannelInboundHandler<Object>
{
	private Logger logger;
	public AdminServerSessionHandler(Logger logger) 
	{
		this.logger=logger;
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx,Object obj)
			throws Exception {
		logger.debug("server receive:"+obj+"|");
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
		logger.debug("server channel activated.");
	}
	/**
	 * Calls ChannelHandlerContext.fireExceptionCaught(Throwable) to forward to the next ChannelHandler in the ChannelPipeline. Sub-classes may override this method to change behavior.
	 * @param ctx the channel that user input command
	 * @param cause the exception cause  
	 */
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) 
    {
		logger.debug(cause.getMessage());
    }

}
