package jisaneko.tinijumper.game.enemies;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.entities.Obstacle;
import jisaneko.tinijumper.game.source.GameObject;

public class Elim extends GameObject {


	public static final int MAXVEL = Main.SCR_H / 240;

	public Elim(int pX, int pY) {

		super("resources/images/enemy/elim.png", 0, pY, Main.SCR_W, Obstacle.SIZE / 3);
		
		setVel(0, -MAXVEL/2);

	}



	public void tick(){

		posX += velX; posY += velY;

	}


	public void setVel(int velX, int velY){

		this.velX = velX; this.velY = velY;

	}



	public void stop(){

		velX = 0; velY = 0;

	}

}
