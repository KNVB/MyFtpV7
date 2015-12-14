package com.myftpserver;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.util.Stack;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.io.FileNotFoundException;

import com.myftpserver.Configuration;
import com.myftpserver.channelinitializer.CommandChannelInitializer;

import io.netty.channel.EventLoopGroup;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
/**
 * 
 * @author SITO3
 * 
 */
public class MyFtpServer 
{
	/**
	 * Specify the transfer is send a file to client
	 */
	public static final int SENDFILE=0;
	/**
	 * Specify the transfer is receive a file to client
	 */	
	public static final int RECEIVEFILE=1;
	/**
	 * Specify the transfer is send a file listing to client
	 */
	public static final int SENDDIRLIST=2;
	
	private static int connectionCount=0;
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Logger logger=null;
	private Configuration config=null;
	private Stack<Integer> passivePorts;
//-------------------------------------------------------------------------------------------
    /**
     * FTP Server object
     */
	public MyFtpServer()
	{
    	if (initLogger())
		{
			config=new Configuration(logger);
			if (config.load(this))
			{
				logger.info("Server Initialization completed.");
				if ((config.isSupportPassiveMode()) && (config.isPassivePortSpecified()))
				{
					passivePorts=config.passivePorts;
					//logger.debug(passivePorts==null);
				}
				logger.info("Available passive port:"+passivePorts.toString());
			}
		}
	}
 //-------------------------------------------------------------------------------------------
    private boolean initLogger()
	{
		boolean result=false;
		Properties logp = new Properties();
		try 
		{	
			logp.load(new FileReader("conf/log4j.properties"));
			PropertyConfigurator.configure(logp);
			logger=Logger.getLogger("My Ftp Server");
			logger.info("Log4j is ready.");
			result=true;	
		}
		catch (FileNotFoundException e) 
		{
			System.out.println("file log4j.properties not found:"+e.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.println("An exception occur when loading file log4j.properties.");
		}
		return result;
	}
//-------------------------------------------------------------------------------------------	
    /**
	 * Get message logger
	 * @return message logger 
	 */
    public Logger getLogger() 
	{
		return logger;
	}
//-------------------------------------------------------------------------------------------
	/**
	 * Get Configuration object
	 * @return Configuration object
	 */
	public Configuration getConfig()
	{
		return config;
	}
//-------------------------------------------------------------------------------------------
	/**
	 * Check whether the concurrent connection is over the limit
	 * @return true when the concurrent connection is over the limit
	 */
	public synchronized boolean isOverConnectionLimit()
	{
		if (connectionCount<config.getMaxConnection())
		{	
			connectionCount++;
			return false;
		}
		else			
			return true;
	}
//-------------------------------------------------------------------------------------------	
	/**
	 * Called by FtpSessionHandler object when a FTP session is ended.
	 */
	public synchronized void sessionClose()
	{
		logger.debug("Before:"+connectionCount);
		connectionCount--;
		logger.debug("After:"+connectionCount);
	}
//-------------------------------------------------------------------------------------------	
	/**
	 * Get next available passive port for data transfer
	 * @return port no.<br>
	 *         -1 when no. passive port is available.
	 */
	public synchronized int getNextPassivePort()
	{
		int nextPassivePort=-1;
		if (config.isSupportPassiveMode())
		{
			if (config.isPassivePortSpecified())
			{
				if (passivePorts.size()>0)
					nextPassivePort=passivePorts.pop();
			}
		}
		return nextPassivePort;
	}
//-------------------------------------------------------------------------------------------	
	/**
	 * Return port no. to passive port pool
	 * @param port the return passive port 
	 */
	public void returnPassivePort(int port) 
	{
		if (!passivePorts.contains(port))
		{	
			passivePorts.push(port);
			logger.debug("Passive Port:"+port+" return");
		}
	}		
//-------------------------------------------------------------------------------------------
/**
 *  start FTP server
 */
	public void start()
	{
		try 
        {
			ServerBootstrap bootStrap = new ServerBootstrap();
            bootStrap.group(bossGroup, workerGroup);
            bootStrap.channel(NioServerSocketChannel.class);
            bootStrap.childHandler(new CommandChannelInitializer(this));
            bootStrap.localAddress(config.getServerPort());         
            logger.info("My FTP Server is started.");

            // Wait until the server socket is closed.
            bootStrap.bind();
        } 
        catch (Exception e) 
        {
			e.printStackTrace();
			this.stop();
		}	        
	}
//-------------------------------------------------------------------------------------------	
/**
*  stop FTP server
*/
	public void stop()
	{
    	bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        logger.info("Server shutdown gracefully.");
	}
//-------------------------------------------------------------------------------------------	
	public static void main(String[] args) throws Exception 
	{
		MyFtpServer m=new MyFtpServer();
		m.start();
	}
}
