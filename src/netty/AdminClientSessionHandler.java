package netty;

import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AdminClientSessionHandler extends SimpleChannelInboundHandler<Object> 
{
	private Logger logger;
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
	public void login(User user)
	{
		logger.debug("client login server");
		ch.writeAndFlush(user);
	}
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		logger.debug("client receive:"+msg+"|");
		//ctx.writeAndFlush(msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
