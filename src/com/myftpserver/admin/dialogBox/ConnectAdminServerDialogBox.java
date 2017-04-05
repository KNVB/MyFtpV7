package com.myftpserver.admin.dialogBox;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.Border;

public class ConnectAdminServerDialogBox implements ActionListener
{	
    JFrame frame=new JFrame("Connect to Admin. server");
    JButton okButton=new JButton("Ok");
    JButton cancelButton=new JButton("Cancel");
	JTextField adminServerName = new JTextField();
    JTextField adminServerPort = new JTextField();
	public ConnectAdminServerDialogBox()
	{
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
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); // show in the center of screen
        frame.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(okButton))
		{
			Socket client = new Socket();
			try
			{
				 InetSocketAddress isa = new InetSocketAddress(adminServerName.getText(), Integer.parseInt(adminServerPort.getText()));
				 client.connect(isa, 10000);
			}
			catch (IOException |NumberFormatException ce)
			{
				JOptionPane.showMessageDialog(frame, "Connection to Admin. Server failured.");
			}
		}
		else
		{
			frame.dispose();
		}
	}	
}
