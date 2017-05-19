package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class AdminClient
{
	private Logger logger;
	private EventLoopGroup group = new NioEventLoopGroup();
	private Bootstrap b = new Bootstrap(); 
	private Channel ch;
	public AdminClient(Logger logger)
	{
		this.logger=logger;	
	}
	public void connect(String adminServerName, int portNo) throws InterruptedException 
	{
		b.group(group);
		b.channel(NioSocketChannel.class);
		b.handler(new AdminClientChannelInitializer(logger));
		b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		ch=b.connect(adminServerName,portNo).sync().channel();
	}
	private void login(User user) 
	{
		AdminClientSessionHandler adminClientSessionHandler=ch.pipeline().get(AdminClientSessionHandler.class);
		adminClientSessionHandler.login(user);
	}
	public void shutdown()
	{
		logger.debug("Shutdown client");
		group.shutdownGracefully(0,0,TimeUnit.MILLISECONDS);
	}
	public static void main(String[] args) throws InterruptedException  
	{
		int portNo=4466;
		String adminServerName="localhost";
		User user=new User();
		user.setName("³¯¤j¤å");
		user.setPassword("±K½X");
		Logger logger = LogManager.getLogger(AdminClient.class.getName());
		AdminClient adminClient=new AdminClient(logger);
		adminClient.connect(adminServerName,portNo);
		Thread.sleep(3000);
		adminClient.login(user);
		Thread.sleep(3000);
		adminClient.shutdown();
	}
}

