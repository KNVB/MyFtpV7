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
 * Ftp Server Object
 */
public class MyFtpServer 
{
	
	public static final int SENDFILE=0;
	public static final int RECEIVEFILE=1;
	public static final int SENDDIRLIST=2;
	
	private static int connectionCount=0;
	private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    private Logger logger=null;
	private Configuration config=null;
	private Stack<Integer> passivePorts;
//-------------------------------------------------------------------------------------------
    public MyFtpServer()
	{
    	if (initLogger())
		{
			config=new Configuration(logger);
			if (config.load(this))
			{
				logger.info("Server Initialization completed.");
				if ((config.supportPassiveMode) && (config.havePassivePortSpecified))
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
	public Logger getLogger() 
	{
		return logger;
	}
//-------------------------------------------------------------------------------------------
	public Configuration getConfig()
	{
		return config;
	}
//-------------------------------------------------------------------------------------------
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
	public synchronized void sessionClose()
	{
		logger.debug("Before:"+connectionCount);
		connectionCount--;
		logger.debug("After:"+connectionCount);
	}
//-------------------------------------------------------------------------------------------	
	public synchronized int getNextPassivePort()
	{
		int nextPassivePort=-1;
		if (config.supportPassiveMode)
		{
			if (config.havePassivePortSpecified)
			{
				if (passivePorts.size()>0)
					nextPassivePort=passivePorts.pop();
			}
		}
		return nextPassivePort;
	}
	//-------------------------------------------------------------------------------------------	
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
