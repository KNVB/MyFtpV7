package com.myftpserver.admin;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.util.ConfigurationFactory;
import com.myftpserver.admin.dialogBox.*;
import com.myftpserver.abstracts.FtpServerConfig;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Console 
{
	private JFrame frame;
	private JScrollPane detailView = new JScrollPane();
    private JScrollPane serverView = new JScrollPane();
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
	public void loadConfigfailure()
	{
		frame = new JFrame("Admin. Server console");
		JOptionPane.showMessageDialog(null,
			    "Loading Configuration File failure.");
	}
	private void initUI() 
	{
		JMenuItem menuItem;
		JPanel consolePanel=new JPanel(new GridLayout(1,2));
		//Create a toolbar and give it an etched border.
		JToolBar toolBar = new JToolBar();
		//Create the menu bar.
		JMenuBar menuBar = new JMenuBar();
		// Build the first menu.
		JMenu menu = new JMenu("Server");
		
		//Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      
        frame = new JFrame(serverConfig.getConsoleHeading());
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		detailView.setWheelScrollingEnabled(true);
		serverView.setWheelScrollingEnabled(true);
		menu.setMnemonic(KeyEvent.VK_S);
		menu.getAccessibleContext().setAccessibleDescription("Server related function.");
		menuItem = new JMenuItem("Connect", KeyEvent.VK_C);
		menuItem.getAccessibleContext().setAccessibleDescription("Connect to Admin. server");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ConnectAdminServerDialogBox(frame);
			}		
		});
		menu.add(menuItem);
		menuBar.add(menu);
		serverView.setBackground(Color.white);
        splitPane.setResizeWeight(.3d);
        splitPane.setLeftComponent(serverView);
        splitPane.setRightComponent(detailView);
        splitPane.setPreferredSize(new Dimension(500, 300));
        //Add the split pane to this panel.
        consolePanel.add(splitPane);
		
		frame.setJMenuBar(menuBar);
		frame.add(consolePanel);
		//Display the window.
		frame.pack();
		frame.setVisible(true);
		//serverView.setViewportView(builtTree());
	}
	
	private static void start() 
	{
		try {
			Console c=new Console();
			if (c.loadConfigResult==FtpServerConfig.LOAD_FAIL)
				c.loadConfigfailure();
			else
			{
				c.initUI();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	 try {
                     UIManager.setLookAndFeel(
                                   //"javax.swing.plaf.metal.MetalLookAndFeel");
                                   //"com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                                 //UIManager.getCrossPlatformLookAndFeelClassName());
                             UIManager.getSystemLookAndFeelClassName());
                     start();
                 } catch (Exception ex) {
                     ex.printStackTrace();
                 }            	
            	
            }
        });
	}
}
