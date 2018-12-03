package jisaneko.tinijumper.game.enemies;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.entities.Obstacle;
import jisaneko.tinijumper.game.source.GameObject;

public class FlyEnemy extends GameObject{


	public static final int MAXVEL = Main.SCR_H / 240;

	public FlyEnemy(int pX, int pY) {

		super("resources/images/enemy/flyenemy.gif", pX, pY, Obstacle.SIZE, Obstacle.SIZE);

	}



	public void tick(){

		posX += velX; posY += velY;		

	}


	public void setVel(int fX, int fY){

		int dX = fX - posX, dY = fY - posY;

		if(dX == 0){
			if(dY > 0) velY = MAXVEL;
			if(dY < 0) velY = -MAXVEL; 
		}
		if(dY == 0){
			if(dX > 0) velX = -MAXVEL;
			if(dX < 0) velX = MAXVEL; 
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



	public void stop(){

		velX = 0; velY = 0;

	}

}
