package com.myftpserver.handler;
import java.io.*;

import com.myftpserver.PassiveServer;
import com.myftpserver.listener.FileTransferCompleteListener;

import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInboundHandlerAdapter;




@Sharable
public class ReceiveFileHandler extends ChannelInboundHandlerAdapter
{
	String fileName;
	BufferedOutputStream bos=null;
	PassiveServer passiveServer=null;
	public ReceiveFileHandler(String fileName, PassiveServer passiveServer)
	{
		this.fileName=fileName;
		this.passiveServer=passiveServer;
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception 
	{ 
		//System.out.println("ReceiveFileHandler channelActive");
		bos=new BufferedOutputStream(new FileOutputStream(fileName));
    }
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception 
	{ 
		//System.out.println("ReceiveFileHandler channelRead");
		ByteBuf in = (ByteBuf) msg;
	    try 
	    {
	        while (in.isReadable()) 
	        { 
	           bos.write(in.readByte());
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
		ctx.channel().close().addListener(new FileTransferCompleteListener(passiveServer));
    }
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		cause.printStackTrace();
	}
}