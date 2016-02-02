package com.myftpserver.handler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.Logger;

import com.myftpserver.PassiveServer;
import com.util.Utility;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class SendFileHandler extends SimpleChannelInboundHandler<ByteBuf> implements ChannelFutureListener 
{
	private Logger logger;
	private String remoteIp,line;
	private FtpSessionHandler fs;
	private ChannelFuture cf=null;
	private BufferedReader br=null;
	private PassiveServer passiveServer=null;
	
	/**
	 * Send file handler
	 * It also handle send file completed and channel close event.
	 * @param fs FtpSessionHandler object
	 */
	public SendFileHandler(FtpSessionHandler fs)
	{
		this.fs=fs;
		this.logger=fs.getLogger();
		this.remoteIp=fs.getClientIp();
		this.passiveServer=fs.getPassiveServer();
	}
	@Override
	public void handlerAdded(ChannelHandlerContext ctx)throws IOException
	{
		if (passiveServer!=null)
		{
			try 
			{
				sendFile(ctx);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws IOException 
	{
		if (passiveServer==null)
		{	
			try 
			{
				sendFile(ctx);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
    }
	private void sendFile(ChannelHandlerContext ctx) throws UnsupportedEncodingException, FileNotFoundException,IOException,Exception 
	{
		Logger logger=fs.getLogger();
		logger.debug("Data type="+fs.getDataType()+"|");
		if (fs.getDataType().equals("A"))
		{
			br=new BufferedReader(new InputStreamReader(new FileInputStream(fs.getDownloadFile()),"ISO-8859-1"));
			while ((line = br.readLine()) != null) 
			{
				while (!ctx.channel().isWritable());
				cf=ctx.writeAndFlush(Unpooled.copiedBuffer(line+"\r\n",CharsetUtil.ISO_8859_1));
			}
			br.close();
			operationComplete(cf);
		}
		else
			ctx.writeAndFlush(new ChunkedFile(fs.getDownloadFile())).addListener(this);
	}	
	@Override
	public void operationComplete(ChannelFuture cf) throws Exception 
	{
		if (cf.channel().isOpen())  //it handle file sent complete event
		{	
			if (fs.isPassiveModeTransfer)
			{
				passiveServer.stop();
				passiveServer=null;
				fs.setPassiveServer(passiveServer);
			}
			else
				cf.channel().close();
		}
		else
		{	//it handle channel close event
			String message=fs.getFtpMessage("226_Transfer_Ok");
			Utility.sendMessageToClient(fs.getChannel(),logger, remoteIp,message);
		}		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext arg0, ByteBuf arg1)throws Exception 
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable t)	throws Exception 
	{
		t.printStackTrace();
	}

}
