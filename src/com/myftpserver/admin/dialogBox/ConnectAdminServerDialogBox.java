package com.myftpserver.admin.dialogBox;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class ConnectAdminServerDialogBox implements ActionListener
{
	JButton okButton=new JButton("Ok");
    JButton cancelButton=new JButton("Cancel");
	JTextField adminServerName = new JTextField();
    JTextField adminServerPort = new JTextField();
    JDialog dialog=null;
    Socket client = null;
    public ConnectAdminServerDialogBox(JFrame father)
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
         dialog.setVisible(true);
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(okButton))
		{
			client = new Socket();
			try
			{
				 InetSocketAddress isa = new InetSocketAddress(adminServerName.getText(), Integer.parseInt(adminServerPort.getText()));
				 client.connect(isa, 10000);
			}
			catch (IOException |NumberFormatException ce)
			{
				JOptionPane.showMessageDialog(dialog, "Connection to Admin. Server failured.");
			}
		}
		else
		{
			dialog.dispose();
			if (client!=null)
			{	
				try {
					client.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}	

}

