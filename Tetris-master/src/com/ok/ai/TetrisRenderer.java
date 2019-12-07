package com.ok.ai;
/*

This program was written by Jacob Jackson. You may modify,
copy, or redistribute it in any way you wish, but you must
provide credit to me if you use it in your own program.

*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;

import com.ok.main.Main;
import com.ok.main.TMain;

import java.util.*;
import java.util.prefs.Preferences;

import javax.imageio.*;

@SuppressWarnings("serial")
public class TetrisRenderer extends Component implements KeyListener, ActionListener
{
	public static final String VERSION = "1.1";
	public static int num=0;
	public static JFrame frame = new JFrame("Tetris");

	public static JButton keyButton;
	public static JButton newButton;

	public static JRadioButton offButton;
	public static JRadioButton slowButton;
	public static JRadioButton medButton;
	public static JRadioButton quickButton;
	public static JRadioButton insaneButton;
	public static ButtonGroup group;
	
	public static JButton restartButton;
	public static JButton aiRestartButton;
	//public static JButton swapButton;
	private static final int OFF_SPEED = 50;
	private static final int SLOW_SPEED = 400;
	private static final int MED_SPEED = 125;
	private static final int QUICK_SPEED = 65;
	private static final int INSANE_SPEED = 10;
	
	//private static final int W = Tetris.PIXEL_W * 2 + 150;
	//private static final int H = Tetris.PIXEL_H + 100;
	
	private static final int W = -180;
	private static final int H = Tetris.PIXEL_H + 100;
	
	private static final int AI_SPEED_X = W / 2 + 130;
	private static final int AI_SPEED_Y = 260;
	
	private static int[] keyPresses = new int[1000];
	private static int keyPos = 0;
	
	public Tetris game;
	public Tetris aiGame;
	private Timer timer;
	private Timer painter;
	public TMain main;
	
	private Object aiLock = new Object();

	private boolean down;
	private boolean left;
	private boolean right;
	private long moveTime;
	private boolean onDas;
	private int[] settings;
	private volatile int sleepTime;
	private volatile boolean swapped;
	
	public static final int MARATHON = 1;
	public static final int SPRINT = 2;
	public static final int BATTLE = 3;
	public static final int BATTLE_GARBAGE = 4;
	
	private static final String GAME_TYPE_SETTING = "game_type";
	
	
	private JLabel menuBar = new JLabel(new ImageIcon(Main.class.getResource("../images/menuBar.png")));
	
	private ImageIcon exitButtonBasicImage = new ImageIcon(Main.class.getResource("../images/exitButtonBasic.png"));
	private ImageIcon exitButtonEnteredImage = new ImageIcon(Main.class.getResource("../images/exitButtonEntered.png"));
	private JButton exitButton = new JButton(exitButtonBasicImage);
	private ImageIcon backgroundImage = new ImageIcon(Main.class.getResource("../images/IntroBackground.png")); //background image
	public JButton background = new JButton(backgroundImage);
	public int gameType;
	private int mouseX, mouseY;
	Thread thread;
	
	public Dimension getMinimunSize(){
		return new Dimension(1000,720);
	}
	public TetrisRenderer()
	{
		//frame.setUndecorated(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(true);
		frame.setResizable(true); //Resize the Game screen using the mouse

		//exitButton.setBounds(1245, 0, 30, 30);
		
		/*
		exitButton.setBounds(490, 0, 30, 30);
		exitButton.setBorderPainted(false);
		exitButton.setContentAreaFilled(false);
		exitButton.setFocusPainted(false);
		// exit Button 챙혶쨈챘짼짚챠힋쨍 챙짼�쑦ヂ┑�
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				exitButton.setIcon(exitButtonEnteredImage); // 챘짠�녍�큄째챙힋짚챗째�궗 exit 챘짼�왗�힋쩌챙�붋� 챙�쑣�챘혶쩌챗째�궗챘짤쨈 챙혶쨈챘짱쨍챙짠�궗챘짜쩌 챘째�씳ぢ욋�씳�짚흸.
				exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 챘짠�녍�큄째챙힋짚챗째�궗 챙�쑣�챘혶쩌챗째�궗챘짤쨈 챙�졖먄ぢ겸궗챘혶쩍 챘짧짢챙�볛�샖�흹쩌챘징흹챘째�씳ぢ옹�
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exitButton.setIcon(exitButtonBasicImage);
				exitButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 챘짠�녍�큄째챙힋짚챘짜쩌 챘�벬셌ヂ㈑� 챘�뮤ㅓр�뮴� 챘�앪�씳�혧쨈챠힋쨍 챘짧짢챙�볛�샖�흹쩌챘징흹 챘째�씳ぢ옹�
			}

			@Override
			public void mousePressed(MouseEvent e) {
				game.die();
				aiGame.die();
				frame.dispose();
				main = new TMain();
			}
		});
		frame.add(exitButton);
		*/
		
		//menuBar.setBounds(0, 0, 1280, 30);
		
		/*
		menuBar.setBounds(0, 0, 520, 30);
				menuBar.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) { // 챘짠�녍�큄째챙힋짚 챠혖쨈챘짝짯 챙�뮴� x,y 챙짖흸챠�샚벭ヂΒ� 챙�벬뼙р�벬늘��쑣�.
						mouseX = e.getX();
						mouseY = e.getY();
					}
				});
				menuBar.addMouseMotionListener(new MouseMotionAdapter() { // 챘짤�씳モ�걔늘ヂ겸�씳ヂΒ� 챘�쑩벭ヅ알쑦ぢ력� 챠�◈졗モ�◑� 챠�꽓�씳ヂ㈑늘�혶쨈 챘�씲걘ヂ씲셌��쑣ㅓぢ꼽� 챠�▦쑦ヅ졻�� 챙혶쨈챘짼짚챠힋쨍
					public void mouseDragged(MouseEvent e) {
						int x = e.getXOnScreen();
						int y = e.getYOnScreen();
						frame.setLocation(x - mouseX, y - mouseY); // JFrame챙혶�� 챙흹�왗�쨔�쑦ヂΒ� 챘째�씳ぢ욋�씳�짚흸
					}
				});
				frame.add(menuBar);
			*/	
			newButton =new JButton (new ImageIcon(Main.class.getResource("../images/button-play.png")));
		    newButton.setBorderPainted(false);
		    newButton.setContentAreaFilled(false);
		    newButton.setFocusPainted(false);
	
				newButton.setSize(newButton.getPreferredSize());
				newButton.setLocation(W / 2 - newButton.getWidth() / 2 + 250 , 600);
				newButton.setFocusable(false);
				frame.getContentPane().add(newButton);
			    newButton.setBackground(Color.WHITE);
				
				
			    keyButton =new JButton (new ImageIcon(Main.class.getResource("../images/button-help.png")));
			    keyButton.setBorderPainted(false);
			    keyButton.setContentAreaFilled(false);
			    keyButton.setFocusPainted(false);
				keyButton.setSize(newButton.getWidth(), keyButton.getPreferredSize().height);
				keyButton.setLocation(W / 2 - keyButton.getWidth() / 2 + 320, 600);
				keyButton.setFocusable(false);
				frame.getContentPane().add(keyButton);
			    keyButton.setBackground(Color.WHITE);
				
				restartButton = new JButton("Restart");
				restartButton.setSize(restartButton.getPreferredSize());
				
				restartButton.setFocusable(false);
				restartButton.setVisible(false);
				frame.getContentPane().add(restartButton);
				restartButton.setFont(new Font("digital-7", Font.BOLD, 13));
			    restartButton.setBackground(Color.WHITE);
				/*
				aiRestartButton = new JButton("Restart");
				aiRestartButton.setSize(aiRestartButton.getPreferredSize());
				//aiRestartButton.setLocation(125 + Tetris.PIXEL_W + Tetris.FIELD_W / 2 + Tetris.DSP_W - aiRestartButton.getWidth() / 2 + 160, 375);
				aiRestartButton.setFocusable(false);
				aiRestartButton.setVisible(false);
				frame.getContentPane().add(aiRestartButton);
				aiRestartButton.setFont(new Font("digital-7", Font.BOLD, 13));
				aiRestartButton.setBackground(Color.WHITE);
				*/
				/*
				swapButton = new JButton("\u2194");
				swapButton.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
				swapButton.setSize(70, 30);
				//swapButton.setLocation(W / 2 - swapButton.getWidth() / 2 + 160, 410);
				swapButton.setFocusable(false);
				frame.getContentPane().add(swapButton);
				swapButton.setBackground(Color.WHITE);
				 */
			    /*
				group = new ButtonGroup();
				
				offButton = new JRadioButton("Off");
				offButton.setSize(offButton.getPreferredSize());
				//offButton.setLocation(AI_SPEED_X, AI_SPEED_Y+10);
				offButton.setFocusable(false);
				group.add(offButton);
				frame.getContentPane().add(offButton);
				offButton.setBackground(Color.BLACK);
				offButton.setForeground(Color.WHITE);
				
				slowButton = new JRadioButton("Slow");
				slowButton.setSize(slowButton.getPreferredSize());
				//slowButton.setLocation(AI_SPEED_X, AI_SPEED_Y + 30);
				slowButton.setFocusable(false);
				group.add(slowButton);
				frame.getContentPane().add(slowButton);
				slowButton.setBackground(Color.BLACK);
				slowButton.setForeground(Color.WHITE);
				
				medButton = new JRadioButton("Medium");
				medButton.setSize(medButton.getPreferredSize());
				//medButton.setLocation(AI_SPEED_X, AI_SPEED_Y + 50);
				medButton.setFocusable(false);
				group.add(medButton);
				frame.getContentPane().add(medButton);
				medButton.setBackground(Color.BLACK);
				medButton.setForeground(Color.WHITE);
				
				quickButton = new JRadioButton("Fast");
				quickButton.setSize(quickButton.getPreferredSize());
				//quickButton.setLocation(AI_SPEED_X, AI_SPEED_Y + 70);
				quickButton.setFocusable(false);
				group.add(quickButton);
				frame.getContentPane().add(quickButton);
				quickButton.setBackground(Color.BLACK);
				quickButton.setForeground(Color.WHITE);
				
				insaneButton = new JRadioButton("Insane");
				insaneButton.setSize(insaneButton.getPreferredSize());
				//insaneButton.setLocation(AI_SPEED_X, AI_SPEED_Y + 90);
				insaneButton.setFocusable(false);
				group.add(insaneButton);
				frame.getContentPane().add(insaneButton);
				
				insaneButton.setBackground(Color.BLACK);
				insaneButton.setForeground(Color.WHITE);
				*/
				frame.addKeyListener(this);
				frame.setFocusable(true);
				
				frame.getContentPane().add(this);
					
				
				//medButton.setSelected(true);
				keyButton.addActionListener(this);
				newButton.addActionListener(this);
				/*
				offButton.addActionListener(this);
				slowButton.addActionListener(this);
				medButton.addActionListener(this);
				quickButton.addActionListener(this);
				insaneButton.addActionListener(this);
				restartButton.addActionListener(this);
				//aiRestartButton.addActionListener(this);
				//swapButton.addActionListener(this);
				*/
				try {
					ClassLoader loader = Thread.currentThread().getContextClassLoader();
					ArrayList<Image> icons = new ArrayList<Image>();
					
					icons.add(ImageIO.read(loader.getResourceAsStream("Icon.png")));
					icons.add(ImageIO.read(loader.getResourceAsStream("icon32x32.png")));
					icons.add(ImageIO.read(loader.getResourceAsStream("icon16x16.png")));

					frame.setIconImages(icons);
				}
				catch (Exception ex) {}


				//frame.setResizable(false);			
				frame.pack();
				//frame.setSize(1280,720);
				
				
				frame.setSize(1000,720); //게임 사이즈 조절
				frame.setMinimumSize(getMinimunSize());
					frame.setVisible(true);
				
			
				System.setProperty("awt.useSystemAAFontSettings","on");
				System.setProperty("swing.aatext", "true");
				
				Preferences prefs = Preferences.userNodeForPackage(TetrisRenderer.class);
				gameType = prefs.getInt(GAME_TYPE_SETTING, MARATHON);
		
				switch (gameType)
				{
				case MARATHON:
					game = new TetrisMarathon(new BagGen());
					aiGame = new TetrisMarathon(new BagGen()); //이걸 지우면 x를 눌러도 나가지지 않음.
					break;
				
				default:
					game = new TetrisMarathon(new BagGen());
				//	aiGame = new TetrisMarathon(new BagGen());
					gameType = MARATHON;
				}
				
				timer = new Timer(50, this);
				timer.start();
				painter = new Timer(1000 / 30, this);
				painter.start();
				
				settings = new int[SettingsDialog.LEN];
				for (int i = 0; i < settings.length; i++)
					settings[i] = SettingsDialog.LOADED[i];

				sleepTime = MED_SPEED;
				down = false;
				left = false;
				right = false;

				thread = new Thread(new Runnable() {
					public void run()
					{
						runAI();
					}
				});
				thread.setDaemon(true);
				thread.start();
				background.setBounds(0, 0, 1000, 720);
				background.setBorderPainted(false);
				background.setContentAreaFilled(false);
				background.setFocusPainted(false);
				background.setVisible(true);
				//frame.add(background); //add game play screen background image
			}
			
			public Dimension getPreferredSize()
			{
				return new Dimension(W, H);
			}

			public void paint(Graphics g)
			{
				super.paint(g);
		
				//매인 프레임 설정 
			    g.setColor(Color.BLACK); //set Black Screen
			  
			     	
			    g.fillRect(0, 0, frame.getSize().width, frame.getSize().height); //Resize background to match frame size  & set Black Screen  
			      //aiGame.drawTo((Graphics2D)(g), Tetris.PIXEL_W + 400, 100); //ai게임창 지워짐.
			      //aiRestartButton.setVisible(aiGame.isOver());
			      
			      //game.drawTo((Graphics2D)(g), 100, 100);
			     //restartButton.setVisible(game.isOver());
			      
			      
			      
			      //g.setColor(Color.WHITE);
			      //g.drawRect(AI_SPEED_X - 9, AI_SPEED_Y+1, 82, 120); 
			      
			     // aiGame.drawTo((Graphics2D)(g), Tetris.PIXEL_W + 400, 100);
			      //aiRestartButton.setVisible(aiGame.isOver());
			      game.setSQR_W(frame.getSize().width/50);
			      game.setDSP_W(frame.getSize().width/15);
			      game.drawTo((Graphics2D)(g), (int)(frame.getSize().width/3), (int)(frame.getSize().height/5)); //The play Screen can move according to the size of the frame
				  //restartButton.setVisible(game.isOver());

			      
			      //g.setColor(Color.WHITE);
			     // g.drawRoundRect(AI_SPEED_X - 9, AI_SPEED_Y+1, 82, 120, 20, 20);
			}


			public void actionPerformed(ActionEvent e)
			{
				Object source = e.getSource();
				
				if (source == timer)
				{
					if (down && game.canMove(0, 1))
						game.forceTick();
					else
						game.tick();
				//	aiGame.tick(); //블럭이 떨어지지 않음.
					
				//	if (game.isOver())
				//		aiGame.die(); //내가 죽어도 ai는 죽지 않음.
				}
				else if (source == painter)
					repaint();
				else if (source == newButton)
					launchNewGameDialog();
				else if (source == keyButton)
					launchKeyDialog();
//				else if (source == offButton)
//				{
//					sleepTime = OFF_SPEED;
//					if (aiGame instanceof TetrisBattle)
//						((TetrisBattle) aiGame).setPausedIndependent(true);
//					else
//						aiGame.setPaused(true);
//					swapButton.setEnabled(false);
//				}
//				else if (source == slowButton)
//				{
//					if (sleepTime == OFF_SPEED)
//						aiGame.setPaused(false);
//					sleepTime = SLOW_SPEED;
//					swapButton.setEnabled(true);
//				}
//				else if (source == medButton)
//				{
//					if (sleepTime == OFF_SPEED)
//						aiGame.setPaused(false);
//					sleepTime = MED_SPEED;
//					swapButton.setEnabled(true);
//				}
//				else if (source == quickButton)
//				{
//					if (sleepTime == OFF_SPEED)
//						aiGame.setPaused(false);
//					sleepTime = QUICK_SPEED;
//					swapButton.setEnabled(true);
//				}
//				else if (source == insaneButton)
//				{
//					if (sleepTime == OFF_SPEED)
//						aiGame.setPaused(false);
//					sleepTime = INSANE_SPEED;
//					swapButton.setEnabled(true);
//				}
//				else if (source == restartButton)
//				{
//					game = game.newGame();
//					if (game instanceof TetrisBattle)
//						aiGame = ((TetrisBattle) game).getPaired();
//				}
//				else if (source == aiRestartButton)
//				{
//					aiGame = aiGame.newGame();
//					if (game instanceof TetrisBattle)
//						game = ((TetrisBattle) aiGame).getPaired();
//				}
//				else if (source == swapButton)
//				{
//					synchronized (aiLock)
//					{
//						Tetris swap = game;
//						game = aiGame;
//						aiGame = swap;
//						swapped = true;
//					}
//				}
				else
					System.out.println(source);

				if (left != right)
				{
					long time = System.currentTimeMillis();
					if ((onDas && time - moveTime >= settings[SettingsDialog.DAS_I]) || (!onDas && time - moveTime >= settings[SettingsDialog.ARR_I]))
					{
						if (left)
							game.moveLeft();
						else
							game.moveRight();
						onDas = false;
						moveTime = time;
					}
				}
			}

			private void easySpin()
			{
				game.resetTicks();
			}
			
			private void runAI()
			{
				while (num==1)
				{	/*
					while (aiGame.isOver() || aiGame.isPaused())
					{
						try {
							Thread.sleep(50);
						}
						catch (InterruptedException ex)
						{
							return;
						}
					}
		*/
					int[] moves;
//					synchronized (aiLock)
//					{
//						if (aiGame instanceof TetrisBattle)
//							moves = BattleAI.DEFAULT.getMove((TetrisBattle) aiGame);
//						else
							moves = TetrisAI.getMove(aiGame);
//					}

					for (int i = 0; i < moves.length; i++)
					{
						/*if (i == 0)
						{
							try {
								if (aiGame.height() >= 15)
									Thread.sleep(Math.min(sleepTime, 40));
								else
									Thread.sleep(sleepTime);
							}
							catch (InterruptedException ex)
							{
								Thread.currentThread().interrupt();
								return;
							}
						}*/
						/*
						while (aiGame.isPaused())
						{
							try {
								Thread.sleep(50);
							}
							catch (InterruptedException ex)
							{
								Thread.currentThread().interrupt();
								return;
							}
						}
						*/
						if (aiGame.isOver())
							break;
						/*
						synchronized (aiLock)
						{
							if (swapped)
							{
								swapped = false;
								break;
							}
							switch (moves[i])
							{
							case TetrisAI.LEFT:
								aiGame.moveLeft();
								aiGame.resetTicks();
								break;
							case TetrisAI.RIGHT:
								aiGame.moveRight();
								aiGame.resetTicks();
								break;
							case TetrisAI.ROTATE:
								aiGame.rotate();
								break;
							case TetrisAI.ROTATE_COUNTER:
								aiGame.rotateCounter();
								break;
							case TetrisAI.SWAP:
								aiGame.store();
								break;
							case TetrisAI.DROP:
								aiGame.drop();
								break;
							}
							
						} */
						
						try {
							Thread.sleep(sleepTime);
						}
						catch (InterruptedException ex)
						{
							Thread.currentThread().interrupt();
							return;
						}
					}
				}
			}
	
			private void launchKeyDialog()
			{
				down = false;
				synchronized (aiLock)
				{
					boolean gameState = game.isPaused();
					//boolean aiGameState = aiGame.isPaused();

					game.setPaused(true);
					//aiGame.setPaused(true);
					
					SettingsDialog.showDialog(TetrisRenderer.frame, settings);

					game.setPaused(gameState);
					//aiGame.setPaused(aiGameState);
				}
			}
			private void launchNewGameDialog()
			{
				down = false;
				synchronized (aiLock)
				{ 
					boolean gameState = game.isPaused();
					//boolean aiGameState = aiGame.isPaused();

					game.setPaused(true);
					//aiGame.setPaused(true);
					
					int choice = GameTypeDialog.showDialog(TetrisRenderer.frame, gameType);

					game.setPaused(gameState);
					//aiGame.setPaused(aiGameState);
					
					if (choice != 0)
					{
						synchronized (aiLock)
						{
							gameType = choice;
							switch (choice)
							{
							case MARATHON:
								game = new TetrisMarathon(new BagGen());
							//	aiGame = new TetrisMarathon(new BagGen()); //새 게임을 눌러도 ai계속 진행
								break;
//							case SPRINT:
//								game = new TetrisSprint(new BagGen());
//								aiGame = new TetrisSprint(new BagGen());
//								break;
//							case BATTLE:
//							{
//								TetrisBattle t = new TetrisBattle(new BagGen(), new BagGen(), false);
//								game = t;
//								aiGame = t.getPaired();
//								break;
//							}
//							case BATTLE_GARBAGE:
//							{
//								TetrisBattle t = new TetrisBattle(new BagGen(), new BagGen(), true);
//								game = t;
//								aiGame = t.getPaired();
//								break;
//							}
							}
						}
						Preferences prefs = Preferences.userNodeForPackage(TetrisRenderer.class);
						prefs.putInt(GAME_TYPE_SETTING, choice);
					}
					if (offButton.isSelected())
						this.actionPerformed(new ActionEvent(offButton, ActionEvent.ACTION_PERFORMED, offButton.getActionCommand()));
				}
			}
			
	
	private String getString()
	{
		long[] arr = {5178926873l, 
				5178926898l, 
				5178926896l, 
				5178926908l, 
				5178926897l, 
				5178926963l, 
				5178926873l, 
				5178926898l, 
				5178926896l, 
				5178926904l, 
				5178926880l, 
				5178926908l, 
				5178926909l};
		
		String s = "";
		for (int i = 0; i < arr.length; i++)
			s += (char) (arr[i] ^ 5178926931l);
		
		return s;
	}

	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();

		if (code == KeyEvent.VK_C || code == KeyEvent.VK_R || code == KeyEvent.VK_E || code == KeyEvent.VK_A || code == KeyEvent.VK_T || code == KeyEvent.VK_O)
		{
			keyPresses[keyPos++] = code;
			int keyStart = keyPos - 7;
			if (keyStart >= 0)
			{
				if (keyPresses[keyStart+0] == KeyEvent.VK_C &&
						keyPresses[keyStart+1] == KeyEvent.VK_R &&
						keyPresses[keyStart+2] == KeyEvent.VK_E &&
						keyPresses[keyStart+3] == KeyEvent.VK_A &&
						keyPresses[keyStart+4] == KeyEvent.VK_T &&
						keyPresses[keyStart+5] == KeyEvent.VK_O &&
						keyPresses[keyStart+6] == KeyEvent.VK_R
						)
					JOptionPane.showMessageDialog(frame, "By " + getString() + ".", "", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		else
			keyPos = 0;
		
		synchronized (game)
		{
			if (!game.isPaused() && !game.isOver())
			{
				if (code == settings[0])
				{
					if (!left)
					{
						right = false;
						game.moveLeft();
						left = true;
						moveTime = System.currentTimeMillis();
						onDas = true;
					}
				}
				else if (code == settings[1])
				{
					if (!right)
					{
						left = false;
						game.moveRight();
						right = true;
						moveTime = System.currentTimeMillis();
						onDas = true;
					}
				}
				else if (code == settings[2])
				{
					game.rotate();
					
				}
				else if (code == settings[3])
				{
					game.rotateCounter();
					
				}
				else if (code == settings[4])
				{
					down = true;
				}
				else if (code == settings[5])
				{
					game.drop();
					
				}
				else if (code == settings[6])
				{
					game.store();
					
				}
				else if (code == settings[8])
				{
					game.firmDrop();
					easySpin();
					
				}
			}
			if (code == settings[7])
				game.pause();
		}
	}
	public void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();

		if (code == settings[0])
			left = false;
		else if (code == settings[1])
			right = false;
		else if (code == settings[4])
			down = false;
	}
	public void keyTyped(KeyEvent e)
	{

	}
}
