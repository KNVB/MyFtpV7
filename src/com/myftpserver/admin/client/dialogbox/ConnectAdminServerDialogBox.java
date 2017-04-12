package com.myftpserver.admin.client.dialogbox;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.apache.logging.log4j.Logger;

import com.myftpserver.admin.client.Client;
import com.myftpserver.admin.client.Console;

public class ConnectAdminServerDialogBox implements ActionListener
{
	JButton okButton=new JButton("Ok");
    JButton cancelButton=new JButton("Cancel");
	JTextField adminServerName = new JTextField();
    JTextField adminServerPort = new JTextField();
    JDialog dialog=null;
    Logger logger=null;
    Console console=null;
    public ConnectAdminServerDialogBox(Console console,JFrame father, Logger logger)
    {
    	 dialog=new JDialog(father,"Connect to Admin. server",true); 
    	 JPanel panel = new JPanel(new GridLayout(3, 2));
         Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
         panel.setBorder(padding);
         panel.add(new JLabel("Admin. Server Host name/IP Address:"));
         panel.add(adminServerName);
         panel.add(new JLabel("Admin. Server Port No.:"));
         panel.add(adminServerPort);
         panel.add(okButton);
         panel.add(cancelButton);
         okButton.addActionListener(this);
         cancelButton.addActionListener(this);
         dialog.setContentPane(panel);
         dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         dialog.pack();
         dialog.setLocationRelativeTo(null); // show in the center of screen
        
         this.console=console;
         this.logger=logger;
    }
    public void show() 
    {
    	dialog.setVisible(true);
	}	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(okButton))
		{
			Client adminClient;
			try
			{
				 adminClient = new Client(adminServerName.getText(), Integer.parseInt(adminServerPort.getText()),logger);
				 console.updateUI(adminClient);
				 dialog.dispose();
			}
			catch (NumberFormatException|InterruptedException ex)
			{
				JOptionPane.showMessageDialog(dialog, "Connection to Admin. Server failured.");
			}
		}
		else
		{
			dialog.dispose();
		}
	}
	

}

