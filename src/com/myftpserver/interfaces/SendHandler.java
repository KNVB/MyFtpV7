package com.myftpserver.interfaces;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
/*
* Copyright 2004-2005 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
* 
* @author SITO3
*
*/
public abstract class SendHandler implements ChannelInboundHandler,ChannelFutureListener  
{
	public abstract void closeChannel(ChannelHandlerContext ctx)throws Exception;
	public abstract void startToSend(ChannelHandlerContext ctx)throws Exception;
	public abstract void handlerAdded(ChannelHandlerContext ctx) throws Exception;
	public abstract void handlerRemoved(ChannelHandlerContext ctx) throws Exception;
	public abstract void operationComplete(ChannelFuture future) throws Exception;
	public abstract void channelRegistered(ChannelHandlerContext ctx) throws Exception;
	public abstract void channelUnregistered(ChannelHandlerContext ctx) throws Exception;	
	public abstract void channelActive(ChannelHandlerContext ctx) throws Exception;
	public abstract void channelInactive(ChannelHandlerContext ctx) throws Exception;
	public abstract void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception;	
	public abstract void channelReadComplete(ChannelHandlerContext ctx) throws Exception;
	public abstract void userEventTriggered(ChannelHandlerContext ctx, Object evt)throws Exception;
	public abstract void channelWritabilityChanged(ChannelHandlerContext ctx)throws Exception;	
	public abstract void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)throws Exception;
}

