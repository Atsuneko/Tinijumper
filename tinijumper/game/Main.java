package jisaneko.tinijumper.game;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import jisaneko.tinijumper.game.enemies.Elim;
import jisaneko.tinijumper.game.enemies.FlyEnemy;
import jisaneko.tinijumper.game.enemies.ShootEnemy;
import jisaneko.tinijumper.game.entities.Bullet;
import jisaneko.tinijumper.game.entities.Obstacle;
import jisaneko.tinijumper.game.menus.Controls;
import jisaneko.tinijumper.game.menus.LevelSelect;
import jisaneko.tinijumper.game.menus.MainMenu;
import jisaneko.tinijumper.game.menus.MenuState;
import jisaneko.tinijumper.game.menus.OnlineSelect;
import jisaneko.tinijumper.game.menus.Pause;
import jisaneko.tinijumper.game.source.GameObject;
import jisaneko.tinijumper.game.source.HUD;
import jisaneko.tinijumper.game.source.Kitty;
import jisaneko.tinijumper.online.Client;
import jisaneko.tinijumper.online.OnlineKitty;
import jisaneko.tinijumper.online.Server;

public class Main extends JPanel implements KeyListener, MouseListener, MouseMotionListener, FocusListener{
	/**
	 * Tini Jumper // By Jisaneko
	 * @author Jisaneko
	 */
	private static final long serialVersionUID = 1L;


	public static JFrame window;

