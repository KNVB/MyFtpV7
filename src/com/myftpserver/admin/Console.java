package com.myftpserver.admin;

import java.io.File;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import com.util.ConfigurationFactory;
import com.myftpserver.abstracts.FtpServerConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
public class Console 
{
	private JFrame frame;
	private JMenuBar menuBar;
	private JToolBar toolBar;
	
	private static Logger logger=null;
	private FtpServerConfig serverConfig=null;
	public int loadConfigResult=FtpServerConfig.LOAD_FAIL;
	public Console() throws Exception
	{
		logger = LogManager.getLogger(Console.class.getName()); 
		logger.debug("Log4j2 is ready.");
		
		ConfigurationFactory cf=new ConfigurationFactory(logger);
		serverConfig=cf.getServerConfiguration();
		loadConfigResult=serverConfig.load();
	}
	public void initUI()
	{
		// Create a toolbar and give it an etched border.
		toolBar = new JToolBar();
		toolBar.setBorder(new EtchedBorder());

		frame = new JFrame(serverConfig.getConsoleHeading());
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		frame.pack();
		
		// Create the menu bar.
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
	}
	public void loadConfigfailure()
	{
		frame = new JFrame("MyFtpServer console");
		JOptionPane.showMessageDialog(frame,
			    "Loading Configuration File failure.");
	}
	public static void main(String[] args) 
	{
		try
		{
			Console c=new Console();
			if (c.loadConfigResult==FtpServerConfig.LOAD_FAIL)
				c.loadConfigfailure();
			else
				c.initUI();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
