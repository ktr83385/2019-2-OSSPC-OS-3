package com.ok.ai;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class IDFrame extends JFrame{

	JTextField tf;
	
	IDFrame(final int score) {
		
		super("ID 입력");
		JLabel lb = new JLabel("ID : ",Label.RIGHT);
		tf = new JTextField(10);
		JButton jb =new JButton("확인");
		jb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(tf.getText().equals(""))tf.setText("user");
					String str=tf.getText();
					ScoreFrame sf = new ScoreFrame(str,score);
					setVisible(false);
				}
		});
		
		add(lb);
		add(tf);
		add(jb);
		
		this.getContentPane().setBackground(Color.lightGray);
		setBounds(510, 150, 300, 200);
		setLayout(new FlowLayout());
		setVisible(true);
		this.pack();
	}
}

