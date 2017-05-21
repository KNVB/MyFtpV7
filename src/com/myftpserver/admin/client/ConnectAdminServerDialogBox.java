package com.myftpserver.admin.client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

import org.apache.logging.log4j.Logger;

import com.myftpserver.admin.object.AdminUser;

public class ConnectAdminServerDialogBox  implements ActionListener 
{
	JButton okButton=new JButton("Ok");
    JButton cancelButton=new JButton("Cancel");
	JTextField adminServerName = new JTextField("localhost");
    JTextField adminServerPort = new JTextField("4466");
    JTextField adminUserName = new JTextField("陳大");
    JTextField adminPassword = new JPasswordField("文");
    JDialog dialog=null;
    Logger logger=null;
    AdminConsole adminConsole=null;
    public ConnectAdminServerDialogBox(AdminConsole adminConsole,JFrame father, Logger logger)
    {
    	 dialog=new JDialog(father,"Connect to Admin. server",true); 
    	 JPanel panel = new JPanel(new GridLayout(5, 2));
         Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
         panel.setBorder(padding);
         panel.add(new JLabel("Admin. Server Host name/IP Address:"));
         panel.add(adminServerName);
         panel.add(new JLabel("Admin. Server Port No.:"));
         panel.add(adminServerPort);
         panel.add(new JLabel("Admin. User Name:"));
         panel.add(adminUserName);
         panel.add(new JLabel("Admin. Password:"));
         panel.add(adminPassword);
         
         panel.add(okButton);
         panel.add(cancelButton);
         okButton.addActionListener(this);
         cancelButton.addActionListener(this);
         dialog.setContentPane(panel);
         dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
         dialog.pack();
         dialog.setLocationRelativeTo(null); // show in the center of screen
        
         this.adminConsole=adminConsole;
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
			try
			{
				AdminClient adminClient=new AdminClient(logger);
				AdminUser adminUser=new AdminUser();
				adminUser.setName(adminUserName.getText());
				adminUser.setPassword(adminPassword.getText());
				adminClient.connect(adminServerName.getText(),Integer.parseInt(adminServerPort.getText()));
				adminClient.login(adminUser);
				adminConsole.setAdminClient(adminClient);
				dialog.dispose();
			}
			catch (IllegalArgumentException ex)
			{
				JOptionPane.showMessageDialog(dialog, "Invalid host name or port no.");
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(dialog, "Connection to admin. server failure");
			}
		}
		else
		{
			dialog.dispose();
		}
	}

}