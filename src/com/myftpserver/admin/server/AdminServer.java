package com.myftpserver.admin.server;

import io.netty.channel.ChannelFutureListener;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.myftpserver.abstracts.MyServer;
import com.myftpserver.admin.server.tx.AdminServerChannelInitializer;
import com.myftpserver.admin.server.tx.ServerBindListener;
public class AdminServer<T> extends MyServer<T>
{
	
	private Logger logger;
	private static int connectionCount=0;
	public AdminServer(Logger logger) 
	{
		super(logger);
		try 
		{
			//super.setConnectionAcceptanceType(MyServer.ACCEPT_SINGLE_CONNECTION);
			//super.setServerIOType(BLOCKED_IO_TYPE);
			this.logger=logger;
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * Check whether the concurrent connection is over the limit
	 * @return true when the concurrent connection is over the limit
	 */
	public synchronized boolean isOverConnectionLimit()
	{
		if (connectionCount<1)
		{	
			connectionCount++;
			return false;
		}
		else			
			return true;
	}
	/**
	 * Get message logger
	 * @return message logger 
	 */
    public Logger getLogger() 
	{
		return logger;
	}
	/**
	 * Called by AdminChannelClosureListener object when an admin. session is ended.
	 * It reduce the concurrent connection count by 1.
	 */
	public synchronized void sessionClose() 
	{
		if (connectionCount>0)
		{
			logger.debug("Before:"+connectionCount);
			connectionCount--;
			logger.info("Concurrent Connection Count:"+connectionCount);
		}
	}
	public void start(ChannelFutureListener bindListener)
	{
		super.start(bindListener);
	}
	/**
	 * Stop the server
	 */
	public void stop()
	{
		super.stop();
		logger.debug("Server shutdown gracefully.");
	}
//-------------------------------------------------------------------------------------------	
	public static void main(String[] args)  
	{
		//Scanner scanIn = new Scanner(System.in);
		Logger logger = LogManager.getLogger(AdminServer.class.getName());
		AdminServer<Object> adminServer=new AdminServer<Object>(logger);
		adminServer.setServerPort(4466);
		adminServer.setChildHandlers(new AdminServerChannelInitializer(adminServer,logger));
		adminServer.start(new ServerBindListener(logger,adminServer));
		//scanIn.nextInt();
		//adminServer.stop();
	}
	
}