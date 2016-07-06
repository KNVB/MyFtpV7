package com.myftpserver;
import java.io.File;
import java.util.Stack;

import com.util.ConfigurationFactory;
import com.util.MyServer;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
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
	private MyServer myServer=null;
	private static Logger logger=null;
	private Stack<Integer> passivePorts;
	private static int connectionCount=0;
//-------------------------------------------------------------------------------------------    
	/**
     * FTP Server object
     */
	public MyFtpServer()
	{
		File file = new File("conf/MyFtpServer.xml");
		LoggerContext context =(org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		context.setConfigLocation(file.toURI());
		
		logger = LogManager.getLogger(MyFtpServer.class.getName()); 
		logger.debug("Log4j2 is ready.");
		myServer=new MyServer(MyServer.ACCEPT_MULTI_CONNECTION);
		ConfigurationFactory cf=new ConfigurationFactory(logger); 
	}
//-------------------------------------------------------------------------------------------	
	/**
	 *  start FTP server
	 */
	public void start()
	{
		myServer.start();
	}	
	/**
	*  stop FTP server
	*/
	public void stop()
	{
		logger.info("FTP Server shutdown gracefully.");
		LoggerContext context = (LoggerContext) LogManager.getContext();
		Configurator.shutdown(context);
	}
//-------------------------------------------------------------------------------------------	
	public static void main(String[] args) 
	{
		MyFtpServer m=new MyFtpServer();
		m.start();
	}

}
