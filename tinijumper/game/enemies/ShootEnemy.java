package jisaneko.tinijumper.game.enemies;

import jisaneko.tinijumper.game.Main;
import jisaneko.tinijumper.game.entities.Obstacle;
import jisaneko.tinijumper.game.source.GameObject;

public class ShootEnemy extends GameObject{

	
	public static final int SH_TIME = 160;	
	
	static String imgSource = "resources/images/enemy/shootenemy.png";
	
	public boolean shoot = false;
	int shootTimer = 0;
	
	public ShootEnemy(int pX, int pY){
		
		super(imgSource, pX, pY, Obstacle.SIZE, Obstacle.SIZE);
		velX = 5;
		
	}
	
	
	
	public void tick(){
		
		if(posX <= 0) velX = 5;
		if(posX >= Main.SCR_H) velX = -5;
		
		posX += velX;
	
		if(shoot) shoot = false;
		
		shootTimer++;
		if(shootTimer >= SH_TIME){
			shoot = true;
			shootTimer = 0;
		}
		
	}

}
