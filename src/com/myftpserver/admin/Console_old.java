package com.myftpserver.admin;

import java.io.File;
import java.util.Stack;

import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import com.util.ConfigurationFactory;
import com.myftpserver.abstracts.FtpServerConfig;
import com.myftpserver.admin.popup_windows.ConnectAdminServerPopup;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
public class Console_old 
{
	private JMenu menu;
	private JFrame frame;
	private JMenuBar menuBar;
	private JToolBar toolBar;
	private JMenuItem menuItem;
	
	private static Logger logger=null;
	private FtpServerConfig serverConfig=null;
	public int loadConfigResult=FtpServerConfig.LOAD_FAIL;
	public Console_old() throws Exception
	{
		logger = LogManager.getLogger(Console_old.class.getName()); 
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
		
		// Build the first menu.
		menu = new JMenu("Server");
		menu.setMnemonic(KeyEvent.VK_S);
		menu.getAccessibleContext().setAccessibleDescription("Server related function.");
		// a group of JMenuItems
		menuItem = new JMenuItem("Connect", KeyEvent.VK_C);
		menuItem.getAccessibleContext().setAccessibleDescription("Connect to Admin. server");
		menuItem.addActionListener(new ConnectAdminServerPopup());
		menu.add(menuItem);
		menuBar.add(menu);
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
			Console_old c=new Console_old();
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
