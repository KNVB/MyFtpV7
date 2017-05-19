package com;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import org.apache.logging.log4j.Logger;

public class AdminClientSessionHandler extends SimpleChannelInboundHandler<Object>
{
	// Stateful properties
    private volatile Channel channel;
    Logger logger;
    public AdminClientSessionHandler(Logger logger) 
	{
    	this.logger=logger;
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) 
	{
		/*System.out.println("Client active");
		ctx.writeAndFlush(u);  */
		logger.debug("Client Channel activated.");
	}

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
    	logger.debug("Channel register");
        channel = ctx.channel();
    }

	public void login(User user)throws Exception {
		logger.debug("Send User object");
		channel.writeAndFlush(user);
		
	}
	@Override
	protected void channelRead0(ChannelHandlerContext arg0, Object arg1)
			throws Exception {
		// TODO Auto-generated method stub
		
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
