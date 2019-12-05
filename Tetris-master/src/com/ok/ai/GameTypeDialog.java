package com.ok.ai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameTypeDialog implements ActionListener
{

	public static int showDialog(JFrame frame, int def)
	{
		return new GameTypeDialog(frame, def).choice;
	}
	
	public int choice;
	
	private static final String okString = "OK";
	private static final String cancelString = "Cancel";
	
	private JDialog dialog;
	
	private ButtonGroup group;
	private JRadioButton marathonButton;
	
	private JButton okButton;
	private JButton cancelButton;
	
	private GameTypeDialog(JFrame frame, int def)
	{
		dialog = new JDialog(frame, "New Game", true);
		Container pane = dialog.getContentPane();
		pane.setLayout(null);
		
		group = new ButtonGroup();
		
		JRadioButton button;
		
		button = new JRadioButton("NEW GAME");
		button.setSize(button.getPreferredSize());
		button.setLocation(40, 20);
		button.addActionListener(this);
		button.setVisible(true);
		group.add(button);
		pane.add(button);
		marathonButton = button;

		choice = def;
		
		okButton = new JButton(okString);
		okButton.setSize(okButton.getPreferredSize());
		okButton.setLocation(95, 145);
		okButton.addActionListener(this);
		okButton.setVisible(true);
		dialog.getRootPane().setDefaultButton(okButton);
		group.add(okButton);
		pane.add(okButton);
		
		cancelButton = new JButton(cancelString);
		cancelButton.setSize(cancelButton.getPreferredSize());
		cancelButton.setLocation(160, 145);
		cancelButton.addActionListener(this);
		cancelButton.setVisible(true);
		group.add(cancelButton);
		pane.add(cancelButton);

		dialog.setResizable(false);
		dialog.setSize(340, 220);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();
		
		if (source == marathonButton)
			choice = TetrisRenderer.MARATHON;

		if (source == okButton)
			dialog.setVisible(false);
		if (source == cancelButton)
		{
			choice = 0;
			dialog.setVisible(false);
		}
	}

}
