package com.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.logging.log4j.Logger;



import com.myftpserver.handler.FtpSessionHandler;
import com.myftpserver.listener.CommandCompleteListener;
import com.myftpserver.listener.SessionClosureListener;

import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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
public class Utility
{
	/**
	 * Utility for sending response message to client and then perform some action 
	 */
	public Utility()
	{
		
	}
	/**
	 * It sends a good bye message to client and then close a ftp channel
	 * @param ch ftp channel
	 * @param logger message logger
	 * @param remoteIp client IP address
	 * @param goodByeMessage Good bye message
	 */
	public static void disconnectFromClient(Channel ch, Logger logger,String remoteIp,String goodByeMessage)
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(goodByeMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new SessionClosureListener(null,ch,logger,remoteIp,goodByeMessage));
	}
	/**
	 * It sends a response message to client
	 * @param ch ftp channel
	 * @param logger  message logger
	 * @param remoteIp client IP address 
	 * @param ftpMessage response message
	 */
	public static void sendMessageToClient(Channel ch, Logger logger,String remoteIp,String ftpMessage) 
	{
		ch.writeAndFlush(Unpooled.copiedBuffer(ftpMessage+"\r\n",CharsetUtil.UTF_8)).addListener(new CommandCompleteListener(logger,remoteIp,ftpMessage));
	}
	/**
	 * It sends a file list to client
	 * @param responseCtx response channel
	 * @param fs ftp session
	 * @param resultList File List
	 * @throws InterruptedException
	 */
	public static void sendFileListToClient(ChannelHandlerContext responseCtx,FtpSessionHandler fs,StringBuffer resultList) throws InterruptedException 
	{
		Logger logger=fs.getLogger();
		/*if (fs.isPassiveModeTransfer)
		{
			logger.info("Transfer File Listing in Passive mode");
			PassiveServer ps=new PassiveServer(fs);
			ps.sendFileNameList(resultList, responseCtx);
			sendMessageToClient(responseCtx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("150_Open_Data_Conn"));
		}
		else
		{
			logger.info("Transfer File Listing in Active mode");
			sendMessageToClient(responseCtx.channel(), logger,fs.getClientIp(),fs.getFtpMessage("150_Open_Data_Conn"));
			ActiveClient activeClient=new ActiveClient(fs,responseCtx);
			activeClient.sendFileNameList(resultList);
		}*/
	}
	
	/**
	 * Prepare response for system inquiry
	 * @param logger Message logger
	 * @return A response for system inquiry
	 */
	public static final String getSystemType(Logger logger)
	{
		 String loc = System.getProperty("user.timezone");
	        final int p = loc.indexOf("/");
	        if (p > 0) {
	            loc = loc.substring(0, p);
	        }
	        loc = loc + "/"+ System.getProperty("user.language");
	        String result=System.getProperty("os.arch") + " "
	                + System.getProperty("os.name") + " "
	                + System.getProperty("os.version") + ", " + loc;
	        logger.debug("System type="+result);
	        return result;
	}
	/**
	 * Get all IP address of the machine
	 * http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	 * @return Array of IP address
	 * @throws UnknownHostException
	 */
	public static final ArrayList<String> getLocalHostLANAddress() throws UnknownHostException
	{
		String ip;
		ArrayList<String> result = new ArrayList<String>();
		try
		{
			InetAddress candidateAddress = null;
			// Iterate all NICs (network interface cards)...
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();)
			{
				NetworkInterface iface =ifaces.nextElement();
				// Iterate all IP addresses assigned to each card...
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();)
				{
					InetAddress inetAddr = inetAddrs.nextElement();
					if (inetAddr.isSiteLocalAddress()) 
					{
						// Found non-loopback site-local address. Return it
						// immediately...
						ip = extractIPAddress(inetAddr.toString());
						if (!result.contains(ip))
							result.add(ip);
					}
					else
					{
						if (candidateAddress == null)
						{
							// Found non-loopback address, but not necessarily
							// site-local.
							// Store it as a candidate to be returned if site-local
							// address is not subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback
							// non-site-local addresses as candidates,
							// only the first. For subsequent iterations, candidate
							// will be non-null.
						}
					}
					
				}
			}
			if (candidateAddress != null)
			{
				// We did not find a site-local address, but we found some other
				// non-loopback address.
				// Server might have a non-site-local address assigned to its
				// NIC (or it might be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				ip = extractIPAddress(candidateAddress.toString());
				if (!result.contains(ip))
					result.add(ip);
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost()
			// returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) 
			{
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			ip = extractIPAddress(jdkSuppliedAddress.toString());
			if (!result.contains(ip))
				result.add(ip);			
		}
		catch (Exception e) 
		{
			UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
		return result;
	}
	/**
	 * Extract IP address 
	 * @param in InetAddess to string
	 * @return IP address in format
	 */
	private static final String extractIPAddress(String in) 
	{ 
		int index; 
		String result; 
		index=in.indexOf("/"); 
		result=in.substring(index+1); 
		index=result.indexOf("%"); 
		if (index>-1) 
		{ 
		   result=result.substring(0,index);                 
		} 
		return result; 
	}	
}