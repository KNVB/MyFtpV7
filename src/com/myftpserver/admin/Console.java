package com.myftpserver.admin;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

public class Console 
{
	JFrame frame;
	JMenuBar menuBar;
	JToolBar toolBar;
	public Console()
	{
		
	}
	public void initUI()
	{
		// Create a toolbar and give it an etched border.
		toolBar = new JToolBar();
		toolBar.setBorder(new EtchedBorder());

		frame = new JFrame("Test");
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		frame.getContentPane().add(toolBar, BorderLayout.NORTH);
		frame.pack();
		
		// Create the menu bar.
		menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
	}
	public static void main(String[] args) 
	{
		Console c=new Console();
		c.initUI();
	}

}
