package com.myftpserver.admin.client;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.myftpserver.admin.client.abstracts.AdminClientConfig;
import com.myftpserver.admin.client.dialogbox.*;
import com.myftpserver.admin.client.util.ConfigurationFactory;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AdminConsole 
{
	private JFrame frame;
	private JScrollPane detailView = new JScrollPane();
    private JScrollPane serverView = new JScrollPane();
    
    private AdminClient adminClient=null;
    private static Logger logger=null;
	private AdminClientConfig adminConfig=null;
	public int loadConfigResult=AdminClientConfig.LOAD_FAIL;
	private ConnectAdminServerDialogBox connectAdminServerDialogBox;
	public AdminConsole() throws Exception
	{
		logger = LogManager.getLogger(AdminConsole.class.getName()); 
		logger.debug("Log4j2 is ready.");
		
		ConfigurationFactory cf=new ConfigurationFactory(logger);
		adminConfig=cf.getAdminClientConfiguration();
		loadConfigResult=adminConfig.load();
		
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
		JMenu menu = new JMenu("Admin. Server");
		
		connectAdminServerDialogBox=new ConnectAdminServerDialogBox(this,frame,logger);
		//Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        
        frame = new JFrame(adminConfig.getConsoleHeading());
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
				connectAdminServerDialogBox.show();
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
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
            public void windowClosing(WindowEvent e)
            {
				if (adminClient!=null)
				{	
					adminClient.disconnect();
					adminClient=null;
				}
            }
		});
		//serverView.setViewportView(builtTree());
	}
	
	private static void start() 
	{
		try {
			AdminConsole c=new AdminConsole();
			if (c.loadConfigResult==AdminClientConfig.LOAD_FAIL)
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
	public void updateUI(AdminClient adminClient) 
	{
		this.adminClient=adminClient;
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