	public static int SCR_H = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.9), SCR_W = (int)(SCR_H * 1.2);
	public static int ticksPS = 80;
	int FPS = 0;

	public static int TILESIZEY = SCR_H / 12, TILESIZEX = TILESIZEY * 3;

	State state = State.MENUSTART;

	Random rand = new Random();

	Clip boostSound;
	Clip clip;
	static File meowSound;
	static File shootSound;
	static File selectSound;
	static File hitSound;
	static File jumpSound;

	MediaPlayer mediaPlayer;

	/*GAME OBJECTS - Start*/
	MainMenu menu = new MainMenu();
	LevelSelect lvlSel = new LevelSelect();
	Pause pause = new Pause();
	Controls controls = new Controls();
	OnlineSelect onSel = new OnlineSelect();

	GameObject levelBack;
	Kitty plyr;
	GameObject boostGif;
	ArrayList<Obstacle> walls;
	ShootEnemy shEnemy;
	ArrayList<FlyEnemy> flEnemies;
	ArrayList<Bullet> bullets;
	Elim elim;
	HUD hud;
	/*GAME OBJECTS - End*/

	Server serv;
	Thread servTh;
	Client clnt;
	Thread clntTh;
	String onlineError = "";

	int level = 0, levelHeight = 0, difficulty = 2;

	int posXStart, posYStart;
	int playerType = 1;

	int inGameShift;

	int inMenuShiftX, inMenuShiftY;

	int generateKey;
	static double requiredKey;
	int maxBlockHeight;

	int keyPause;
	boolean keyUpPress = false;
	
	int mousePause;


	//Main method
	public static void main(String[] args){

		Main game = new Main();

		window = new JFrame("Tini Jumper vALPHA");
		//window.setUndecorated(true);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(game);
		window.setSize(SCR_W, SCR_H);
		window.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2 - window.getWidth() / 2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2 - window.getHeight() / 2));
		window.setResizable(false);
		window.setIconImage(Toolkit.getDefaultToolkit().createImage("resources/images/tinijumper.png"));

		game.setBackground(Color.BLACK);
		game.setFocusable(true); game.requestFocusInWindow();
		game.addKeyListener(game);
		game.addMouseListener(game);
		game.addMouseMotionListener(game);
		game.addFocusListener(game);
		game.setFocusTraversalKeysEnabled(false);

		@SuppressWarnings("unused")
		JFXPanel fxPanel = new JFXPanel();

		try {
			GraphicsEnvironment ge = 
					GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("data/cus_font.ttf")));
		} catch (IOException | FontFormatException e) {
			//Error message 
			System.exit(1);
		}

		Image image = new ImageIcon("resources/images/menus/cursor.gif").getImage();
		Cursor c = Toolkit.getDefaultToolkit().createCustomCursor(image , new Point(game.getX(), game.getY()), "img");
		game.setCursor(c);

		requiredKey = Obstacle.SIZE;


		//Sounds
		try {
			game.clip = AudioSystem.getClip();
			meowSound = new File("resources/sounds/meow.wav");
			hitSound = new File("resources/sounds/hit.wav");
			shootSound = new File("resources/sounds/shoot.wav");
			selectSound = new File("resources/sounds/select.wav");
			jumpSound = new File("resources/sounds/jump.wav");
		} catch (LineUnavailableException e) {
			System.exit(1);
		}
		game.boostSound();

		game.playMusic("menu");

		long lastTime = System.nanoTime();
		double ns = 1000000000 / ticksPS;
		double delta = 0;
		long timer = System.currentTimeMillis();
		int frames = 0;
		while(true){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				game.performTick();
				delta--;
			}
			game.repaint();
			frames++;

			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				game.FPS = frames;
				//System.out.println(game.FPS);
				frames = 0;
			}
		}

	}




	/*
	 * PAINT COMPONENT //___\\___//___\\
	 */
	public void paintComponent(Graphics gr){

		super.paintComponent(gr);

		Graphics2D g = (Graphics2D) gr;

		Composite originalTrans = g.getComposite();

		g.setFont(new Font("Joystix Monospace", Font.PLAIN, 30));

		//Menu
		if(state == State.MENUSTART || state == State.MENU || state == State.MENUEXIT){
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, menu.alpha));
			g.drawImage(menu.background.image, menu.background.posX - inMenuShiftX, menu.background.posY - inMenuShiftY, menu.background.sizeX, menu.background.sizeY, this);
			g.drawImage(menu.title.image, menu.title.posX - inMenuShiftX/3, menu.title.posY - inMenuShiftY/3, menu.title.sizeX, menu.title.sizeY, this);
			g.drawImage(menu.start.image, menu.start.posX, menu.start.posY, menu.start.sizeX, menu.start.sizeY, this);
			g.drawImage(menu.multi.image, menu.multi.posX, menu.multi.posY, menu.multi.sizeX, menu.multi.sizeY, this);
			g.drawImage(menu.controls.image, menu.controls.posX, menu.controls.posY, menu.controls.sizeX, menu.controls.sizeY, this);
			g.drawImage(menu.quit.image, menu.quit.posX, menu.quit.posY, menu.quit.sizeX, menu.quit.sizeY, this);
			g.setComposite(originalTrans);
		}

		//Level select
		if(state == State.LVLSEL){
			g.drawImage(lvlSel.background.image, lvlSel.background.posX, lvlSel.background.posY, lvlSel.background.sizeX, lvlSel.background.sizeY, this);
			g.drawImage(lvlSel.title.image, lvlSel.title.posX, lvlSel.title.posY, lvlSel.title.sizeX, lvlSel.title.sizeY, this);
			g.drawImage(lvlSel.exit.image, lvlSel.exit.posX, lvlSel.exit.posY, lvlSel.exit.sizeX, lvlSel.exit.sizeY, this);
			for(GameObject o : lvlSel.levels){
				g.drawImage(o.image, o.posX, o.posY, o.sizeX, o.sizeY, this);
			}
			g.setColor(new Color(255, 22, 147)); g.setFont(new Font("Joystix Monospace", Font.PLAIN, 30)); g.drawString("MORE LEVELS WORK IN PROGRESS", 50, SCR_H - 50);
		}

		//Online select
		if(state == State.ONLINESELECT){
			g.drawImage(onSel.back.image, onSel.back.posX, onSel.back.posY, onSel.back.sizeX, onSel.back.sizeY, this);
			g.drawImage(onSel.host.image, onSel.host.posX, onSel.host.posY, onSel.host.sizeX, onSel.host.sizeY, this);
			g.drawImage(onSel.join.image, onSel.join.posX, onSel.join.posY, onSel.join.sizeX, onSel.join.sizeY, this);
			g.drawImage(onSel.exit.image, onSel.exit.posX, onSel.exit.posY, onSel.exit.sizeX, onSel.exit.sizeY, this);
			g.setColor(new Color(255, 22, 147)); g.setFont(new Font("Joystix Monospace", Font.PLAIN, 50)); g.drawString(onlineError, onSel.host.posX - 3 * onSel.join.sizeY / 4, onSel.join.posY + 3 * onSel.join.sizeY / 2);
			g.setColor(new Color(255, 22, 147)); g.setFont(new Font("Joystix Monospace", Font.PLAIN, TILESIZEY * 3/ 4)); g.drawString("MULTIPLAYER", onSel.host.posX - TILESIZEY / 3, onSel.host.posY - TILESIZEY);
		}

		//Controls
		if(state == State.CONTROLS){
			g.drawImage(controls.back.image, controls.back.posX, controls.back.posY, controls.back.sizeX, controls.back.sizeY, this);
			g.drawImage(controls.exit.image, controls.exit.posX, controls.exit.posY, controls.exit.sizeX, controls.exit.sizeY, this);
		}

		if(state == State.STARTGAME){
			g.drawImage(levelBack.image, levelBack.posX, levelBack.posY - inGameShift / 5 + levelHeight / 5, levelBack.sizeX, levelBack.sizeY, this);

			int numWalls = walls.size();
			for(int i = 0; i < numWalls; i++){
				if(!walls.get(i).visible) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
				else if(walls.get(i).trans && walls.get(i).visible) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
				if(walls.get(i).posY + levelHeight < getHeight() * 1.5) g.drawImage(walls.get(i).image, walls.get(i).posX, walls.get(i).posY - inGameShift + levelHeight, walls.get(i).sizeX, walls.get(i).sizeY, this);
				g.setComposite(originalTrans);
			}
			int numBullets = bullets.size();
			for(int i = 0; i < numBullets; i++){
				g.drawImage(bullets.get(i).image, bullets.get(i).posX, bullets.get(i).posY - inGameShift + levelHeight, bullets.get(i).sizeX, bullets.get(i).sizeY, this);
			}
			int numFlEnemies = flEnemies.size();
			for(int i = 0; i < numFlEnemies; i++){
				g.drawImage(flEnemies.get(i).image, flEnemies.get(i).posX, flEnemies.get(i).posY - inGameShift + levelHeight, flEnemies.get(i).sizeX, flEnemies.get(i).sizeY, this);
			}

		}

		//In game ----------------------------------------------------------------------
		if(state == State.INGAME){
			g.drawImage(levelBack.image, levelBack.posX, levelBack.posY - inGameShift / 5 + levelHeight / 5, levelBack.sizeX, levelBack.sizeY, this);
			if(boostGif.added) g.drawImage(boostGif.image, boostGif.posX, boostGif.posY - inGameShift + levelHeight, boostGif.sizeX, boostGif.sizeY, this);

			int numWalls = walls.size();
			for(int i = 0; i < numWalls; i++){
				if(!walls.get(i).visible) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
				else if(walls.get(i).trans && walls.get(i).visible) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
				if(walls.get(i).posY + levelHeight < getHeight() * 1.5) g.drawImage(walls.get(i).image, walls.get(i).posX, walls.get(i).posY - inGameShift + levelHeight, walls.get(i).sizeX, walls.get(i).sizeY, this);
				g.setComposite(originalTrans);
			}
			int numBullets = bullets.size();
			for(int i = 0; i < numBullets; i++){
				g.drawImage(bullets.get(i).image, bullets.get(i).posX, bullets.get(i).posY - inGameShift + levelHeight, bullets.get(i).sizeX, bullets.get(i).sizeY, this);
			}
			if(level == 2) g.drawImage(shEnemy.image, shEnemy.posX, shEnemy.posY + levelHeight, shEnemy.sizeX, shEnemy.sizeY, this);
			int numFlEnemies = flEnemies.size();
			for(int i = 0; i < numFlEnemies; i++){
				g.drawImage(flEnemies.get(i).image, flEnemies.get(i).posX, flEnemies.get(i).posY - inGameShift + levelHeight, flEnemies.get(i).sizeX, flEnemies.get(i).sizeY, this);
			}

			if(plyr.added) g.drawImage(plyr.image, plyr.posX, plyr.posY - inGameShift + levelHeight, plyr.sizeX, plyr.sizeY, this);
			g.drawImage(elim.image, elim.posX, elim.posY - inGameShift + levelHeight, elim.sizeX, elim.sizeY, this);
			
			g.setColor(hud.boostCol);
			g.fillRect(hud.boostPosX, hud.boostPosY, hud.boostSizeX, HUD.BOOSTSIZEY);
			g.setColor(new Color(255, 22, 147)); g.setFont(new Font("Joystix Monospace", Font.PLAIN, SCR_H / 30)); g.setColor(hud.boostCol); g.drawString("Boost", hud.boostPosX, hud.boostPosY - 10);
			if(levelHeight <= 0){
				g.setColor(new Color(255, 22, 147));
				g.setFont(new Font("Joystix Monospace", Font.PLAIN, SCR_H / 20)); 
				g.drawString("Jump! Jump! Jump!", SCR_W / 6, SCR_H / 3);
			}
		}

		//Online
		if(state == State.ONLINESTART){

		}
		if(state == State.ONLINEINGAME){
			g.setFont(new Font("Joystix Monospace", Font.PLAIN, 20)); g.setColor(new Color(255, 22, 147));

			g.drawImage(clnt.levelBack.image, clnt.levelBack.posX, clnt.levelBack.posY + levelHeight / 5, clnt.levelBack.sizeX, clnt.levelBack.sizeY, this);

			g.drawImage(clnt.oplyr.image, clnt.oplyr.posX, clnt.oplyr.posY + clnt.levHeight, clnt.oplyr.sizeX, clnt.oplyr.sizeY, this);
			int numPlyrs = clnt.plyrs.size();
			for(int i = 0; i < numPlyrs; i++){
				g.drawString(clnt.plyrs.get(i).username, clnt.plyrs.get(i).posX, clnt.plyrs.get(i).posY - 25 + clnt.levHeight);
				g.drawImage(clnt.plyrs.get(i).image, clnt.plyrs.get(i).posX, clnt.plyrs.get(i).posY + clnt.levHeight, clnt.plyrs.get(i).sizeX, clnt.plyrs.get(i).sizeY, this);
			}
			int numWalls = clnt.walls.size();
			for(int i = 0; i < numWalls; i++){
				g.drawImage(clnt.walls.get(i).image, clnt.walls.get(i).posX, clnt.walls.get(i).posY + clnt.levHeight, clnt.walls.get(i).sizeX, clnt.walls.get(i).sizeY, this);
			}

			if(clnt.levHeight <= 0){
				g.setColor(new Color(255, 22, 147));
				g.setFont(new Font("Joystix Monospace", Font.PLAIN, SCR_H / 20)); 
				g.drawString(clnt.disp, SCR_W / 6, SCR_H / 3);
			}
		}

		//Pause
		if(state == State.PAUSE){
			g.drawImage(levelBack.image, levelBack.posX, levelBack.posY - inGameShift / 5 + levelHeight / 5, levelBack.sizeX, levelBack.sizeY, this);
			if(boostGif.added) g.drawImage(boostGif.image, boostGif.posX, boostGif.posY - inGameShift + levelHeight, boostGif.sizeX, boostGif.sizeY, this);

			int numWalls = walls.size();
			for(int i = 0; i < numWalls; i++){
				if(!walls.get(i).visible) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
				else if(walls.get(i).trans && walls.get(i).visible) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
				if(walls.get(i).posY + levelHeight < getHeight() * 1.5) g.drawImage(walls.get(i).image, walls.get(i).posX, walls.get(i).posY - inGameShift + levelHeight, walls.get(i).sizeX, walls.get(i).sizeY, this);
				g.setComposite(originalTrans);
			}
			if(level == 2) g.drawImage(shEnemy.image, shEnemy.posX, shEnemy.posY, shEnemy.sizeX, shEnemy.sizeY, this);
			int numBullets = bullets.size();
			for(int i = 0; i < numBullets; i++){
				g.drawImage(bullets.get(i).image, bullets.get(i).posX, bullets.get(i).posY - inGameShift + levelHeight, bullets.get(i).sizeX, bullets.get(i).sizeY, this);
			}
			int numFlEnemies = flEnemies.size();
			for(int i = 0; i < numFlEnemies; i++){
				g.drawImage(flEnemies.get(i).image, flEnemies.get(i).posX, flEnemies.get(i).posY - inGameShift + levelHeight, flEnemies.get(i).sizeX, flEnemies.get(i).sizeY, this);
			}
			if(plyr.added) g.drawImage(plyr.image, plyr.posX, plyr.posY - inGameShift + levelHeight, plyr.sizeX, plyr.sizeY, this);
			g.drawImage(elim.image, elim.posX, elim.posY - inGameShift + levelHeight, elim.sizeX, elim.sizeY, this);
			g.setColor(hud.boostCol);
			g.fillRect(hud.boostPosX, hud.boostPosY, hud.boostSizeX, HUD.BOOSTSIZEY);
			g.setColor(new Color(255, 22, 147)); g.setFont(new Font("Joystix Monospace", Font.PLAIN, SCR_H / 30)); g.setColor(hud.boostCol); g.drawString("Boost", hud.boostPosX, hud.boostPosY - 10);

			g.drawImage(pause.background.image, pause.background.posX, pause.background.posY, pause.background.sizeX, pause.background.sizeY, this);
			g.setColor(new Color(255, 22, 147)); g.setFont(new Font("Joystix Monospace", Font.PLAIN, TILESIZEY * 3/ 4)); g.drawString("PAUSED", pause.main.posX, pause.main.posY - TILESIZEY);
			g.drawImage(pause.main.image, pause.main.posX, pause.main.posY, pause.main.sizeX, pause.main.sizeY, this);
			g.drawImage(pause.quit.image, pause.quit.posX, pause.quit.posY, pause.quit.sizeX, pause.quit.sizeY, this);
		}


		if(state == State.LVLCOMPLETE){
			g.drawImage(levelBack.image, levelBack.posX, levelBack.posY - inGameShift / 5 + levelHeight / 5, levelBack.sizeX, levelBack.sizeY, this);
			if(boostGif.added) g.drawImage(boostGif.image, boostGif.posX, boostGif.posY - inGameShift + levelHeight, boostGif.sizeX, boostGif.sizeY, this);

			int numWalls = walls.size();
			for(int i = 0; i < numWalls; i++){
				if(!walls.get(i).visible) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
				else if(walls.get(i).trans && walls.get(i).visible) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
				if(walls.get(i).posY + levelHeight < getHeight() * 1.5) g.drawImage(walls.get(i).image, walls.get(i).posX, walls.get(i).posY - inGameShift + levelHeight, walls.get(i).sizeX, walls.get(i).sizeY, this);
				g.setComposite(originalTrans);
			}
			int numBullets = bullets.size();
			for(int i = 0; i < numBullets; i++){
				g.drawImage(bullets.get(i).image, bullets.get(i).posX, bullets.get(i).posY - inGameShift + levelHeight, bullets.get(i).sizeX, bullets.get(i).sizeY, this);
			}
			int numFlEnemies = flEnemies.size();
			for(int i = 0; i < numFlEnemies; i++){
				g.drawImage(flEnemies.get(i).image, flEnemies.get(i).posX, flEnemies.get(i).posY - inGameShift + levelHeight, flEnemies.get(i).sizeX, flEnemies.get(i).sizeY, this);
			}
			g.drawImage(plyr.image, plyr.posX, plyr.posY - inGameShift + levelHeight, plyr.sizeX, plyr.sizeY, this);
			g.drawImage(elim.image, elim.posX, elim.posY - inGameShift + levelHeight, elim.sizeX, elim.sizeY, this);
			g.setColor(hud.boostCol);
			g.fillRect(hud.boostPosX, hud.boostPosY, hud.boostSizeX, HUD.BOOSTSIZEY);
			g.setColor(new Color(255, 22, 147)); g.setFont(new Font("Joystix Monospace", Font.PLAIN, SCR_H / 30)); g.setColor(hud.boostCol); g.drawString("Boost", hud.boostPosX, hud.boostPosY - 10);

			g.setColor(new Color(255, 22, 147)); g.setFont(new Font("Joystix Monospace", Font.PLAIN, TILESIZEY * 3/ 4)); g.drawString("VICTORY!", pause.main.posX - 100, pause.main.posY - TILESIZEY);
			g.drawImage(pause.main.image, pause.main.posX, pause.main.posY, pause.main.sizeX, pause.main.sizeY, this);
			g.drawImage(pause.quit.image, pause.quit.posX, pause.quit.posY, pause.quit.sizeX, pause.quit.sizeY, this);
		}

	}




	/*
	 * PERFORM TICK ||----||----||----||
	 */
	public void performTick(){

		if(keyPause > 0) keyPause--;
		if(mousePause > 0) mousePause--;

		//Menu
		if(state == State.MENUSTART || state == State.MENU || state == State.MENUEXIT){
			menu.tick();
			if(menu.state == MenuState.INMENU) state = State.MENU;
			if(menu.state == MenuState.EXITED) state = State.LVLSEL;
		}

		//Game start
		if(state == State.STARTGAME){

			levelHeight -= levelHeight / 20;
			levelHeight -= 1;
			if(levelHeight <= 0) {
				levelHeight = 0;
				createLevel(level);
			}

		}

		//In game
		if(state == State.INGAME){

			plyr.tick();

			for(FlyEnemy fE : flEnemies){
				fE.tick();
			}
			for(Bullet b : bullets){
				b.tick();
			}elim.tick();
			hud.tick();
			for(Obstacle o : walls){
				o.tick();
			}

			if(level == 1 && elim.velY > -Elim.MAXVEL * 1.3 || level == 2 && elim.velY > -Elim.MAXVEL) elim.velY -= levelHeight / 100;

			if(levelHeight/ 5 + getHeight() >= levelBack.sizeY){
				winLevel();
			}

			boostGif.posX = plyr.posX; boostGif.posY = (int)plyr.midY();
			if(hud.inBoost) boostGif.added = true;
			else boostGif.added = false;

			plyr.botCol = checkBotCollisions();
			checkTopAndSides();

			if(hud.inBoost && !plyr.isBoosting) {
				plyr.isBoosting = true;
				boostSound.loop(Clip.LOOP_CONTINUOUSLY);
			}
			if(!hud.inBoost && plyr.isBoosting) {
				plyr.isBoosting = false;
				boostSound.stop();
			}

			if(plyr.posY - maxBlockHeight < Obstacle.SIZE * 5){
				for(int i = 0; i < 100; i++) generateBlocks();
			}
			
			if(keyUpPress){
				if(plyr.botCol) playSound(meowSound);
				plyr.jump();
			}

			if(level == 2){
				shEnemy.tick();
				shEnemy.posY = -levelHeight;
				if(shEnemy.shoot){
					if(bullets.size() > 4) bullets.remove(0);
					bullets.add(new Bullet((int)(shEnemy.midX() - Obstacle.SIZE / 4), (int)(shEnemy.midY() - Obstacle.SIZE / 4), (int)(plyr.midX()), (int)(plyr.midY())));
					playSound(shootSound);
				}
			}

			for(FlyEnemy fE : flEnemies){
				if(fE.midY() + levelHeight >= 0 && fE.midY() + levelHeight <= getHeight()) fE.setVel((int)(plyr.midX() - Obstacle.SIZE / 2), (int)(plyr.midY() - Obstacle.SIZE / 2));
				else fE.stop();
			}

			if(plyr.posY + levelHeight < getHeight() / 3){
				levelHeight = getHeight() / 3  - plyr.posY;
				generateKey += getHeight() / 3 - plyr.posY + levelHeight;
				if(!hud.inBoost) hud.boost += 0.1;
			}

			if(plyr.midY() + levelHeight > getHeight() || plyr.midY() + levelHeight <= levelBack.posY + (1/10) * levelBack.posY) {
				playSound(hitSound);
				endLevel();
			}
			for(Bullet b : bullets){
				if(plyr.collides(b)) {
					playSound(hitSound);
					endLevel();
				}
			}
			for(FlyEnemy fE : flEnemies){
				if(plyr.collides(fE)) {
					playSound(hitSound);
					endLevel();
				}
			}
			if(plyr.collides(elim)) {
				playSound(hitSound);
				endLevel();
			}
			for(Obstacle o : walls){
				if(plyr.collides(o) && o.trans) o.visible = false;
				else if(!plyr.collides(o) && o.trans) o.visible = true;

				if(o.collides(plyr.posX, (int)plyr.endX(), plyr.posY + Kitty.SIZE_Y, (int)plyr.endY() + Kitty.SIZE_Y) && o.type.equals("hardwall.png") && plyr.botCol){
					plyr.posX += o.velX;
				}
			}

		}

		//Online
		if(state == State.ONLINEINGAME){
			clnt.oplyr.tick();
			int numWalls = clnt.walls.size();
			for(int i = 0; i < numWalls; i++){
				clnt.walls.get(i).tick();
			}

			int numNewWalls = clnt.walls.size();
			for(int i = 0; i < numNewWalls; i++){
				if(clnt.walls.get(i).collides(clnt.oplyr.posX, (int)clnt.oplyr.endX(), clnt.oplyr.posY + Kitty.SIZE_Y, (int)clnt.oplyr.endY() + Kitty.SIZE_Y) && clnt.walls.get(i).type.equals("hardwall.png") && clnt.oplyr.botCol){
					clnt.oplyr.posX += clnt.walls.get(i).velX;
				}
			}

			if(!clnt.input.openSpace){
				onlineError = "SERVER IS FULL";
				state = State.ONLINESELECT;
			}

			else if(!clnt.input.serverExists){
				onlineError = "SERVER NOT FOUND";
				state = State.ONLINESELECT;
			}

			if(clnt.gameStarted){
				if(clnt.oplyr.posY + clnt.levHeight < getHeight() / 3){
					clnt.levHeight = getHeight() / 3 - clnt.oplyr.posY;
				}
			}

			if(clnt.oplyr.midY() + clnt.levHeight > getHeight()){
				playSound(hitSound);
				clnt.oplyr.posX = clnt.startX; clnt.oplyr.posY = clnt.startY;
				clnt.levHeight = 0;
			}

			if(clnt.serverHeight < clnt.levHeight){
				clnt.genKey += clnt.levHeight - clnt.serverHeight ;
				clnt.serverHeight = clnt.levHeight;
			}
			if(clnt.genKey >= Obstacle.SIZE){
				if (clnt.input.openSpace && clnt.input.serverExists) clnt.generateBlocksOnline(getHeight(), getWidth());
			}

			for(OnlineKitty k : clnt.plyrs){
				k.manage();
			}
		}

		//Loading
		if(state == State.LOADING){}

		//Controls
		if(state == State.CONTROLS){}

		//Pause
		if(state == State.PAUSE){
			boostSound.stop();
		}

	}



	/*EVENT LISTENERS - START*/

	//Key pressed
	public void keyPressed(KeyEvent k){

		//Menu
		if(state == State.MENU){
			if(k.getKeyCode() == 27){
				System.exit(1);
			}
		}

		//In game
		if(state == State.INGAME){

			if(k.getKeyCode() == 65 || k.getKeyCode() == 37){
				plyr.move("L");
			}

			if(k.getKeyCode() == 68 || k.getKeyCode() == 39){
				plyr.move("R");
			}

			if(k.getKeyCode() == 87 || k.getKeyCode() == 38){
				keyUpPress = true;
			}

			if(k.getKeyCode() == 32){
				hud.inBoost = true;
			}

			//Pause
			if(k.getKeyCode() == 27 && keyPause == 0){
				state = State.PAUSE;
				keyPause = 10;
			}

		}

		//Online
		if(state == State.ONLINEINGAME){

			if(k.getKeyCode() == 65 || k.getKeyCode() == 37){
				clnt.oplyr.move("L");
			}

			if(k.getKeyCode() == 68 || k.getKeyCode() == 39){
				clnt.oplyr.move("R");
			}

			if(k.getKeyCode() == 87 || k.getKeyCode() == 38){
				clnt.oplyr.jump();
			}

		}

		//In Pause menu
		if(state == State.PAUSE && keyPause == 0){
			if(plyr.isBoosting) boostSound.loop(Clip.LOOP_CONTINUOUSLY);
			if(k.getKeyCode() == 27) state = State.INGAME;
			keyPause = 10;
		}

	}



	//Key released
	public void keyReleased(KeyEvent k){

		//In game
		if(state == State.INGAME){

			if(k.getKeyCode() == 65 || k.getKeyCode() == 37){
				plyr.stop("L");
			}

			if(k.getKeyCode() == 68 || k.getKeyCode() == 39){
				plyr.stop("R");
			}

			if(k.getKeyCode() == 87 || k.getKeyCode() == 38){
				keyUpPress = false;
			}
			
			if(k.getKeyCode() == 32){
				hud.inBoost = false;
			}

		}

		//Online
		if(state == State.ONLINEINGAME){
			if(k.getKeyCode() == 65 || k.getKeyCode() == 37){
				clnt.oplyr.stop("L");
			}

			if(k.getKeyCode() == 68 || k.getKeyCode() == 39){
				clnt.oplyr.stop("R");
			}
		}

	}



	//Mouse moved
	public void mouseMoved(MouseEvent m){

		//Menu
		if(state == State.MENU){
			inMenuShiftX = m.getX() / 20; inMenuShiftY = m.getY() / 20;

			if(menu.start.contains(m.getX(), m.getY())) menu.glow("start");
			else menu.stopGlow("start");

			if(menu.multi.contains(m.getX(), m.getY())) menu.glow("multi");
			else menu.stopGlow("multi");

			if(menu.controls.contains(m.getX(), m.getY())) menu.glow("controls");
			else menu.stopGlow("controls");

			if(menu.quit.contains(m.getX(), m.getY())) menu.glow("quit");
			else menu.stopGlow("quit");
		}

		//Online select
		if(state == State.ONLINESELECT){
			if(onSel.host.contains(m.getX(), m.getY())) onSel.glow("host");
			else onSel.stopGlow("host");

			if(onSel.join.contains(m.getX(), m.getY())) onSel.glow("join");
			else onSel.stopGlow("join");
		}

		//In game
		if(state == State.INGAME){
			inGameShift = m.getY() / 10;
		}

		//Pause
		if(state == State.PAUSE){
			if(pause.main.contains(m.getX(), m.getY())) pause.glow("main");
			else pause.stopGlow("main");

			if(pause.quit.contains(m.getX(), m.getY())) pause.glow("quit");
			else pause.stopGlow("quit");
		}

	}



	//Mouse pressed
	public void mousePressed(MouseEvent m){

		if(mousePause <= 0){
			//Menu
			if(state == State.MENU){
				if(menu.start.contains(m.getX(), m.getY())){
					playSound(selectSound);
					menu.exit();
					stopMusic();
					state = State.MENUEXIT;
					mousePause = 10;
				}

				if(menu.controls.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					state = State.CONTROLS;
					mousePause = 10;
				}

				if(menu.multi.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					//state = State.ONLINESELECT;
					//mousePause = 10;
				}

				if(menu.quit.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					System.exit(1);
				}
			}

			//Online select
			if(state == State.ONLINESELECT){
				if(onSel.exit.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					toMainMenu();
				}
				if(onSel.host.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					createServer(7777);
				}
				if(onSel.join.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					joinServer("25.77.201.164", 7777);
				}
			}

			if(state == State.LVLSEL){
				if(onSel.exit.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					toMainMenu();
				}
				for (int i = 0; i < lvlSel.levels.size(); i++){
					if(lvlSel.levels.get(i).contains(m.getX(), m.getY())){
						playSound(selectSound);
						levelHeight = Obstacle.SIZE * 50;
						level = i + 1;
						playMusic("level" + level);
						createLevel(level);
					}
				}
			}


			//Controls
			if(state == State.CONTROLS){
				if(controls.exit.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					toMainMenu();
				}
			}

			//Pause
			if(state == State.PAUSE){
				if(pause.main.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					toMainMenu();
					mousePause = 10;
				}

				if(pause.quit.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					System.exit(1);;
				}
			}
			
			//Lvlcomplete
			if(state == State.LVLCOMPLETE){
				if(pause.main.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					toMainMenu();
					mousePause = 10;
				}

				if(pause.quit.contains(m.getX(), m.getY())) {
					playSound(selectSound);
					System.exit(1);;
				}
			}
		}

	}



	//Focus loss
	public void focusLost(FocusEvent f){

		if(state == State.INGAME){
			state = State.PAUSE;
		}

	}
	/*EVENT LISTENERS - END*/



	//BOTTOM COLLISION CHECKING
	public boolean checkBotCollisions(){

		for(Obstacle o : walls){
			if(plyr.collidesBotCol(o) && !o.trans){
				if((plyr.endY() - o.posY) - plyr.velY < plyr.endX() - o.posX && (plyr.endY() - o.posY) - plyr.velY < o.endX() - plyr.posX){
					if(plyr.midY() < o.midY()){
						plyr.posY = o.posY - plyr.sizeY;
						return true;
					}
				}
			}
		}

		return false;

	}



	public void checkTopAndSides(){

		for(Obstacle o : walls){
			if(plyr.collides(o) && !o.trans){
				if((o.endY() - plyr.posY) - plyr.velY < plyr.endX() - o.posX && (o.endY() - plyr.posY) - plyr.velY < o.endX() - plyr.posX){
					if(plyr.midY() > o.midY()){
						plyr.posY = (int)o.endY();
						plyr.velY = 0;
					}
				}
				else if(!((plyr.endY() - o.posY) - plyr.velY < plyr.endX() - o.posX && (plyr.endY() - o.posY) - plyr.velY < o.endX() - plyr.posX)){
					if(plyr.midX() < o.midX()){
						if(plyr.movingRight && !plyr.movingLeft) plyr.velX = 0;
						plyr.posX = o.posX - plyr.sizeX - 1;
					}
					if(plyr.midX() > o.midX()){
						if(plyr.movingLeft && !plyr.movingRight) plyr.velX = 0;
						plyr.posX = (int) (o.endX() + 1);
					}
				}
			}
		}

	}


	public int midY(){

		return getHeight() / 2;

	}



	public int midX(){

		return getWidth() / 2;

	}



	public void generateBlocks(){

		int fromHeight = getHeight() + levelHeight;
		for(Obstacle o : walls){
			if(o.posY < fromHeight) fromHeight = o.posY;
		}

		ArrayList<Obstacle> highWalls = new ArrayList<Obstacle>();
		for(Obstacle o : walls){
			if(o.posY == fromHeight) highWalls.add(o);
		}

		int nextHeight = fromHeight - ((rand.nextInt(2) + 3) * Obstacle.SIZE);

		maxBlockHeight = nextHeight;

		if(rand.nextDouble() < 0.1 && level == 1){
			walls.add(new Obstacle(rand.nextInt(9 * Obstacle.SIZE), nextHeight, level, "hardwall.png"));
		} else {
			int[] nextPosX = new int[rand.nextInt(3) + 3];

			for(int i = 0; i < nextPosX.length; i++) nextPosX[i] = -1;

			for(int i = 0; i < nextPosX.length; i++){
				nextPosX[i] = rand.nextInt(getWidth() / Obstacle.SIZE);

				boolean doSkip = false;

				for(Obstacle o : highWalls){
					if(nextPosX[i] == o.posX / Obstacle.SIZE) {
						i--;
						doSkip = true;
						break;
					}
				}
				if(doSkip) continue;

				for(Obstacle o : highWalls){
					if(nextPosX[i] + 1 == o.posX / Obstacle.SIZE || nextPosX[i] - 1 == o.posX / Obstacle.SIZE) {
						i--;
						break;
					}
				}
			}

			Arrays.sort(nextPosX);

			for(int i = 0; i < nextPosX.length; i++){
				String blType = "";
				for(int j = 0; j < nextPosX.length; j++){
					if(j == i) continue;
					if(nextPosX[i] == nextPosX[j]) continue;

					if(nextPosX[i] == nextPosX[j] + 1) blType = "_ol";
					if(nextPosX[i] == nextPosX[j] - 1){
						if(blType.equals("")) blType = "_or";
						if(blType.equals("_ol")) blType = "_ph";
					}
				}
				walls.add(new Obstacle(nextPosX[i] * Obstacle.SIZE, nextHeight, level, "wall" + blType + ".png"));
				if(rand.nextDouble() <= 0.01 && level == 3){
					flEnemies.add(new FlyEnemy(nextPosX[i] * Obstacle.SIZE, nextHeight - Obstacle.SIZE));
				}
			}
		}

		requiredKey *= 1.2;

	}



	public void createLevel(int levelType){

		requiredKey = Obstacle.SIZE;

		BufferedReader levelFile = null;
		
		keyUpPress = false;

		try {
			levelFile = new BufferedReader(new FileReader(new File("data/levels/lvl" + levelType + ".txt")));
		} catch (FileNotFoundException e) {
			//Error Message
			System.exit(1);
		}

		elim = new Elim(0, (int)(SCR_H * 3));

		walls = new ArrayList<Obstacle>();
		if(level == 2) shEnemy = new ShootEnemy(SCR_W / 2,0);
		flEnemies = new ArrayList<FlyEnemy>();
		bullets = new ArrayList<Bullet>();

		String line = null;
		try {
			levelFile.readLine();
			while((line = levelFile.readLine()) != null){
				if(line.equals("")) continue;
				if(line.equals("next")) break;
				walls.add(new Obstacle(Integer.parseInt(line.substring(0, 2)) * Obstacle.SIZE, Integer.parseInt(line.substring(2, 4)) * Obstacle.SIZE, levelType, line.substring(4, line.length())));
			}

			line = levelFile.readLine();
		} catch (IOException e) {
			//Error Message
			System.exit(1);
		}

		levelBack = new GameObject("resources/images/backgrounds/lvl" + levelType + ".gif", 0, getHeight() - (getWidth() * 11), getWidth(), getWidth() * 11);

		posXStart = Integer.parseInt(line.substring(0,2)) * Obstacle.SIZE; posYStart = Integer.parseInt(line.substring(2,4)) * Obstacle.SIZE;
		plyr = new Kitty(playerType , posXStart, posYStart);

		hud = new HUD();

		boostGif = new GameObject("resources/images/envir/boost.gif", 0 , 0, plyr.sizeX, plyr.sizeY);
		boostGif.added = false;

		for(int i = 0; i < 100; i++) generateBlocks();

		state = State.INGAME;

	}



	public void endLevel(){

		plyr.added = false;
		boostSound.stop();
		//stopMusic();
		state = State.STARTGAME;

	}


	public void winLevel(){

		plyr.added = false;
		boostSound.stop();
		state = State.LVLCOMPLETE;

	}



	public void toMainMenu(){

		setBackground(Color.BLACK);
		menu = new MainMenu();
		state = State.MENUSTART;
		playMusic("menu");

	}



	public void createServer(int port){

		serv = new Server(port, 1);
		servTh = new Thread(serv);
		servTh.start();

		clnt = new Client("localhost", port, 1, "jisaneko");
		clntTh = new Thread(clnt);
		clntTh.start();

		state = State.ONLINEINGAME;
	}



	public void joinServer(String ip, int port){

		clnt = new Client(ip, port, level, "what a nigger");
		clntTh = new Thread(clnt);
		clntTh.start();

		state = State.ONLINEINGAME;

	}



	public void playSound(File file){

		try {
			clip = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
			if(!clip.isOpen()) clip.open(inputStream);
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
		clip.start(); 

	}



	public void boostSound(){

		try {
			boostSound = AudioSystem.getClip();
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("resources/sounds/boost.wav"));
			boostSound.open(inputStream);
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			System.exit(1);
		}

	}



	public void playMusic(String mus){

		if(mediaPlayer != null) mediaPlayer.stop();

		try{
			Media music = new Media(new File("resources/music/" + mus + ".mp3").toURI().toString());
			mediaPlayer = new MediaPlayer(music);
			mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			mediaPlayer.play();
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}



	//@SuppressWarnings("deprecation")
	public void stopMusic(){

		mediaPlayer.stop();

	}


	//Unused methods
	public void mouseDragged(MouseEvent m){
	}public void mouseEntered(MouseEvent m){
	}public void mouseExited(MouseEvent m){
	}public void mouseClicked(MouseEvent m){
	}public void keyTyped(KeyEvent k){
	}public void focusGained(FocusEvent f){
	}public void mouseReleased(MouseEvent m){
	}

} enum State {

	MENUSTART,
	MENU,
	MENUEXIT,

	CONTROLS,

	LOADING,

	LVLSEL,
	STARTGAME,
	INGAME,
	LVLCOMPLETE,

	TP,

	ONLINESELECT,

	ONLINESTART,
	ONLINEINGAME,

	PAUSE,

}
