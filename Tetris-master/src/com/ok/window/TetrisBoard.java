package com.ok.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ok.classes.Block;
import com.ok.classes.TetrisBlock;
import com.ok.controller.TetrisController;
import com.ok.main.Main;
import com.ok.main.TMain;
import com.ok.network.GameClient;
import com.ok.shape.CenterUp;
import com.ok.shape.LeftTwoUp;
import com.ok.shape.LeftUp;
import com.ok.shape.Line;
import com.ok.shape.Nemo;
import com.ok.shape.RightTwoUp;
import com.ok.shape.RightUp;

public class TetrisBoard extends JPanel implements Runnable, KeyListener, MouseListener, ActionListener {

	// Start button 관련 객체 생성
	private Image startImage = new ImageIcon(Main.class.getResource("../images/StartBasic.png")).getImage();
	private ImageIcon startBasicImage = new ImageIcon(Main.class.getResource("../images/StartBasic.png"));
	private ImageIcon startEnteredImage = new ImageIcon(Main.class.getResource("../images/StartEntered.png"));
	private JButton startBtn = new JButton(startBasicImage);

	// Exit button 관련 객체 생성
	private Image exitImage = new ImageIcon(Main.class.getResource("../images/SmallExitBasic.png")).getImage();
	private ImageIcon exitBasicImage = new ImageIcon(Main.class.getResource("../images/SmallExitBasic.png"));
	private ImageIcon exitEnteredImage = new ImageIcon(Main.class.getResource("../images/SmallExitEntered.png"));
	private JButton exitBtn = new JButton(exitBasicImage);

	private static final long serialVersionUID = 1L; //직렬화에 사용되는 고유 아이디

	private Tetris tetris;
	private GameClient client; //네트워크
	public EnemyBoard enemy;

	public static final int BLOCK_SIZE = 20;
	public static final int BOARD_X = 120;
	public static final int BOARD_Y = 150;
	private int minX = 1, minY = 0, maxX = 10, maxY = 21, down = 50, up = 0;
	private final int MESSAGE_X = 2;
	private final int MESSAGE_WIDTH = BLOCK_SIZE * (7 + minX);
	private final int MESSAGE_HEIGHT = BLOCK_SIZE * (6 + minY);
	private final int PANEL_WIDTH = maxX * BLOCK_SIZE + MESSAGE_WIDTH + BOARD_X;
	private final int PANEL_HEIGHT = maxY * BLOCK_SIZE + MESSAGE_HEIGHT + BOARD_Y;

