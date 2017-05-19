package com.myftpserver.admin.client.handler;

import com.myftpserver.admin.client.AdminClient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.Logger;

public class AdminClientSessionHandler extends SimpleChannelInboundHandler<Object> 
{
	private AdminClient adminClient;
	// Stateful properties
    private volatile Channel channel;
	private Logger logger;
	
	
	private final BlockingQueue<Object> answer = new LinkedBlockingQueue<Object>();
	public AdminClientSessionHandler(Logger logger, AdminClient adminClient) 
	{
		this.logger=logger;
		this.adminClient=adminClient;
	
	}
	@Override
    public void channelRegistered(ChannelHandlerContext ctx) 
	{
        channel = ctx.channel();
    }
	public Object sendRequest(Object obj)
	{
		boolean interrupted = false;
		Object result;
		channel.writeAndFlush(obj);
		for (;;) {
	            try {
	                result = answer.take();
	                break;
	            } catch (InterruptedException ignore) {
	                interrupted = true;
	            }
	        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return result;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx)
	{
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object obj)throws Exception 
	{
		 answer.add(obj);
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
