package com.myftpserver.admin.util;
import java.io.File;

import java.util.ArrayList;
import java.util.Enumeration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.lang.reflect.Constructor;
import java.util.PropertyResourceBundle;

import org.apache.logging.log4j.Logger;

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
	 * Get all IP address of the current machine.<br>
	 * http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	 * @return Array of IP addresses
	 * @throws UnknownHostException
	 */
	public static final String[] getLocalHostLANAddress() throws UnknownHostException
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
		return (String[]) result.toArray();
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
	/**
	 * Instantiate a class from key of a bundle
	 * @param key 
	 * @param bundle Resource bundle
	 * @return object
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public static final Constructor<?> getObject(String key,PropertyResourceBundle bundle) throws NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		@SuppressWarnings("rawtypes")
		Constructor c=null;
		c=Class.forName(bundle.getString(key)).getConstructor(Logger.class);
		return c;
	}
	/**
	 * Get all supporting raw FTP command
	 * @throws ClassNotFoundException 
	 */
	public static final String getAllSupportingCommand() throws ClassNotFoundException
	{
		int i=1;
		File directory=null;
		StringBuffer temp=new StringBuffer();
		
		String pckgname="com.myftpserver.command";
		try { 
		      directory=new File(Thread.currentThread().getContextClassLoader().getResource(pckgname.replace('.', '/')).getFile());
		      for (String fileName :directory.list())
		      {
		    	  
		    	  fileName =fileName.substring(0,fileName.lastIndexOf(".class"));
		    	  if (i==10)
		    	  {
		    		 temp.append(fileName+"\r\n");
		    		 i=1;
		    	  }
		    	  else
		    	  { 
		    		  temp.append(String.format("%1$-5s", fileName));
		    		  i++;
		    	  } 
		      }
		      return temp.toString();
		    } 
		catch(NullPointerException x) 
		{ 
		      throw new ClassNotFoundException(pckgname+" does not appear to be a valid package"); 
		} 
	}
}