	private SystemMessageArea systemMsg;
	private MessageArea messageArea;
	private JButton btnStart = new JButton("Start");
	private JButton btnExit = new JButton("寃뚯엫 醫낅즺");
	private JCheckBox checkGhost = new JCheckBox("怨좎뒪�듃", true);
	private JCheckBox checkGrid = new JCheckBox("洹몃━�뱶", true);
	private Integer[] lv = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 };
	private JComboBox<Integer> comboSpeed = new JComboBox<Integer>(lv);

	private String ip;
	private int port;
	private String nickName;
	private Thread th;
	private ArrayList<Block> blockList;
	private ArrayList<TetrisBlock> nextBlocks;
	private TetrisBlock shap;
	private TetrisBlock ghost;
	private TetrisBlock hold;
	private Block[][] map;
	private TetrisController controller;
	private TetrisController controllerGhost;

	public boolean isSingle = false;
	private boolean isPlay = false;
	private boolean isHold = false;
	private boolean usingGhost = true;
	private boolean usingGrid = true;
	private boolean usingBlind = false;
	private int removeLineCount = 0;
	private int removeLineCombo = 0;
	private long start_blind_time = 0;
	private long end_blind_time = 0;

	public int key_set[] = new int[11];

	public TetrisBoard(Tetris tetris, GameClient _client, boolean isSingle, int[] key_setting) {
		this.tetris = tetris;
		this.client = _client;
		this.isSingle = isSingle;
		if (isSingle == false) // �겢�씪�씠�뼵�듃 紐⑤뱶
		{
			systemMsg = new SystemMessageArea(21, 330, 100, 240);
			messageArea = new MessageArea(this, 500, 460, 360, 130);
			this.add(messageArea); //컴포넌트 부착 -> 배열 뒤에 데이터 더하기
			this.add(systemMsg); //컴포넌트 부착

			messageArea.setVisible(true); //messageArea가 화면에 보여지게 함.
		} else {
			systemMsg = null;
			messageArea = null;
			this.add(comboSpeed);
		}
		
		this.enemy = new EnemyBoard();
		this.setBounds(0, 30, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT); //위치 0,30 너비 Main.SCREEN_WIDTH, 높이 Main.SCREEN_HEIGHT로 지정
		this.setBackground(Color.WHITE); 
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.setLayout(null); //레이아웃설정
		this.setFocusable(true); //키 이벤트의 포커스를 받을 수 있는 컴포넌트가 여러개있을 때 우선적으로 입력받기 위해 설정
		this.key_set = key_setting;
		

		// Start Button설정
		startBtn.setBounds(475, 600, 220, 100); //위치 너비 높이 설정
		startBtn.setBorderPainted(false); //버튼 외곽선 없애기
		startBtn.setContentAreaFilled(false); //버튼 내용 영역 채우기 안함
		startBtn.setFocusPainted(false); //버튼 선택되었을 때 생기는 테두리 사용안함
		startBtn.setFocusable(false); //이동시에 해당뷰가 포커스 되지 않고 다음으로 간다.
		// Start Button 마우스 리스너 등록
		startBtn.addMouseListener(new MouseAdapter() { //MousePressed()기능 사용하기 위해서 Adapter사용
			@Override
			public void mouseEntered(MouseEvent e) { //마우스가 해당 컴포넌트 영역 안으로 들어올 때 발생
				startBtn.setIcon(startEnteredImage); // 아이콘 설정
				startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // HAND_CURSOR로 커서 변환
			}

			@Override
			public void mouseExited(MouseEvent e) { //마우스가 해당 컴포넌트 영역 밖으로 나갈 때 발생
				startBtn.setIcon(startBasicImage);
				startBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // DEFAULT_CURSOR로 커서 변환
			}

			@Override
			public void mousePressed(MouseEvent e) { //마우스 버튼을 누를 때 발생
				// 寃뚯엫 �떆�옉 �씠踰ㅽ듃泥섎━ 遺�遺�
				if (client != null) {
					client.gameStart((int) comboSpeed.getSelectedItem());
				} else if (isSingle == true) {
					gameStart((int) comboSpeed.getSelectedItem());
				}
			}
		});
		add(startBtn);

		// Start 踰꾪듉 愿��젴 泥섎━
		exitBtn.setBounds(700, 600, 220, 100);
		exitBtn.setBorderPainted(false);
		exitBtn.setContentAreaFilled(false);
		exitBtn.setFocusPainted(false);
		exitBtn.setFocusable(false);
		// Start Button �씠踰ㅽ듃 泥섎━
		exitBtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				exitBtn.setIcon(exitEnteredImage); // 留덉슦�뒪媛� exit 踰꾪듉�뿉 �삱�씪媛�硫� �씠誘몄�瑜� 諛붽퓭以�.
				exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 留덉슦�뒪媛� �삱�씪媛�硫� �넀媛��씫 紐⑥뼇�쑝濡쒕컮轅�
			}

			@Override
			public void mouseExited(MouseEvent e) {
				exitBtn.setIcon(exitBasicImage);
				exitBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); // 留덉슦�뒪瑜� �뼹硫� �떎�떆 �뵒�뤃�듃 紐⑥뼇�쑝濡� 諛붽퓞
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// 寃뚯엫 醫낅즺 �씠踰ㅽ듃
				if (client != null) {
					if (tetris.isNetwork()) {
						client.closeNetwork(tetris.isServer());
					}
				} else {
					new TMain(key_set);
					tetris.dispose();
				}
			}
		});
		add(exitBtn);

		checkGhost.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 70, 85, 20);
		checkGhost.setBackground(Color.WHITE); 
		checkGhost.setForeground(Color.WHITE);
		checkGhost.setFont(new Font("留묒� 怨좊뵓", Font.BOLD, 13));
		checkGhost.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				usingGhost = checkGhost.isSelected();
				TetrisBoard.this.setRequestFocusEnabled(true);
				TetrisBoard.this.repaint();
			}
		});
		checkGrid.setBounds(PANEL_WIDTH - BLOCK_SIZE * 7 + 35, 50, 85, 20);
		checkGrid.setBackground(Color.WHITE); //배경색 설정
		checkGrid.setForeground(Color.WHITE); //글자색 설정
		checkGrid.setFont(new Font("留묒� 怨좊뵓", Font.BOLD, 13)); //폰트 설정
		checkGrid.addChangeListener(new ChangeListener() { //체크박스 상태가 변하면 처리할 리스너 추가
			@Override
			public void stateChanged(ChangeEvent arg0) {
				usingGrid = checkGrid.isSelected(); //체크박스가 체크 되었는지 여부를 리턴
				TetrisBoard.this.setRequestFocusEnabled(true);  //특정 셀에 포커스 설정되게 처리 
				TetrisBoard.this.repaint(); //이미지 그림 갱신
			}
		});
		comboSpeed.setBounds(PANEL_WIDTH - BLOCK_SIZE * 8, 50, 45, 20);
		enemy.setBounds(1000, 150, 202, 422);
		enemy.setVisible(true);

		this.add(btnStart);
		this.add(btnExit);
		this.add(checkGhost);
		this.add(checkGrid);
		this.add(enemy);
	}

	public void startNetworking(String ip, int port, String nickName) {
		this.ip = ip;
		this.port = port;
		this.nickName = nickName;
		this.repaint();
		enemy.repaint();
	}

	/**
	 * TODO : 野껊슣�뿫占쎈뻻占쎌삂 野껊슣�뿫占쎌뱽 占쎈뻻占쎌삂占쎈립占쎈뼄.
	 */

	public void gameStart(int speed) { //게임 시작
		comboSpeed.setSelectedItem(new Integer(speed)); //특정 내용의 스피드가 선택되도록
		if (th != null) { //쓰레드 != null
			try {
				isPlay = false;
				th.join(); //해당 쓰레드가 종료될 때까지 기다렸다가 다음으로 넘어감.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		map = new Block[maxY][maxX]; //21행 10열
		blockList = new ArrayList<Block>();
		nextBlocks = new ArrayList<TetrisBlock>();

		shap = getRandomTetrisBlock(); //랜덤으로 테트리스 블록 정하기
		ghost = getBlockClone(shap, true); //랜덤으로 정한 블록 shap의 유렁블록 ghost생성
		hold = null; 
		isHold = false;
		controller = new TetrisController(shap, maxX - 1, maxY - 1, map); //테트리스 블록을 조정하는 컨트롤러 설정
		/*
		 * @param shap : 움직일 테트리스 블럭
		 * @param maxX-1 : 블럭이 움직일 최대 GridX좌표
		 * @param maxY-1 : 블럭이 움직일 최대 GridY좌표
		 */
		controllerGhost = new TetrisController(ghost, maxX - 1, maxY - 1, map); //유령 블록을 조정하는 컨트롤러 설정
		/*
		 * @param ghost : 움직일 테트리스 블럭
		 * @param maxX-1 : 블럭이 움직일 최대 GridX좌표
		 * @param maxY-1 : 블럭이 움직일 최대 GridY좌표
		 */
		this.showGhost(); //ghost블럭이 shap의 움직임에 따라 보일 수 있도록 설정
		for (int i = 0; i < 5; i++) { 
			nextBlocks.add(getRandomTetrisBlock()); //랜덤블록 5개를 미리 생성하여 nextBlock에 저장
		}

		isPlay = true;
		th = new Thread(this); //쓰레드 생성
		th.start(); //운영체제에서 현재 인스턴스(쓰레드 th)의 상태를 running으로 변경하도록 한다.
	} //게임 시작

	/* TODO : 스윙 컴포넌트가 자신의 모양을 그리는 메서드
	* 컴포넌트가 그려져야 하는 시점마다 호출됨(크기나 위치가 변경되거나 컴포넌트가 가렸던 것이 사라지는 등
	* @param : Graphics 객체
	* 		       그래픽 컨텍스트 : 컴포넌트 그리기에 필요한 도구를 제공하는 객체
	* 		       색 지정, 도형 그리기, 클리핑, 이미지 그리기 등의 메소드 제공
	*/
	@Override
	protected void paintComponent(Graphics g) { //
		g.clearRect(0, 0, this.getWidth(), this.getHeight() + 1); //사각형 영역을 지우는 메소드 -> 캔버스의 전체 영역을 지움

		g.setColor(Color.WHITE); //객체를 WHITE 색상 설정
		g.fillRect(0, 0, 1280, 720); //객체를 하얀색으로 색칠된 직사각형 생성 -> 프레임 생성

		// g.fillRect(0, BOARD_Y, (maxX + minX + 13) * BLOCK_SIZE + 1 + 300, maxY *
		// BLOCK_SIZE + 1);

		Font font = g.getFont(); //객체의 글꼴을 font에 저장
		g.setFont(new Font("Serif", Font.BOLD, 13)); //객체의 폰트를 "Serif"로 굵은체, 사이즈 13 설정
		g.setFont(font); //객체의 폰트를 font로 변경
		if (isSingle == false) {
			g.setColor(Color.WHITE);
			g.drawString("ip : " + ip + "     port : " + port, 20, 65);

			g.drawString("�땳�꽕�엫 : " + nickName, 20, 85);
		} else { //single 모드
			g.setFont(new Font("Serif", Font.BOLD, 13));
			g.setColor(Color.WHITE);
			g.drawString("이건뭘까", PANEL_WIDTH - BLOCK_SIZE * 10, 65); //START 라는 문구 (BLOCK_SIZE*10, 65)에 생성
		}

		g.setColor(Color.WHITE);
		g.fillRect(BOARD_X + BLOCK_SIZE * minX, BOARD_Y, maxX * BLOCK_SIZE + 1, maxY * BLOCK_SIZE + 1);
		g.fillRect(BLOCK_SIZE * minX, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * 5, BLOCK_SIZE * 5);
		g.fillRect(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * 5,
				BLOCK_SIZE * 5);
		g.fillRect(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE + BLOCK_SIZE * 7,
				BLOCK_SIZE * 5, BLOCK_SIZE * 12);

		g.setColor(Color.WHITE);
		g.setFont(new Font(font.getFontName(), font.getStyle(), 20));//객체의 폰트를 font의 글꼴과, 스타일, 사이즈 20 설정
		g.drawString("HOLD", BLOCK_SIZE + 20, BOARD_Y + 7);
		g.drawString("NEXT", BOARD_X + BLOCK_SIZE + (maxX + 1) * BLOCK_SIZE + 1 + 25, BOARD_Y + 7);
		g.drawString("ENEMY", 1070, BOARD_Y - 10);
		g.drawString("PLAYER", 200, BOARD_Y - 10);
		g.setFont(font);

		if (usingGrid) { //CheckGrid가 체크되어있다면
			g.setColor(Color.WHITE);
			// 寃뚯엫 蹂대뱶�뙋 �뀒�몢由�
			g.drawRect(BOARD_X + BLOCK_SIZE * minX, BOARD_Y, BLOCK_SIZE * 10 + 3, BLOCK_SIZE * 21 + 3);
			// ���뱶 �뀒�몢由�
			g.drawRect(BLOCK_SIZE * minX, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * 5, BLOCK_SIZE * 5);
			// �꽖�뒪�듃 �뀒�몢由�
			g.drawRect(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * 5,
					BLOCK_SIZE * 5);
			// ��湲� �뀒�몢由�
			g.drawRect(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE * 8 - 10,
					BLOCK_SIZE * 5, BLOCK_SIZE * 14);
			// 怨좎뒪�듃, �뀒�몢由� �몴�떆�쓽 �뀒�몢由�
			g.drawRect(370, 45, 92, 50);
			g.setColor(Color.darkGray);
			for (int i = 1; i < maxY; i++) // 寃뚯엫 蹂대뱶�뙋 媛�濡� 洹몃━湲�
				g.drawLine(BOARD_X + BLOCK_SIZE * minX, BOARD_Y + BLOCK_SIZE * (i + minY),
						BOARD_X + (maxX + minX) * BLOCK_SIZE, BOARD_Y + BLOCK_SIZE * (i + minY));
			for (int i = 1; i < maxX; i++) // 寃뚯엫 蹂대뱶�뙋 �꽭濡� 洹몃━湲�
				g.drawLine(BOARD_X + BLOCK_SIZE * (i + minX), BOARD_Y + BLOCK_SIZE * minY,
						BOARD_X + BLOCK_SIZE * (i + minX), BOARD_Y + BLOCK_SIZE * (minY + maxY));
			for (int i = 1; i < 5; i++) // HOLD 媛�濡� 洹몃━湲�
				g.drawLine(BLOCK_SIZE * minX, BOARD_Y + BLOCK_SIZE * (i + 1), BLOCK_SIZE * (minX + 5) - 1,
						BOARD_Y + BLOCK_SIZE * (i + 1));
			for (int i = 1; i < 5; i++) // HOLD �꽭濡� 洹몃━湲�
				g.drawLine(BLOCK_SIZE * (minY + i + 1), BOARD_Y + BLOCK_SIZE, BLOCK_SIZE * (minY + i + 1),
						BOARD_Y + BLOCK_SIZE * (minY + 6) - 1);

			for (int i = 1; i < 5; i++) // NEXT 媛�濡� 洹몃━湲�
				g.drawLine(BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE * (i + 1),
						BOARD_X + BLOCK_SIZE * minX + (maxX + 1) * BLOCK_SIZE + BLOCK_SIZE * 5,
						BOARD_Y + BLOCK_SIZE * (i + 1));
			for (int i = 1; i < 5; i++) // NEXT �꽭濡� 洹몃━湲�
				g.drawLine(BOARD_X + BLOCK_SIZE * minX + (maxX + 1 + i) * BLOCK_SIZE + 1, BOARD_Y + BLOCK_SIZE,
						BOARD_X + BLOCK_SIZE * minX + BLOCK_SIZE + BLOCK_SIZE * (10 + i) + 1,
						BOARD_Y + BLOCK_SIZE * 6 - 1);
		}

		int x = 0, y = 0, newY = 0;
		if (hold != null) { //hold가 존재한다면
			x = 0;
			y = 0;
			newY = 3;
			x = hold.getPosX(); //hold의 x좌표 받아옴
			y = hold.getPosY(); //hold의 y좌표 받아옴
			hold.setPosX(-4 + minX); //hold의 x좌표를 -4+minX로 설정
			hold.setPosY(newY + minY); //hold의 y좌표를 3+minY로 설정
			hold.drawBlock(g); //hold의 drawBlock호출 col에 저장하여 drawColorBlock으로 블록을 그린다.
			hold.setPosX(x); //hold의 x좌표 원래대로 설정
			hold.setPosY(y); //hold의 y좌표 원래대로 설정
		}

		if (nextBlocks != null) { //nextBlocks가 존재한다면
			x = 0;
			y = 0;
			newY = 3;
			for (int i = 0; i < nextBlocks.size(); i++) {
				TetrisBlock block = nextBlocks.get(i); //nextBlocks을 하나씩 받아와 block에 대입
				x = block.getPosX(); //block의 x좌표를 받아옴
				y = block.getPosY(); //block의 y좌표를 받아옴
				block.setPosX(13 + minX); //block의 x좌표 13+minX로 설정
				block.setPosY(newY + minY); //block의 y좌표 3+minY로 설정
				if (newY == 3)
					newY = 6; //newY 6으로 변경
				block.drawBlock(g); //해당 block의 객체를 그린다.
				block.setPosX(x); //block의 x좌표 원래대로 설정
				block.setPosY(y); //block의 y좌표 원래대로 설정
				newY += 3; //newY 3씩 증가
			}
		}

		if (blockList != null) { //blockList가 존재한다면
			x = 0;
			y = 0;
			for (int i = 0; i < blockList.size(); i++) {
				Block block = blockList.get(i);
				x = block.getPosGridX(); //block의 posGridX(블럭 x좌표)를 x에 대입
				y = block.getPosGridY(); //block의 posGridY(블럭 y좌표)를 y에 대입
				block.setPosGridX(x + minX); //block의 posGridX좌표를 x+minX로 설정
				block.setPosGridY(y + minY); //block의 posGridY좌표를 x+minY로 설정
				block.drawColorBlock(g); //block을 그림
				block.setPosGridX(x); //block의 x좌표 원래대로 설정
				block.setPosGridY(y); //block의 y좌표 원래대로 설정
			}
		}

		if (ghost != null) { //유령조각이 존재한다면

			if (usingGhost) { //유령조각 사용여부
				x = 0;
				y = 0;
				x = ghost.getPosX(); 
				y = ghost.getPosY();
				ghost.setPosX(x + minX);
				ghost.setPosY(y + minY);
				ghost.drawBlock(g);
				ghost.setPosX(x);
				ghost.setPosY(y);
			}
		}

		if (shap != null) { 
			x = 0;
			y = 0;
			x = shap.getPosX();
			y = shap.getPosY();
			shap.setPosX(x + minX);
			shap.setPosY(y + minY);
			shap.drawBlock(g);
			shap.setPosX(x);
			shap.setPosY(y);
		}

		if (usingBlind) { 
			end_blind_time = System.currentTimeMillis();
			/* 시간을 구하기 위한 메소드
			 * 날짜와 관련한 시각 계산을 위해 사용(ms 단위)
			 * 시스템 시간을 참조
			 * 시각(시/분/초) 계산에 용이
			 * 계산한 시각을 end_bline_time에 대입
			 */
			g.setColor(new Color(0, 0, 0)); //객체 색상 검정 설정
			g.fillRect(minX * BLOCK_SIZE * 7, minY * BLOCK_SIZE + 50, maxX * BLOCK_SIZE + 1, maxY * BLOCK_SIZE + 1);
			if ((end_blind_time - start_blind_time) / 1000 > 2) //수행시간이 2초보다 오래 걸리면
				usingBlind = false; //usingBlind는 false로 변경됨 
			System.out.println("Time Check" + (end_blind_time - start_blind_time) / 1000); //수행시간 초 계산
		}

		enemy.repaint(); //enemy를 다시 그린다
	}

	@Override
	public void run() {
		int countMove = (21 - (int) comboSpeed.getSelectedItem()) * 5;
		int countDown = 0;
		int countUp = up; //up=0

		while (isPlay) { //play하는 동안
			try {
				Thread.sleep(10); //아무일도 하지 않고 1초 기다림
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (countDown != 0) { //countdown이 0이 아니라면
				countDown--; //countdown하나씩 감소 (아래로 내려감)
				if (countDown == 0) { //countdown이 0이라면

					if (controller != null && !controller.moveDown()) //움직인 범위를 벗어난 경우와 컨트롤러가 비지 않았다면 
						this.fixingTetrisBlock(); //하나의 객체에 여러 스레드가 접근해서 처리할 때 해당 스레드만 제외하고 나머지는 접근불가
				}
				this.repaint(); //그림 다시 그리기
				enemy.repaint();
				continue;
			}

			countMove--; //움직임이 하나씩 줄어듬
			if (countMove == 0) { //움직임이 없을 때
				countMove = (21 - (int) comboSpeed.getSelectedItem()) * 5;
				if (controller != null && !controller.moveDown())
					countDown = down;
				else
					this.showGhost();
			}

			if (countUp != 0) { //countUp이 0이 아닐때
				countUp--; //countUp -1씩 됨.
				if (countUp == 0) { //countUp이 0이 되면
					countUp = up; //countUp에 0  대입
					addBlockLine(1); //블록라인 1줄 추가
				}
			}

			this.repaint(); //다시 그리기
			enemy.repaint();
		} // while()
	}// run()

	/**
	 * 筌랃옙(癰귣똻�뵠疫뀐옙, 占쎈걠�뵳占�)占쎌뱽 占쎄맒占쎈릭嚥∽옙 占쎌뵠占쎈짗占쎈립占쎈뼄.
	 * 
	 * @param lineNumber
	 * @param num
	 *            -1 or 1
	 */
	public void dropBoard(int lineNumber, int num) {

		// 筌띾벊�뱽 占쎈샵占쎈선占쎈뱜�뵳怨뺣뼄.
		this.dropMap(lineNumber, num); //라인수와 방향에 맞게 dropMap호출

		// �넫�슦紐닺쳸遺쏀벊雅뚯눊由�(1筌띾슦寃�筌앹빓占�)
		this.changeTetrisBlockLine(lineNumber, num); //한줄씩 테트리스 블록 라인 변경(라인수에 맞게)

		// 占쎈뼄占쎈뻻 筌ｋ똾寃뺧옙釉�疫뀐옙
		this.checkMap(); 
		if (isSingle == false && isPlay)
		{
			testfun();
			GoBlind();
		}
		// �⑥쥙�뮞占쎈뱜 占쎈뼄占쎈뻻 �굢�슢�봺疫뀐옙
		this.showGhost();
	}

	/**
	 * lineNumber占쎌벥 占쎌맄筌잞옙 占쎌뵬占쎌뵥占쎈굶占쎌뱽 筌뤴뫀紐� num燁삳챷逾� 占쎄땀�뵳怨뺣뼄.
	 * 
	 * @param lineNumber
	 * @param num
	 *            燁삳챷�땾 -1,1
	 */
	private void dropMap(int lineNumber, int num) { //라인수, 방향에 따라 Map을 그리는 함수
		if (num == 1) {
			// 占쎈립餓κ쑴逾� 占쎄땀�뵳�덈┛ 가장 윗줄부터 시작
			for (int i = lineNumber; i > 0; i--) { //라인수부터 감소하며 1줄까지 반복
				for (int j = 0; j < map[i].length; j++) { //해당 라인수의 블록길이까지 반복
					map[i][j] = map[i - 1][j]; //map을 한줄씩 위로 올린다.
				}
			}

			// 筌랃옙 占쎌맮餓κ쑴占� null嚥∽옙 筌띾슢諭얏묾占�
			for (int j = 0; j < map[0].length; j++) { //map의 첫줄 길이만큼 반복
				map[0][j] = null; //map의 첫줄을 null값으로 비워버림
			}
		} else if (num == -1) {
			// 占쎈립餓κ쑴逾� 占쎌궞�뵳�덈┛ //가장 아랫줄부터 시작
			for (int i = 1; i <= lineNumber; i++) { //라인수까지 증가하며 1줄씩 반복
				for (int j = 0; j < map[i].length; j++) { //해당 라인수의 블록길이까지 반복
					map[i - 1][j] = map[i][j]; //map을 한줄씩 아래로 내린다.
				}
			}

			// removeLine占쏙옙 null嚥∽옙 筌띾슢諭얏묾占�
			for (int j = 0; j < map[0].length; j++) { //map의 첫줄 길이만큼 반복
				map[lineNumber][j] = null; //map의 가장 윗줄을 null값으로 비워버림.
			}
		}
	}

	private void changeTetrisBlockLine(int lineNumber, int num) {
		int y = 0, posY = 0;
		for (int i = 0; i < blockList.size(); i++) { //blockList사이즈만큼 반복
			y = blockList.get(i).getY(); //blockList의 i번째에서 해당 블록의 Y절대 좌표 y에 대입
			posY = blockList.get(i).getPosGridY(); //blockList의 i번째에서 해당 블록의 posGridY좌표를 posY로 설정
			if (y <= lineNumber) //절대좌표 y가 라인수보다 작을 때
				blockList.get(i).setPosGridY(posY + num); //blockList의 i번째의 절대 좌표 Y가 현재 절대 좌료Y값을 posY+num으로 설정
		}
	}

	private void fixingTetrisBlock() {
		synchronized (this) {
			if (stop) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		boolean isCombo = false;
		removeLineCount = 0;

		for (Block block : shap.getBlock()) {
			blockList.add(block);
		}

		isCombo = checkMap();

		if (isCombo)
			removeLineCombo++;
		else
			removeLineCombo = 0;

		if (isSingle == false)
			this.getFixBlockCallBack(blockList, removeLineCombo, removeLineCount);

		this.nextTetrisBlock();

		isHold = false;
		if (isSingle == false && isPlay) {
			testfun();
		}
	}// fixingTetrisBlock()

	private boolean checkMap() {
		boolean isCombo = false;
		int count = 0;
		Block mainBlock;

		for (int i = 0; i < blockList.size(); i++) { //blockList의 사이즈만큼 반복
			mainBlock = blockList.get(i); //blockList를 순서대로 mainBlock에 대입

			if (mainBlock.getY() < 0 || mainBlock.getY() >= maxY) 
				continue; //mainBlock의 현재 y절대좌표가 0보다 작거나 블록이 움직일 수 있는 GridY좌표의 개수보다 크면 continue

			if (mainBlock.getY() < maxY && mainBlock.getX() < maxX) //mainBlock의 현재 y절대좌표가 블록이 움직일 수 있는 GridY좌표의 개수보다 작고 mainBlock의 x절대좌표가 블록이 움직일 수 있는 GridX좌표 개수보다 작으면
				map[mainBlock.getY()][mainBlock.getX()] = mainBlock; //map에 mainBlock이 추가됨. 
				
			if (mainBlock.getY() == 1 && mainBlock.getX() > 2 && mainBlock.getX() < 7) {
				//mainBlock의 y절대좌표가 1이고 mainBlock의 x절대좌표가 2보다 크고 mainBlock의 x절대좌표가 7보다 작으면
				this.gameEndCallBack(); //게임오버
				break;
			}

			count = 0;
			for (int j = 0; j < maxX; j++) { //블록이 움직일 수 있는 GridX좌표까지 반복
				if (map[mainBlock.getY()][j] != null) //mainBlock의 y좌표의 행이 비어있지않다면
					count++; 

			}

			if (count == maxX) { //count가 블록이 움직일 수 있는 GridX좌표와 같다면
				removeLineCount++; 
				this.removeBlockLine(mainBlock.getY()); //mainBlock의 y절대좌표의 라인을 지워버림.
				isCombo = true;
			}
		}
		return isCombo;
	}

	public void nextTetrisBlock() { 
		shap = nextBlocks.get(0); //다음 블록의 첫번째꺼를 현재블록(shap)에 저장
		this.initController(); //controllr가 움직일 블록 설정
		nextBlocks.remove(0); //nextBlocks의 첫번째것을 지운다.
		nextBlocks.add(getRandomTetrisBlock()); //nextBlocks의 끝값에 새로운 Random블록을 추가한다.
	}

	private void initController() { //controller가 움직일 블록 설정 함수
		controller.setBlock(shap); //controller에 움직일 블록(shap)을 넘겨준다.
		ghost = getBlockClone(shap, true); //shap을 이용해 유령블록 생성
		controllerGhost.setBlock(ghost); //controllerGhost에 움직일 블록(ghost)을 넘겨준다.
	}

	private void removeBlockLine(int lineNumber) { 
		for (int j = 0; j < maxX; j++) { //블록이 움직일 수 있는 GirdX좌표 개수까지 반복
			for (int s = 0; s < blockList.size(); s++) { //blockList의 사이즈만큼 반복
				Block b = blockList.get(s); //blockList를 순서대로 b에 저장
				if (b == map[lineNumber][j]) //b가 map[라인수][j]와 같다면
					blockList.remove(s); //해당 blockList 제거
			}
			map[lineNumber][j] = null; //map[라인수][j]=null값으로 비워버림.
		} // for(j)

		this.dropBoard(lineNumber, 1); //라인수로, 윗줄부터 
	}

	public void gameEndCallBack() { //게임오버
		if (isSingle == false)
			client.gameover();
		else {
			// systemMsg.printMessage("Game Over!");
		}
		this.isPlay = false;
	}

	public void testfun() {
		// System.out.println("留� 蹂대궡湲� �떎�뿕以�");
		Block[] temp = new Block[maxX * maxY];
		for (int a = 0; a < 21; a++) {
			for (int b = 0; b < 10; b++) {
				if (map[a][b] != null) {
					temp[a * 10 + b] = map[a][b];
				}
			}
		}

		client.SendMap(temp, tetris.isserver);
	}

	public void GoBlind() {
		Random gen = new Random();
		if (gen.nextInt(10) < 1)
			client.Blind();
	}

	public void BlindAction() {
		start_blind_time = System.currentTimeMillis();
		usingBlind = true;
	}

	private void showGhost() {
		ghost = getBlockClone(shap, true); //ghost에 shap(새로 만든 랜덤 블록)에 대한 유령블록 생성
		controllerGhost.setBlock(ghost); //유령블록 컨트롤러에 ghost블럭을 움직일 수 있게 넘겨줌.
		controllerGhost.moveQuickDown(shap.getPosY(), true); //shap의 현재 블록의 위치, 움직임 여부 true
		//블록을 이동시키거나, 체크해서 범위를 벗어났다면 원상복귀 시킨다.
	}

	public TetrisBlock getRandomTetrisBlock() { //랜덤으로 테트리스 불록 생성(정하기)
		switch ((int) (Math.random() * 7)) { //랜덤 숫자 받기
		case TetrisBlock.TYPE_CENTERUP: 
			return new CenterUp(4, 1);
		case TetrisBlock.TYPE_LEFTTWOUP:
			return new LeftTwoUp(4, 1);
		case TetrisBlock.TYPE_LEFTUP:
			return new LeftUp(4, 1);
		case TetrisBlock.TYPE_RIGHTTWOUP:
			return new RightTwoUp(4, 1);
		case TetrisBlock.TYPE_RIGHTUP:
			return new RightUp(4, 1);
		case TetrisBlock.TYPE_LINE:
			return new Line(4, 1);
		case TetrisBlock.TYPE_NEMO:
			return new Nemo(4, 1);
		}
		return null;
	}

	public TetrisBlock getBlockClone(TetrisBlock tetrisBlock, boolean isGhost) { //유령블록만들기(만들 블록, 참/거짓)
		TetrisBlock blocks = null; //새로운 블록을 만들어 null로 초기화
		switch (tetrisBlock.getType()) { //만들 블록 타입을 받아온다.
		case TetrisBlock.TYPE_CENTERUP:
			blocks = new CenterUp(4, 1);
			break;
		case TetrisBlock.TYPE_LEFTTWOUP:
			blocks = new LeftTwoUp(4, 1);
			break;
		case TetrisBlock.TYPE_LEFTUP:
			blocks = new LeftUp(4, 1);
			break;
		case TetrisBlock.TYPE_RIGHTTWOUP:
			blocks = new RightTwoUp(4, 1);
			break;
		case TetrisBlock.TYPE_RIGHTUP:
			blocks = new RightUp(4, 1);
			break;
		case TetrisBlock.TYPE_LINE:
			blocks = new Line(4, 1);
			break;
		case TetrisBlock.TYPE_NEMO:
			blocks = new Nemo(4, 1);
			break;
		}
		if (blocks != null && isGhost) { //블럭이 비지 않고, isGhost가 true라면
			blocks.setGhostView(isGhost); //TetrisBlock의 고스트뷰 설정하는 함수 호출
			blocks.setPosX(tetrisBlock.getPosX()); //tetrisBlock의 x좌표를 받아 고스트 블럭의 x좌표로 설정
			blocks.setPosY(tetrisBlock.getPosY()); //tetrisBlock의 y좌표를 받아 고스트 블럭의 y좌표로 설정
			blocks.rotation(tetrisBlock.getRotationIndex()); //tetrisBlock의 회전모양을 받아 고스트 블럭의 회전모양으로 설정
		}
		return blocks;
	}

	public void getFixBlockCallBack(ArrayList<Block> blockList, int removeCombo, int removeMaxLine) {
		if (removeCombo < 3) {
			if (removeMaxLine == 3)
				client.addBlock(1);
			else if (removeMaxLine == 4)
				client.addBlock(3);
		} else if (removeCombo < 10) {
			if (removeMaxLine == 3)
				client.addBlock(2);
			else if (removeMaxLine == 4)
				client.addBlock(4);
			else
				client.addBlock(1);
		} else {
			if (removeMaxLine == 3)
				client.addBlock(3);
			else if (removeMaxLine == 4)
				client.addBlock(5);
			else
				client.addBlock(2);
		}
	}

	public void playBlockHold() {
		if (isHold)
			return;

		if (hold == null) { //hold가 비었으면
			hold = getBlockClone(shap, false); //shap을 복사하여 hold 블록만들기, 유령블록은 생성하지 않음
			this.nextTetrisBlock();
		} else { //hold가 있다면
			TetrisBlock tmp = getBlockClone(shap, false); //shap을 복사하여 tmp에 저장해둠, 유령블록은 생성하지 않음
			shap = getBlockClone(hold, false); //shap에 hold를 복사하여 저장, 유령블록은 생성하지 않음
			hold = getBlockClone(tmp, false); //hold에는 tmp을 복사하여 저장, 유령블록은 생성하지 않음.
			this.initController(); //controller가 움직일 블록 설정
		}

		isHold = true; //Hold의 유무 체크
	}

	boolean stop = false;
	
	/**
	 * 테트리스 블럭을 조정하는 컨트롤러이다.
	 * 
	 * @param block : 움직일 테트리스 블럭
	 * @param minX : 블럭이 움직일 최소 GridX좌표
	 * @param minY : 블럭이 움직일 최소 GridY좌표
	 * @param maxX : 블럭이 움직일 최대 GridX좌표
	 * @param maxY : 블럭이 움직일 최대 GridY좌표
	 */
	
	public void addBlockLine(int numOfLine) { //블럭라인추가함수 (라인의 수)
		stop = true;
		Block block; //block 생성
		int rand = (int) (Math.random() * maxX); //rand값 생성 maxX곱하기
		for (int i = 0; i < numOfLine; i++) { //라인 수만큼 반복
			this.dropBoard(maxY - 1, -1); //블럭이 움직일 최대 GridY좌표 -1부터 아랫줄부터 dropBoard함수 호출
			for (int col = 0; col < maxX; col++) { //블럭이 움직일 최대 GridX좌표만큼 반복
				if (col != rand) { //col이 rand와 같지 않다면
					block = new Block(0, 0, Color.GRAY, Color.GRAY); //새로운 회색 블럭 생성
					block.setPosGridXY(col, maxY - 1); //block의 좌표를 col, maxY-1로 설정
					blockList.add(block); //blockList에 새로 만든 block추가
					map[maxY - 1][col] = block; 

					if (isSingle == false && isPlay)
						testfun();
				}
			}
			boolean up = false;
			for (int j = 0; j < shap.getBlock().length; j++) { //shap의 colBlock의 길이(4)까지 반복
				Block sBlock = shap.getBlock(j); //sBlock에 shap의 colBlock[j]를 대입
				if (map[sBlock.getY()][sBlock.getX()] != null) { //map[sBlock의 블록의 y절대좌표 + 사각형 고정 Y그리드좌표][sBlock의 블록의 x절대좌표 + 사각형 고정 X그리드좌표]이 존재한다면
					up = true;
					break;
				}
			}
			if (up) {
				controller.moveDown(-1); //이동
			}
		}

		this.showGhost(); //유령조각 보이기
		this.repaint(); //다시 그리기
		enemy.repaint(); //적 다시 그리기
		synchronized (this) {
			stop = false;
			this.notify();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			messageArea.requestFocus();
		}
		if (!isPlay)
			return;
		if (e.getKeyCode() == key_set[0]) {
			controller.moveLeft();
			controllerGhost.moveLeft();
		} else if (e.getKeyCode() == key_set[1]) {
			controller.moveRight();
			controllerGhost.moveRight();
		} else if (e.getKeyCode() == key_set[4]) {
			controller.moveDown();
		} else if (e.getKeyCode() == key_set[2]) {
			controller.nextRotationLeft();
			controllerGhost.nextRotationLeft();
		} else if (e.getKeyCode() == key_set[3]) {
			controller.nextRotationRight();
			controllerGhost.nextRotationRight();
		} else if (e.getKeyCode() == key_set[5]) {
			controller.moveQuickDown(shap.getPosY(), true);
			this.fixingTetrisBlock();
		} else if (e.getKeyCode() == key_set[6]) {
			playBlockHold();
		}
		this.showGhost();
		this.repaint();
		enemy.repaint();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		this.requestFocus();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnStart) {
			if (client != null) {
				client.gameStart((int) comboSpeed.getSelectedItem());
				setFocusable(true);
			} else {
				this.gameStart((int) comboSpeed.getSelectedItem());
				setFocusable(true);
			}
		} else if (e.getSource() == btnExit) {
			if (client != null) {
				if (tetris.isNetwork()) {
					client.closeNetwork(tetris.isServer());
				}
			} else {
				new TMain(key_set);
				tetris.dispose();
			}
		}
	}

	public boolean isPlay() {
		return isPlay;
	}

	public void setPlay(boolean isPlay) {
		this.isPlay = isPlay;
	}

	public JButton getBtnStart() {
		return btnStart;
	}

	public JButton getBtnExit() {
		return btnExit;
	}

	public void setClient(GameClient client) {
		this.client = client;
	}

	public void printSystemMessage(String msg) {
		systemMsg.printMessage(msg);
	}

	public void printMessage(String msg) {
		messageArea.printMessage(msg);
	}

	public GameClient getClient() {
		return client;
	}

	public ArrayList<Block> getMap() {
		return blockList;
	}

	public void changeSpeed(Integer speed) {
		comboSpeed.setSelectedItem(speed);
	}

	public void clearMessage() {
		if (isSingle == false) {
			messageArea.clearMessage();
			systemMsg.clearMessage();
		}
	}

}