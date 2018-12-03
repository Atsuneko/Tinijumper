package jisaneko.tinijumper.game.source;

import java.awt.Image;

import javax.swing.ImageIcon;

import jisaneko.tinijumper.game.Main;

public class Kitty extends GameObject{


	public static final int SIZE_Y = Main.SCR_H / 10, SIZE_X = SIZE_Y * 4 / 3;
	public static final double MAXVELX = SIZE_X / 15, MAXVELY = - SIZE_X / 4, GRAVITY = 1;

	static String imgSource = "resources/images/player/ptype";
	Image[] dImg = new Image[6];

	public PlayerImage state = PlayerImage.STANDRIGHT;

	public boolean movingRight;
	public boolean movingLeft;
	public char facing = 'r';

	public boolean botCol;
	public int jumpDelay;
	
	public boolean isBoosting = false;

	public int pType;


	public Kitty(int playerType, int pX, int pY) {

		super(imgSource + playerType + "/stand_r.gif", pX, pY, SIZE_X, SIZE_Y);

		pType = playerType;
		
		dImg[0] = new ImageIcon(imgSource + playerType + "/stand_r.gif").getImage();
		dImg[1] = new ImageIcon(imgSource + playerType + "/stand_l.gif").getImage();
		dImg[2] = new ImageIcon(imgSource + playerType + "/jump_r.png").getImage();
		dImg[3] = new ImageIcon(imgSource + playerType + "/jump_l.png").getImage();
		dImg[4] = new ImageIcon(imgSource + playerType + "/run_r.gif").getImage();
		dImg[5] = new ImageIcon(imgSource + playerType + "/run_l.gif").getImage();

	}



	public void tick(){

		posX += velX; posY += velY;

		if(posX < 0) posX = 0;	if(endX() > Main.SCR_W) posX = Main.SCR_W - sizeX;

		if(movingLeft && velX > -MAXVELX) velX--;
		if(movingRight && velX < MAXVELX) velX++;


		if(jumpDelay > 0) jumpDelay --;
		if(!botCol) velY += GRAVITY;
		else if(jumpDelay == 0) velY = 0;

		if(!movingLeft && !movingRight){
			if(velX > 0) velX--;
			if(velX < 0) velX++;
		}
		
		if(velX < 0) facing = 'l';
		if(velX > 0) facing = 'r';

		if(isBoosting) velY = MAXVELY / 2;
		
		manageImage();
		
	}



	public void move(String dir){

		if(dir.equals("R")){
			movingRight = true;
		}

		if(dir.equals("L")){
			movingLeft = true;
		}

	}



	public void stop(String dir){

		if(dir.equals("R")){
			movingRight = false;
		}
		if(dir.equals("L")){
			movingLeft = false;
		}

	}



	public void jump(){

		if(botCol){
			velY = MAXVELY;
			jumpDelay = 5;
		}

	}



	public void manageImage(){

		if(botCol){
			if(velX == 0){
				if(facing == 'r' && !(state == PlayerImage.STANDRIGHT)) {
					setImage(dImg[0]);
					state = PlayerImage.STANDRIGHT;
				}
				else if(facing == 'l' && !(state == PlayerImage.STANDLEFT)) {
					setImage(dImg[1]);
					state = PlayerImage.STANDLEFT;
				}
			}

			else if(velX > 0 && !(state == PlayerImage.RUNRIGHT)) {
				setImage(dImg[4]);
				state = PlayerImage.RUNRIGHT;
			}
			else if(velX < 0 && !(state == PlayerImage.RUNLEFT)) {
				setImage(dImg[5]);
				state = PlayerImage.RUNLEFT;
			}
		}
		else{
			if(facing == 'r' && !(state == PlayerImage.JUMPRIGHT)) {
				setImage(dImg[2]);
				state = PlayerImage.JUMPRIGHT;
			}
			else if(facing == 'l' && !(state == PlayerImage.JUMPLEFT)) {
				setImage(dImg[3]);
				state = PlayerImage.JUMPLEFT;
			}
		}
		
	}

} enum PlayerImage {

	STANDLEFT,
	STANDRIGHT,
	
	JUMPLEFT,
	JUMPRIGHT,
	
	RUNLEFT,
	RUNRIGHT
	
}

