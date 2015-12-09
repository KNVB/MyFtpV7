package com.myftpserver.handler;
import java.io.*;

import org.apache.log4j.Logger;

import com.myftpserver.PassiveServer;
import com.myftpserver.listener.FileTransferCompleteListener;

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
	private ChannelHandlerContext responseCtx;
	private BufferedOutputStream bos=null;
	private PassiveServer passiveServer=null;
	public ReceiveFileHandler(FtpSessionHandler fs,String fileName, ChannelHandlerContext responseCtx, PassiveServer passiveServer)
	{
		this.fs=fs;
		this.fileName=fileName;
		this.responseCtx=responseCtx;
		this.passiveServer=passiveServer;
		this.logger=fs.getConfig().getLogger();
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception 
	{ 
		logger.info("ReceiveFileHandler channel active");
		bos=new BufferedOutputStream(new FileOutputStream(new File(fileName)));
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
		bos.flush();
		bos.close();
		bos=null;
		logger.info("ReceiveFileHandler channel inactive");
		ctx.channel().close().addListener(new FileTransferCompleteListener(fs,passiveServer,responseCtx));
    }
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
	}
}