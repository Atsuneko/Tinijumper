package jisaneko.tinijumper.game.entities;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.source.GameObject;

public class Bullet extends GameObject{

	
	public static final int MAXVEL = Main.SCR_H / 160;
	
	public Bullet(int pX, int pY, int fX, int fY) {
		
		super("resources/images/enemy/bullet.png" , pX, pY, Obstacle.SIZE / 3, Obstacle.SIZE / 3);
		
		setVel(fX, fY);
		
	}
	
	
	
	public void tick(){
		
		posX += velX; posY += velY;
		
	}
	
	
	
	public void setVel(int fX, int fY){
		
		double dX = fX - posX, dY = fY - posY;
		
		if(dX == 0){
			if(dY > 0) velY = MAXVEL;
			if(dY < 0) velY = -MAXVEL; 
		}
		if(dY == 0){
			if(dX > 0) velX = MAXVEL;
			if(dX < 0) velX = -MAXVEL; 
		}
		
		if(dX != 0 && dY !=0){
			if(dX > 0) velX = MAXVEL / Math.sqrt(1 + (dY * dY)/(dX * dX));
			if(dX < 0) velX = -MAXVEL / Math.sqrt(1 + (dY * dY)/(dX * dX));
			velY = (dY/dX) * velX;
		}
		
		else{
			velX = MAXVEL;
		}
		
	}

}
