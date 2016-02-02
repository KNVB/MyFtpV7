package com.myftpserver.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

import org.apache.logging.log4j.Logger;

import com.util.Utility;
import com.myftpserver.User;
import com.myftpserver.PassiveServer;
import com.myftpserver.interfaces.UserManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

public class ReceiveFileHandler extends ChannelInboundHandlerAdapter implements ChannelHandler,ChannelFutureListener  
{
	private User user;
	private Logger logger;
	private File tempFile=null;
	private FtpSessionHandler fs;
	boolean isUploadSuccess=true;
	private String remoteIp,message;
	private BufferedOutputStream bos=null;
	private PassiveServer passiveServer=null;
	/**
	 * Receive file handler
	 * It also handle channel close event.
	 * @param fs FtpSessionHandler object
	 */
	public ReceiveFileHandler(FtpSessionHandler fs)
	{
		this.fs=fs;
		this.user=fs.getUser();
		this.logger=fs.getLogger();
		this.remoteIp=fs.getClientIp();
		this.passiveServer=fs.getPassiveServer();
		message=fs.getFtpMessage("226_Transfer_Ok");
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
	{
		if (bos==null)
		{
			User user=fs.getUser();
			if (user.getUploadSpeedLitmit()==0L)
				logger.info("File upload speed is limited by connection speed");
			else
			{	
				ctx.channel().pipeline().addFirst("TrafficShapingHandler",new ChannelTrafficShapingHandler(0L,user.getUploadSpeedLitmit()*1024));
				logger.info("File upload speed limit:"+user.getUploadSpeedLitmit()+" kB/s");
			}
			ctx.channel().closeFuture().addListener(this);
			tempFile=File.createTempFile("temp-file-name", ".tmp");
			fs.setUploadTempFile(tempFile);
			bos=new BufferedOutputStream(new FileOutputStream(tempFile));
		}
		ByteBuf in = (ByteBuf) msg;
		//logger.debug("ReceiveFileHandler channelRead buffer capacity="+in.capacity()+",readable byte count="+in.readableBytes());
	    try 
	    {
	        while (in.isReadable()) 
	        { 
	        	in.readBytes(bos,in.readableBytes());
	        }
	        bos.flush();
	    } finally {
	        in.release();
	    }		
	}	
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		if (bos!=null)
		{
			try
			{
				bos.flush();
				bos.close();
				bos=null;
				logger.debug("RecevieHandler channel closed.");
				if (fs.isPassiveModeTransfer)
				{	
					passiveServer.stop();
					passiveServer=null;
					fs.setPassiveServer(passiveServer);
				}
				if (fs.getUploadFile()==null)
				{
					isUploadSuccess=false;
				}
				else	
				{
					if (user.getQuota()>-1.0) 
					{	
						double quota=user.getQuota()*1024.0,diskSpaceUsed=user.getDiskSpaceUsed()*1024.0;
						if (fs.getUploadFile().exists())
							diskSpaceUsed=diskSpaceUsed-(new Long(fs.getUploadFile().length())).doubleValue();
						diskSpaceUsed=diskSpaceUsed+(new Long(tempFile.length())).doubleValue();
						
						if  (diskSpaceUsed>quota)
						{
							isUploadSuccess=false;
							logger.info("temp File "+tempFile.getAbsolutePath()+" is deleted.");
							message=fs.getFtpMessage("550_Quota_Exceed");
							message=message.replace("%1",String.valueOf(quota/1024.0));
							message=message.replace("%2",String.valueOf(diskSpaceUsed/1024.0));
						}
						else
						{	
							UserManager um=fs.getServerConfig().getUserManager();
							user.setDiskSpaceUsed(diskSpaceUsed/1024.0);
							um.upDateUserInfo(user);
						}
					}
				}
				
				if (isUploadSuccess)
				{
					tempFile.renameTo(fs.getUploadFile());
					logger.info("File "+fs.getUploadFile().getName()+" uploaded successfully");
				}
				else	
				{
					tempFile.delete();
					logger.info("temp File "+tempFile.getAbsolutePath()+" is deleted.");
				}
				tempFile=null;
				fs.setUploadFile(null);
				fs.setUploadTempFile(tempFile);
				Utility.sendMessageToClient(fs.getChannel(),logger, remoteIp,message);
				
				
			}
			catch (Exception err)
			{
				//logger.debug(err.getMessage());
				err.printStackTrace();
			}
		}	
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception 
	{
		
	}
}
