package com.myftpserver.handler;
import java.io.*;

import org.apache.log4j.Logger;

import com.util.Utility;
import com.myftpserver.Configuration;
import com.myftpserver.PassiveServer;
import com.myftpserver.listener.ReceiveFilerCompleteListener;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class ReceiveFileHandler extends ChannelInboundHandlerAdapter
{
	private Logger logger;
	private String fileName;
	private FtpSessionHandler fs;
	private Configuration config;
	private ChannelHandlerContext responseCtx;
	private BufferedOutputStream bos=null;
	private PassiveServer passiveServer=null;
	public ReceiveFileHandler(FtpSessionHandler fs,String fileName, ChannelHandlerContext responseCtx, PassiveServer passiveServer)
	{
		this.fs=fs;
		this.config=fs.getConfig();
		this.fileName=fileName;
		this.responseCtx=responseCtx;
		this.passiveServer=passiveServer;
		this.logger=fs.getConfig().getLogger();
	}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)throws IOException,Exception
	{
		if (passiveServer!=null)
			channelActive(ctx);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception 
	{ 
		logger.info("ReceiveFileHandler channel active");
		try
		{
			bos=new BufferedOutputStream(new FileOutputStream(new File(fileName)));
		}
		catch (FileNotFoundException err)
		{
			ctx.channel().close();
			Utility.sendMessageToClient(responseCtx.channel(),logger,fs.getClientIp(),config.getFtpMessage("553_Cannot_Create_File").replace("%1", err.getMessage()));
		}
    }
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
	{ 
		
		ByteBuf in = (ByteBuf) msg;
		logger.debug("ReceiveFileHandler channelRead buffer capacity="+in.capacity()+",readable byte count="+in.readableBytes());
	    try 
	    {
	        while (in.isReadable()) 
	        { 
	           //bos.write(in.readByte());
	        	in.readBytes(bos,in.readableBytes());
	        }
	        bos.flush();
	    } finally {
	        in.release();
	    }
    }
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception 
	{ 
		if (bos!=null)
		{
			try
			{
				bos.flush();
				bos.close();
				bos=null;
				logger.info("ReceiveFileHandler channel inactive");
				ctx.channel().close().addListener(new ReceiveFilerCompleteListener(fs,passiveServer,responseCtx));
			}
			catch (Exception err)
			{
				logger.debug(err.getMessage());
				ctx.channel().close();
			}
		}
		else
		{
			ctx.channel().close();
		}
    }
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
	}
}