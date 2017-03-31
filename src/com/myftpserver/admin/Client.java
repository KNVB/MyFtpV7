package com.myftpserver.admin;

import javax.swing.*;

import java.awt.GridLayout;

public class Client 
{	
	public Client()
	{
        JTextField adminServerIpAddress = new JTextField();
        JTextField adminServerPort = new JTextField();
        JPanel panel = new JPanel(new GridLayout(2, 2));
       
        panel.add(new JLabel("Admin. Server Host name/IP Address:"));
        panel.add(adminServerIpAddress);
        panel.add(new JLabel("Admin. Server Port No.:"));
        panel.add(adminServerPort);
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        UIManager.put("OptionPane.okButtonText", "Connect");
        int result = JOptionPane.showConfirmDialog(null, panel, "Connect to Admin. server",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            System.out.println(
                 " " + adminServerIpAddress.getText()
                + " " + adminServerPort.getText());
        } else {
            System.out.println("Cancelled");
        }
        panel=null;
        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) 
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(
                                 // "javax.swing.plaf.metal.MetalLookAndFeel");
                                 // "com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                                //UIManager.getCrossPlatformLookAndFeelClassName());
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                new Client();
            }
        });		
	}
}
