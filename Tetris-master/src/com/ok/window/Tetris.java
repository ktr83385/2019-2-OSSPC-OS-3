package com.ok.window;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.ok.classes.Block;
import com.ok.main.Main;
import com.ok.main.TMain;
import com.ok.network.GameClient;
import com.ok.network.GameServer;

public class Tetris extends JFrame implements ActionListener {

	// 硫붾돱諛� 媛앹껜 �깮�꽦
	private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("../images/menuBar.png")));

	// 硫붾돱 諛� �쐞�쓽 exit button 愿��젴 媛앹껜 �깮�꽦
	private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("../images/exitButtonBasic.png"));
	private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("../images/exitButtonEntered.png"));
	private JButton exitButton = new JButton(exitButtonBasicImage);

	// 留덉슦�뒪 �씠踰ㅽ듃�뿉 �솢�슜�븯湲� �쐞�븳 留덉슦�뒪 x, y 醫뚰몴
	private int mouseX, mouseY;

	private static final long serialVersionUID = 1L;
	private GameServer server;
	private GameClient client;
	private TetrisBoard board;
	private JMenuItem itemServerStart = new JMenuItem("�꽌踰� 留뚮뱾湲�");
	private JMenuItem itemClientStart = new JMenuItem("�겢�씪�씠�뼵�듃 �젒�냽�븯湲�");

	private boolean isNetwork;
	private boolean isServer;

	public int isserver = 0;
	public int mode = 0;

	public Tetris(int mode, int[] key_setting) {
		setUndecorated(true); // 湲곕낯 硫붾돱諛붽� 蹂댁씠吏� �븡�쓬. -> �깉濡쒖슫 menuBar瑜� �꽔湲� �쐞�븳 �옉�뾽
		setSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT + 30);
		setResizable(false); // �솕硫� �겕湲� �닔�젙 遺덇��뒫
		setLocationRelativeTo(null); // �솕硫� �젙以묒븰�뿉 �쑉寃� �븿.
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		setBackground(Color.WHITE);
		// setBackground(new Color(0, 0, 0, 0));
		setLayout(null); // �솕硫댁뿉 諛곗튂�릺�뒗 踰꾪듉�씠�굹 label�쓣 洹� �옄由� 洹몃�濡� �뱾�뼱媛�寃� �븿.
		JMenuBar mnBar = new JMenuBar();
		JMenu mnGame = new JMenu("寃뚯엫�븯湲�");
		this.mode = mode;
		if (mode == 1)
			board = new TetrisBoard(this, client, true, key_setting);
		else
			board = new TetrisBoard(this, client, false, key_setting);

		// Menu bar exit 踰꾪듉 愿��젴 泥섎━
		exitButton.setBounds(1245, 0, 30, 30);
		exitButton.setBorderPainted(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setFocusPainted(false);
		// exit Button �씠踰ㅽ듃 泥섎━
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				exitButton.setIcon(exitButtonEnteredImage); // 留덉슦�뒪媛� exit 踰꾪듉�뿉 �삱�씪媛�硫� �씠誘몄�瑜� 諛붽퓭以�.
				exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 留덉슦�뒪媛� �삱�씪媛�硫� �넀媛��씫 紐⑥뼇�쑝濡쒕컮轅�
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exitButton.setIcon(exitButtonBasicImage);
				exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 留덉슦�뒪瑜� �뼹硫� �떎�떆 �뵒�뤃�듃 紐⑥뼇�쑝濡� 諛붽퓞
			}

			@Override
			public void mousePressed(MouseEvent e) {
				new TMain(key_setting);
				dispose();
			}
		});
		add(exitButton);

		// 硫붾돱諛� �씠踰ㅽ듃
		menuBar.setBounds(0, 0, 1280, 30);
		menuBar.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) { // 留덉슦�뒪 �겢由� �떆 x,y 醫뚰몴瑜� �뼸�뼱�샂.
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
		menuBar.addMouseMotionListener(new MouseMotionAdapter() { // 硫붾돱諛붾�� �뱶�옒洹� �븷�븣 �솕硫댁씠 �뵲�씪�삤寃� �븯�뒗 �씠踰ㅽ듃
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				setLocation(x - mouseX, y - mouseY); // JFrame�쓽 �쐞移섎�� 諛붽퓭以�
			}
		});
		add(menuBar);

		// mnGame.add(SingleStart);
		if (this.mode != 1) {
			mnGame.add(itemServerStart);
			mnGame.add(itemClientStart);
			mnBar.add(mnGame);

			this.setJMenuBar(mnBar);
		}
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getContentPane().add(board);

		if (this.mode == 1) {
			board.setClient(client);
			board.getBtnStart().setEnabled(true);
			board.clearMessage();
			board.requestFocus();
		}

		// SingleStart.addActionListener(this);
		itemServerStart.addActionListener(this);
		itemClientStart.addActionListener(this);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (client != null) {
					System.out.println("123");
					if (isNetwork()) {
						client.closeNetwork(isServer);
					}
				} else {
					new TMain(key_setting);
					dispose();
				}

			}

		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String ip = null;
		int port = 0;
		String nickName = null;
		if (e.getSource() == itemServerStart) {

			String sp = JOptionPane.showInputDialog("�룷�듃 踰덊샇瑜� �엯�젰�븯�꽭�슂", "9500");
			if (sp != null && !sp.equals(""))
				port = Integer.parseInt(sp);
			nickName = JOptionPane.showInputDialog("�땳�꽕�엫�쓣 �엯�젰�븯�꽭�슂", "default");

			if (port != 0) {
				if (server == null)
					server = new GameServer(port);
				server.startServer();
				isserver = 1;
				try {
					ip = InetAddress.getLocalHost().getHostAddress();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				}
				if (ip != null) {
					client = new GameClient(this, ip, port, nickName);
					if (client.start()) {
						itemServerStart.setEnabled(false);
						itemClientStart.setEnabled(false);
						board.setClient(client);
						board.getBtnStart().setEnabled(true);
						board.startNetworking(ip, port, nickName);
						isNetwork = true;
						isServer = true;
					}
				}
			}
		} else if (e.getSource() == itemClientStart) {
			try {
				ip = InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}

			ip = JOptionPane.showInputDialog("IP 二쇱냼瑜� �엯�젰�븯�꽭�슂.", ip);
			String sp = JOptionPane.showInputDialog("port 踰덊샇瑜� �엯�젰�븯�꽭�슂", "9500");
			if (sp != null && !sp.equals(""))
				port = Integer.parseInt(sp);
			nickName = JOptionPane.showInputDialog("�땳�꽕�엫�쓣 �엯�젰�븯�꽭�슂", "default");

			if (ip != null) {
				client = new GameClient(this, ip, port, nickName);
				isserver = 0;
				if (client.start()) {
					itemServerStart.setEnabled(false);
					itemClientStart.setEnabled(false);
					board.setClient(client);
					board.startNetworking(ip, port, nickName);
					isNetwork = true;
				}
			}
		}

	}

	public void closeNetwork() {
		isNetwork = false;
		client = null;
		itemServerStart.setEnabled(true);
		itemClientStart.setEnabled(true);
		board.setPlay(false);
		board.setClient(null);
	}

	public JMenuItem getItemServerStart() {
		return itemServerStart;
	}

	public JMenuItem getItemClientStart() {
		return itemClientStart;
	}

	public TetrisBoard getBoard() {
		return board;
	}

	public void gameStart(int speed) {
		board.gameStart(speed);
	}

	public boolean isNetwork() {
		return isNetwork;
	}

	public void setNetwork(boolean isNetwork) {
		this.isNetwork = isNetwork;
	}

	public void printSystemMessage(String msg) {
		board.printSystemMessage(msg);
	}

	public void printMessage(String msg) {
		board.printMessage(msg);
	}

	public void setmap(Block[] map) {
		board.enemy.map = map;
		board.enemy.fun();
	}

	public boolean isServer() {
		return isServer;
	}

	public void setServer(boolean isServer) {
		this.isServer = isServer;
	}

	public void changeSpeed(Integer speed) {
		board.changeSpeed(speed);
	}
}
