package com.myftpserver.admin;

import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

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
	private JMenuItem menuItem;
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
		frame = new JFrame("MyFtpServer console");
		JOptionPane.showMessageDialog(null,
			    "Loading Configuration File failure.");
	}
	private void initUI() 
	{
		
		JPanel consolePanel=new JPanel(new GridLayout(1,2));
		//Create a toolbar and give it an etched border.
		JToolBar toolBar = new JToolBar();
		//Create the menu bar.
		JMenuBar menuBar = new JMenuBar();
		// Build the first menu.
		JMenu menu = new JMenu("Server");
		
		
		//Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JScrollPane detailView = new JScrollPane();
        JScrollPane serverView = new JScrollPane();
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
				new ConnectAdminServerDialogBox();
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
	private JTree builtTree()
	{
		JTree tree;
		//Create the nodes.
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Servers");
        DefaultMutableTreeNode user1 =new DefaultMutableTreeNode("User1");
        DefaultMutableTreeNode user2 =new DefaultMutableTreeNode("User2");
        DefaultMutableTreeNode server1 =new DefaultMutableTreeNode("Server1");
        DefaultMutableTreeNode server2 =new DefaultMutableTreeNode("Server2");
        top.add(server1);
        server1.add(user1);
        top.add(server2);
        server2.add(user2);
        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        
        //Optionally play with line styles.  Possible values are
        //"Angled" (the default), "Horizontal", and "None".
        tree.putClientProperty("JTree.lineStyle", "Angled");
        
        //That allows one selection at a time.
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        //Listen for when the selection changes.
       // tree.addTreeSelectionListener(this);
        
        //Collapse all child nodes
        //tree.collapseRow(0);
        return tree;
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
                                  // "javax.swing.plaf.metal.MetalLookAndFeel");
                                  // "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
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
