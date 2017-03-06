package com.myftpserver.admin.popup_windows;

import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectAdminServerPopup implements ActionListener {

	private static void display() {
       
        JTextField adminServerIpAddress = new JTextField();
        JTextField adminServerPort = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2));
       
        panel.add(new JLabel("Admin. Server Host name/IP Address:"));
        panel.add(adminServerIpAddress);
        panel.add(new JLabel("Admin. Server Port No.:"));
        panel.add(adminServerPort);
        int result = JOptionPane.showConfirmDialog(null, panel, "Test",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println(
                 " " + adminServerIpAddress.getText()
                + " " + adminServerPort.getText());
        } else {
            System.out.println("Cancelled");
        }
    }
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		display();

	}

}
