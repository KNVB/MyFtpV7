package com.myftpserver.handler;

import com.myftpserver.*;
import org.apache.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Sharable
public class FtpSessionHandler  extends SimpleChannelInboundHandler<String>
{
	private User user;
	private Channel ch;
	
	private MyFtpServer s;
	private Logger logger;
	
	private int clientDataPortNo=-1;
	private PassiveServer passiveServer=null;
	private FtpCommandHandler ftpCommandHandler=null; 
	private String clientIp=new String(),commandString=new String();
	private boolean isLogined=false;
	public boolean isPassiveModeTransfer=false;
	private String userName=new String(),txMode="I",currentPath=new String();
	
	public FtpSessionHandler(Channel ch,MyFtpServer s, String remoteIp)
	{
		super();
		this.s=s;
		this.currentPath="/";
		this.clientIp=remoteIp;
		this.logger=s.getLogger();
		this.isPassiveModeTransfer=false;
		this.ftpCommandHandler=new FtpCommandHandler(this);
		this.ch=ch;
	}
	public Channel getChannel() 
	{
		return ch;
	}
	public void close()
	{
		ch.close();
	}
	public void channelRead0(ChannelHandlerContext ctx, String msg) 
	{
		// TODO Auto-generated method stub
		//ctx.writeAndFlush(Unpooled.copiedBuffer(msg,CharsetUtil.UTF_8)).addListener(new CommandCompleteListener());
		commandString=msg.trim();
		logger.info("commandString="+commandString);
		String commands[]=commandString.split("\n");
		for (String command:commands)
		{
			logger.debug("command:"+command);
			ftpCommandHandler.doCommand(ctx,command.trim(), logger);
		}
	}
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception 
	{
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) 
            {
                //String goodByeMessage=s.getConfig().getFtpMessage("421_Idle_Timeout");
                //goodByeMessage=goodByeMessage.replaceAll("%1",String.valueOf(s.getConfig().getCommandChannelConnectionTimeOut()));
            	//ctx.writeAndFlush(Unpooled.copiedBuffer(goodByeMessage+"\r\n",CharsetUtil.UTF_8));
            	close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
               // ctx.writeAndFlush(new PingMessage());
            }
        }
    }
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) 
    {
        s.getLogger().debug(FtpSessionHandler.class.getName()+" exception occur:");
        s.getLogger().debug(cause.getMessage());
        //cause.printStackTrace();
    }
    public String getClientIp()
    {
    	return clientIp;
    }
    public Configuration getConfig()
    {
    	return s.getConfig();
    }
	public boolean isLogined() 
	{
		// TODO Auto-generated method stub
		return isLogined;
	}
	public void setUserName(String param) 
	{
		// TODO Auto-generated method stub
		this.userName=param;
	}
	public void setIsLogined(boolean l)
	{
		isLogined=l;
	}
	public String getUserName()
	{
		return this.userName;
	}
	public void setCurrentPath(String p)
	{
		currentPath=p;
	}
	public String getCurrentPath()
	{
		return currentPath;
	}
	public String getTransferMode()
	{
		return txMode;
	}
	public void setTransferMode(String mode) 
	{
		// TODO Auto-generated method stub
		txMode=mode;
	}
	public PassiveServer getPassiveServer() 
	{
		// TODO Auto-generated method stub
		return this.passiveServer;
	}
	public void setPassiveServer(PassiveServer passiveServer) 
	{
		// TODO Auto-generated method stub
		this.passiveServer=passiveServer;
	}
	public void setUser(User user) 
	{
		// TODO Auto-generated method stub
		this.user=user;
	}
	public User getUser() {
		// TODO Auto-generated method stub
		return this.user;
	}
	public void setClientDataPortNo(int portNo) 
	{
		// TODO Auto-generated method stub
		clientDataPortNo=portNo;
	}
	public int getClientDataPortNo()
	{
		return clientDataPortNo;
	}
}
