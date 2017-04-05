package com.myftpserver.admin;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginBox implements ActionListener
{	
    JFrame frame=new JFrame("Connect to Admin. server");
    JButton okButton=new JButton("Ok");
    JButton cancelButton=new JButton("Cancel");
	public LoginBox()
	{
		JTextField adminServerIpAddress = new JTextField();
        JTextField adminServerPort = new JTextField();
        JPanel panel = new JPanel(new GridLayout(3, 2));
        Border padding = BorderFactory.createEmptyBorder(10,10,10,10);
        panel.setBorder(padding);
        panel.add(new JLabel("Admin. Server Host name/IP Address:"));
        panel.add(adminServerIpAddress);
        panel.add(new JLabel("Admin. Server Port No.:"));
        panel.add(adminServerPort);
        panel.add(okButton);
        panel.add(cancelButton);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(okButton))
		{
			JOptionPane.showMessageDialog(frame, "Eggs are not supposed to be green.");
		}
		else
		{
			frame.dispose();
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
                new LoginBox();
            }
        });		
	}
}
